package com.a503.onjeong.domain.userGame.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
@NoArgsConstructor
@Data
public class UserGameDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long gameId;
    private Long userGameScore;

    @Builder
    public UserGameDto(
            Long id,
            Long userId,
            String userName,
            Long gameId,
            Long userGameScore
    ) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.gameId = gameId;
        this.userGameScore = userGameScore;
    }

}
