package com.a503.onjeong.domain.news.controller;

import com.a503.onjeong.domain.news.News;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

@Controller
public interface NewsController{
    public List<News> newsList();
}
