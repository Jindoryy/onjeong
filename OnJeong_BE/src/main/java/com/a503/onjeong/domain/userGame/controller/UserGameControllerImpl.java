package com.a503.onjeong.domain.userGame.controller;

import com.a503.onjeong.domain.userGame.UserGame;
import com.a503.onjeong.domain.userGame.dto.UserGameDto;
import com.a503.onjeong.domain.userGame.repository.UserGameRepository;
import com.a503.onjeong.domain.userGame.service.UserGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userGame")
public class UserGameControllerImpl implements UserGameController {

    private final UserGameService userGameService;

    @GetMapping("/lists")
    // 해당 게임에 해당하는 유저들의 리스트 반환(userId와 score쓸거임)
    public List<UserGameDto> topScoreList(@RequestParam Long gameId) {
        return userGameService.userGameList(gameId);
    }

    @PostMapping
    // 해당 게임정보를 반환해서 있으면 update , 없으면 save 진행
    public UserGameDto scoreSave(@RequestBody UserGameDto userGameDto) {
        return userGameService.saveScore(userGameDto);
    }

    @GetMapping("/details")
    // 해당 유저의 게임 정보를 반환
    public UserGameDto scoreDetails(@RequestParam Long userId, Long gameId) {
        return userGameService.userGameDetails(userId, gameId);
    }

}
