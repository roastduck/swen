package com.java.g15;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Teon on 2017/9/10.
 */

public class DemonstratedContent {
    //TODO:新闻达到上限的处理
    public News news;
    public News rightNews;
    public int style;

    public DemonstratedContent(News news, int style) {
        this.news = news;
        this.style = style;
    }

    public DemonstratedContent(News news, News rightNews, int style) {
        this.news = news;
        this.rightNews = rightNews;
        this.style = style;
    }

    public static List<DemonstratedContent> getDemonstratedContent(List<News> news, Random random) {
        List<DemonstratedContent> list = new ArrayList<>();
        updateDemonstratedContent(news, list, 0, random);
        return list;
    }

    public static void updateDemonstratedContent(List<News> news, List<DemonstratedContent> res,
                                                 int start, Random random) {
        for(int position = start; position < news.size(); position++) {
            List<String> pictures = news.get(position).getNewsPictures();
//            if (pictures.isEmpty()) {
//                continue;
//            }
            if (res.isEmpty()) {     //First item in view
                res.add(new DemonstratedContent(news.get(position), 1));
                continue;
            }
            if (pictures.size()
                >= 5) {      //If this news contains many pictures, then we try to show more of them
                res.add(new DemonstratedContent(news.get(position), 4));
                continue;
            }
            int style;
            if (pictures.size() >= 3) {
                style = random.nextInt(4) + 1;
            } else {
                style = random.nextInt(3) + 1;
            }
            if (style == 3) {
                if(position != news.size() - 1) {
                    res.add(new DemonstratedContent(news.get(position),
                        news.get(position + 1), 3));
                    position++;
                    continue;
                }
                style = 1;
            }
            res.add(new DemonstratedContent(news.get(position), style));
        }
    }
}