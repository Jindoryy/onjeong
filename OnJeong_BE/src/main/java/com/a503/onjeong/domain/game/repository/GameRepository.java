package com.a503.onjeong.domain.game.repository;

import com.a503.onjeong.domain.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
}
