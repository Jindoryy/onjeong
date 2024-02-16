package com.a503.onjeong.global.auth.service;

import com.a503.onjeong.domain.group.Group;
import com.a503.onjeong.domain.group.repository.GroupRepository;
import com.a503.onjeong.domain.user.User;
import com.a503.onjeong.domain.user.UserType;
import com.a503.onjeong.domain.user.repository.UserRepository;
import com.a503.onjeong.global.auth.dto.*;
import com.a503.onjeong.global.exception.ExceptionCodeSet;
import com.a503.onjeong.global.exception.UserException;
import com.sun.jdi.InternalException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Random;


/* 인증, 인가 관련 서비스 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {
    WebClient webClient;
    private final UserRepository userRepository;
    private final KakaoService kakaoService;
    private final GroupRepository groupRepository;
    private static final String DefaultProfileImg = "https://allfriend.s3.ap-northeast-2.amazonaws.com/profile_img.png";
    @Value("${phone.API_KEY}")
    private String API_KEY;

    /* 배포시 수정 */
    @Value("${phone.API_SECRET_KEY}")
    private String API_SECRET_KEY;

    @Value("${phone.DOMAIN}")
    private String DOMAIN;

    @PostConstruct
    public void initWebClient() {
        //서버 배포시 서버에 할당된 IP로 변경 예정
        webClient = WebClient.create("http://localhost:8080");
    }

    /* 회원 가입 */
    @Override
    @Transactional
    public Long signup(String kakaoAccessToken, String kakaoRefreshToken,
                       String phoneNumber, HttpServletResponse response) {
        // 유저 정보 추출
        KakaoDto.UserInfo userInfo = kakaoService.getUserInfo(kakaoAccessToken, kakaoRefreshToken);

        User user = userRepository.findByKakaoId(userInfo.getKakaoId()).orElse(null);

        // 유저가 없으면 저장 (신규)
        if (user == null) {

            // 유저 생성
            User userOpt = User.builder()
                    .kakaoId(userInfo.getKakaoId())
                    .name(userInfo.getNickname())
                    .type(UserType.USER)
                    .build();

            user = userRepository.save(userOpt);


            //그룹 생성
            Group group = Group.builder()
                    .name("가족")
                    .ownerId(userOpt.getId())
                    .build();
            groupRepository.save(group);
        }

        // 전화번호 세팅
        user.setPhoneNumber(phoneNumber);
        // 카카오 리프레시 토큰 갱신
        user.setKakaoRefreshToken(userInfo.getKakaoRefreshToken());

        // 프로필 url 세팅
        if (userInfo.getProfileImageUrl() == null) {
            user.setProfileUrl(DefaultProfileImg);
        } else {
            user.setProfileUrl(userInfo.getProfileImageUrl());
        }
        return user.getId();
    }

    /* 로그인 (검증) */
    @Override
    @Transactional
    public LoginInfoResponseDto login(String kakaoAccessToken, Long userId, HttpServletResponse response) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        // 검증
        LoginFilterResponseDto responseDto =
                loginFilter(kakaoAccessToken, user.getKakaoRefreshToken(), user.getId()).getBody();

        user = userRepository.findById(responseDto.getUserId()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        // 카카오 액세스 토큰, JWT 액세스 토큰 발급
        response.setHeader(HttpHeaders.AUTHORIZATION, responseDto.getAccessToken());
        response.setHeader("Refresh-Token", responseDto.getRefreshToken());
        response.setHeader("Kakao-Access-Token", responseDto.getKakaoAccessToken());

        // 카카오 리프레시 토큰, JWT 리프레시 토큰 갱신
        user.setKakaoRefreshToken(responseDto.getKakaoRefreshToken());
        user.setRefreshToken(responseDto.getRefreshToken());

        LoginInfoResponseDto loginInfoResponseDto = LoginInfoResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .type(user.getType())
                .build();

        return loginInfoResponseDto;

    }

    /* 카카오 로그인 */
    @Override
    public KakaoDto.Token kakaoLogin(String code) {
        return kakaoService.getKakaoToken(code);
    }

    /* JWT 재발급 필터 호출하는 함수 */
    @Transactional
    public void reissueToken(String refreshToken, HttpServletResponse response) {
        ReissueFilterResponseDto responseDto = webClient.post()
                .uri("/reissue")
                .header("Refresh-Token", refreshToken)
                .retrieve()
                .toEntity(ReissueFilterResponseDto.class)
                .block()
                .getBody();

        User user = userRepository.findById(responseDto.getUserId()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        response.setHeader("Refresh-Token", responseDto.getRefreshToken());
        response.setHeader(HttpHeaders.AUTHORIZATION, responseDto.getAccessToken());

        user.setRefreshToken(responseDto.getRefreshToken());
    }

    /* AuthenticationManager가 User를 검증하는 함수 */
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.valueOf(id)).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return org.springframework.security.core.userdetails.User.builder()
                .username(id)
                .password(passwordEncoder.encode(String.valueOf(user.getId())))
                .authorities("BASIC")
                .build();
    }

    /* 로그인 필터 호출하는 함수 */
    public ResponseEntity<LoginFilterResponseDto> loginFilter(String kakaoAccessToken, String kakaoRefreshToken, Long userId) {

        LoginFilterRequestDto loginFilterRequestDto = LoginFilterRequestDto.builder()
                .kakaoAccessToken(kakaoAccessToken)
                .kakaoRefreshToken(kakaoRefreshToken)
                .userId(userId)
                .build();

        //필터 호출
        return webClient.post()
                .uri("/login")
                .body(Mono.just(loginFilterRequestDto), LoginFilterRequestDto.class)
                .retrieve()
                .toEntity(LoginFilterResponseDto.class)
                .block();
    }

    /* 전화 번호 인증 함수 */
    public String phoneVerification(String phoneNumber) {
        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY,
                API_SECRET_KEY, DOMAIN);

        Message message = new Message();
        String randomNumber = getRandomNumber();
        message.setFrom("01090443111");
        message.setTo(phoneNumber);
        message.setText("본인확인을 위해 인증번호 [" + randomNumber + "]을 입력해 주세요.");

        try {
            messageService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            throw new InternalException(exception.getMessage());
        } catch (Exception exception) {
            throw new InternalException(exception.getMessage());
        }
        return randomNumber;
    }

    private String getRandomNumber() {
        Random random = new Random(); // 랜덤 객체 생성
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
