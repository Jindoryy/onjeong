package com.a503.onjeong.domain.news.controller;

import com.a503.onjeong.domain.news.News;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

<<<<<<< HEAD
@Controller
public interface NewsController{
    public List<News> newsList();
=======

public interface NewsController{
    public List<News> getNews() throws IOException;
>>>>>>> 85ca971af4c43f0b48c3898bb60ae544ba344aaf
}
