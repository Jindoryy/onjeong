package com.a503.onjeong.domain.userGame;

import com.a503.onjeong.domain.game.Game;

import com.a503.onjeong.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user_game")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_game_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "user_game_score")
    private Long userGameScore;

    @Builder
    public UserGame(
            User user,
            Game game,
            Long userGameScore
    ) {
        this.user = user;
        this.game = game;
        this.userGameScore = userGameScore;
    }

    // 유저의 최고기록을 갱신
    public void setUserGameScore(Long userGameScore){
        this.userGameScore = userGameScore;
    }

}
