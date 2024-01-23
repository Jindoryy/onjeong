package com.a503.onjeong.domain.news.service;

import com.a503.onjeong.domain.news.News;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface NewsService {
    List<News> getNewsDatas();
}