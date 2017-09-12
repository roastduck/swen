package com.swen;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ContentPolisher
{
    public static void addHref(News news) {
        int i = 0;
        ArrayList<String> keywords = new ArrayList<>();
        for(News.WeightedKeyword w: news.Keywords) {
            keywords.add(w.word);
        }
        if(news.locations != null) {
            for (News.WordCnt wordCnt : news.locations) {
                String word = wordCnt.word;
                String replacement = "<a href=\"https://baike.baidu.com/item/"
                                     + word + "\">" + word + "</a>";
                news.news_Content = news.news_Content.replaceFirst(word, replacement);
            }
        }
        if(news.persons != null) {
            for (News.WordCnt wordCnt : news.persons) {
                String word = wordCnt.word;
                if(!keywords.contains(word)) {
                    continue;
                }
                String replacement = "<a href=\"https://baike.baidu.com/item/"
                                     + word + "\">" + word + "</a>";
                news.news_Content = news.news_Content.replaceFirst(word, replacement);
            }
        }
        if(news.organizations != null) {
            for (News.WordCnt wordCnt : news.organizations) {
                String word = wordCnt.word;
                String replacement = "<a href=\"https://baike.baidu.com/item/"
                                     + word + "\">" + word + "</a>";
                news.news_Content = news.news_Content.replaceFirst(word, replacement);
            }
        }
    }

}
