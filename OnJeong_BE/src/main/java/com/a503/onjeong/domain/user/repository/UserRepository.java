package com.a503.onjeong.domain.user.repository;

import com.a503.onjeong.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long userId);

    boolean existsById(Long userId);

    Optional<User> findByKakaoId(Long kakaoId);

    @Query(value = "SELECT user FROM User as user WHERE user.phoneNumber IN (:numbers)")
    List<User> findByPhoneBook(@Param("numbers") List<String> numbers); // 연락처에 있는 전화번호를 가지고 있는 모든 유저 객체 반환

}
