package com.a503.onjeong.domain.news.repository;

import com.a503.onjeong.domain.news.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

//    Optional<News> findByCategory(int category);

}
