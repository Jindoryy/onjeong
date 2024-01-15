package com.a503.onjeong.domain.user.repository;

import com.a503.onjeong.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByKakaoId(Long kakaoId);
}
