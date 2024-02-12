package com.a503.onjeong.domain.game.dto;

import com.a503.onjeong.domain.game.Game;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
@NoArgsConstructor
@Data
public class GameDto {
    @NotNull
    private Long id;
    @NotNull
    private String name;

    @Builder
    public GameDto(
            Long id,
            String name
    ) {
        this.id = id;
        this.name = name;
    }



}
