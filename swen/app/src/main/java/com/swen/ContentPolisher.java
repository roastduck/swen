package com.swen;

public class ContentPolisher
{
    public static void addHref(News news) {
        if(news.locations != null) {
            for (News.WordCnt wordCnt : news.locations) {
                String word = wordCnt.word;
                String replacement = "<a href=\"https://baike.baidu.com/item/"
                                     + word + "\">" + word + "</a>";
                news.news_Content = news.news_Content.replaceAll(word, replacement);
            }
        }
        if(news.persons != null) {
            for (News.WordCnt wordCnt : news.persons) {
                String word = wordCnt.word;
                String replacement = "<a href=\"https://baike.baidu.com/item/"
                                     + word + "\">" + word + "</a>";
                news.news_Content = news.news_Content.replaceAll(word, replacement);
            }
        }
        if(news.organizations != null) {
            for (News.WordCnt wordCnt : news.organizations) {
                String word = wordCnt.word;
                String replacement = "<a href=\"https://baike.baidu.com/item/"
                                     + word + "\">" + word + "</a>";
                news.news_Content = news.news_Content.replaceAll(word, replacement);
            }
        }
    }
}
