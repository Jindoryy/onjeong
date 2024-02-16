package com.a503.onjeong.domain.userGame.controller;

import com.a503.onjeong.domain.userGame.UserGame;
import com.a503.onjeong.domain.userGame.dto.UserGameDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public interface UserGameController {
     List<UserGameDto> topScoreList(@RequestParam Long gameId);

     UserGameDto scoreSave(@RequestBody UserGameDto userGameDto);

     UserGameDto scoreDetails(@RequestParam Long userId, Long gameId);


    }
