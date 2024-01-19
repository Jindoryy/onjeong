package com.a503.onjeong.domain.user.repository;

import com.a503.onjeong.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long userId);

    Optional<User> findByKakaoId(Long kakaoId);

}
