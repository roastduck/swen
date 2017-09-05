package com.swen;

public class Parser
{
    public void parse(News news) {
        for(News.WordCnt wordCnt: news.locations) {
            String word = wordCnt.word;
            String replacement = "<a href=\"https://baike.baidu.com/item/"
                                 + word + "\">" + word + "</a>";
            news.news_Content = news.news_Content.replaceAll(word, replacement);
        }
        for(News.WordCnt wordCnt: news.persons) {
            String word = wordCnt.word;
            String replacement = "<a href=\"https://baike.baidu.com/item/"
                                 + word + "\">" + word + "</a>";
            news.news_Content = news.news_Content.replaceAll(word, replacement);
        }
        for(News.WordCnt wordCnt: news.organizations) {
            String word = wordCnt.word;
            String replacement = "<a href=\"https://baike.baidu.com/item/"
                                 + word + "\">" + word + "</a>";
            news.news_Content = news.news_Content.replaceAll(word, replacement);
        }
    }
}
