package com.a503.onjeong.domain.userGame.service;

import com.a503.onjeong.domain.game.repository.GameRepository;
import com.a503.onjeong.domain.user.repository.UserRepository;
import com.a503.onjeong.domain.userGame.UserGame;
import com.a503.onjeong.domain.userGame.dto.UserGameDto;
import com.a503.onjeong.domain.userGame.repository.UserGameRepository;
import com.a503.onjeong.global.exception.ExceptionCodeSet;
import com.a503.onjeong.global.exception.GameException;
import com.a503.onjeong.global.exception.UserException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGameServiceImpl implements UserGameService {

    private final UserGameRepository userGameRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    // 랭킹 리스트 점수 top10개 반환
    @Override
    @Transactional
    // gameId에 해당하는 유저들의 score를 불러와서 10개만
    // userGameDto로 전환후 return
    public List<UserGameDto> userGameList(Long gameId) {
        List<UserGame> tmpList = userGameRepository.findByGameIdOrderByUserGameScoreDesc(gameId);
        List<UserGame> userGameList = new ArrayList<>();
        long size = Math.min(10, tmpList.size());
        for(int i = 0;i<size;i++){
            userGameList.add(tmpList.get(i));
        }
        List<UserGameDto> userGameDtoList = userGameList.stream()
                .map(userGame -> UserGameDto.builder()
                        .id(userGame.getId())
                        .userId(userGame.getUser().getId())
                        .userName(userGame.getUser().getName())
                        .gameId(userGame.getGame().getId())
                        .userGameScore(userGame.getUserGameScore())
                        .build())
                .collect(Collectors.toList());

        return userGameDtoList;
    }

    @Override
    @Transactional
    // 입력되는 정보로 저장된 데이터가 없으면 save , 저장된 최고점수보다 높으면 update 진행
    public UserGameDto saveScore(UserGameDto userGameDto) {
        UserGame userGame = userGameRepository.findByUserIdAndGameId(userGameDto.getUserId(), userGameDto.getGameId());
        if (userGame == null) {
            userGame = UserGame.builder()
                    .user(userRepository.findById(userGameDto.getUserId()).orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)))
                    .game(gameRepository.findById(userGameDto.getGameId()).orElseThrow(() -> new GameException(ExceptionCodeSet.GAME_NOT_FOUND)))
                    .userGameScore(userGameDto.getUserGameScore())
                    .build();
            userGameRepository.save(userGame);
        } else if (userGame.getUserGameScore() < userGameDto.getUserGameScore()) {
            userGame.setUserGameScore(userGameDto.getUserGameScore());
        }
        return userGameDetails(userGameDto.getUserId() , userGameDto.getGameId());
    }

    @Override
    // userId와 gameId에 해당하는 유저 정보 반환
    public UserGameDto userGameDetails(Long userId, Long gameId) {
        UserGame userGame = userGameRepository.findByUserIdAndGameId(userId, gameId);
        UserGameDto userGameDto = UserGameDto.builder()
                .id(userGame.getId())
                .userId(userGame.getUser().getId())
                .userName(userGame.getUser().getName())
                .gameId(userGame.getGame().getId())
                .userGameScore(userGame.getUserGameScore())
                .build();
        return userGameDto;
    }


}
