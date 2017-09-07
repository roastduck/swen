package com.swen;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class News implements Serializable
{
    // NOTE: All properties that will be written by fastjson must be public or with a setter provided.
    // NOTE: This object cannot be serialized back the same as original API response with fastjson.

    public static class WeightedKeyword implements Serializable
    {
        public String word;
        public double score;
    }

    // TODO: What's this? Counting? If you know, write down here.
    public static class WordBag implements Serializable
    {
        public String word;
        public int score;
    }

    public static class WordCnt implements Serializable
    {
        public String word;
        public int count;
    }

    public enum Category
    {
        TECH(1), EDUCATION(2), MILITARY(3), DOMESTIC(4), SOCIETY(5), CULTURE(6),
        CAR(7), INTERNATIONAL(8), SPORT(9), FINANCE(10), HEALTH(11), ENTERTAIN(12);

        private int id;

        static class InvalidCategoryException extends Exception {}

        /** Constructing from int is private (default for enum) for safety issues
         *  Please store a category in enum instead of int
         */
        Category(int id) { this.id = id; }

        /** Get category id
         *  Used for preparing request
         */
        public int getId() { return id; }

        /** Factory method
         *  @param s : e.g. "科技". NOTE: This corresponds to News.newsClassTag, NOT to News.news_Category
         */
        public static Category fromString(String s) throws InvalidCategoryException
        {
            switch (s)
            {
                case "科技": return TECH;
                case "教育": return EDUCATION;
                case "军事": return MILITARY;
                case "国内": return DOMESTIC;
                case "社会": return SOCIETY;
                case "文化": return CULTURE;
                case "汽车": return CAR;
                case "国际": return INTERNATIONAL;
                case "体育": return SPORT;
                case "财经": return FINANCE;
                case "健康": return HEALTH;
                case "娱乐": return ENTERTAIN;
                default: throw new InvalidCategoryException();
            }
        }
    }

    public String lang_Type; // e.g. ":"zh-CN"
    private String newsClassTag; // e.g. "科技". Please use getter.
    public String news_Author;
    public String news_ID; // e.g. "20160913041301d5fc6a41214a149cd8a0581d3a014f"
    public String news_Pictures; // URLs
    public String news_Source; // e.g. "新浪新闻"
    private String news_Time; // e.g. "20160912000000"  Please use getter
    public String news_Title;
    public String news_URL;
    public String news_Video;
    public String news_Intro;
    // Properties above are returned in `NewsAPI.list`

    public List<WeightedKeyword> Keywords;
    public List<WordBag> bagOfWords;
    public String crawl_Source; // A host name
    private String crawl_Time; // Please use getter
    public String inborn_KeyWords; // TODO: What's this then?
    public List<WordCnt> locations;
    public String news_Category; // e.g. "首页 > 新闻 > 环球扫描 > 正文"
    public String news_Content;
    public String news_Journal;
    public List<WordCnt> organizations;
    public List<WordCnt> persons;
    public String repeat_ID; // TODO: What's this?
    public List<String> seggedPListOfContent;
    public String seggedTitle; // e.g. "德/n 媒/g ：/w 俄/b 柔道/n 运动员/n 里约/LOC 夺金/vn 与/cc 普京/PER 密切相关/n "
    public int wordCountOfContent;
    public int wordCountOfTitle;

    // This should be a static variable, because News object will be cached and stored
    private static Set<String> alreadyRead = new HashSet<>();

    public boolean isAlreadyRead() {
        return alreadyRead.contains(news_ID);
    }

    public void setAlreadyRead(boolean alreadyRead) {
        if (alreadyRead)
            this.alreadyRead.add(news_ID);
        else
            this.alreadyRead.remove(news_ID);
    }

    public void setNewsClassTag(String newsClassTag) { this.newsClassTag = newsClassTag; }

    public Category getNewsClassTag() throws Category.InvalidCategoryException
    {
        return Category.fromString(newsClassTag);
    }

    public void setNews_Time(String news_Time) { this.news_Time = news_Time; }
    public void setCrawl_Time(String crawl_Time) { this.crawl_Time = crawl_Time; }

    // NOTE: MONTH is 0-based
    public Calendar getNewsTime() { return parseDateTime(news_Time); }
    public Calendar getCrawlTime() { return parseDateTime(crawl_Time); }

    /** Parse date and time from the digital string of the API
     *  @return A Calendar object with default time zone, because LocalDateTime is not available in old Androids
     */
    private Calendar parseDateTime(String s)
    {
        Calendar ret = Calendar.getInstance();
        ret.set(
                Integer.parseInt(s.substring(0, 4)),
                Integer.parseInt(s.substring(4, 6)) - 1, // MONTH is 0-based
                Integer.parseInt(s.substring(6, 8)),
                Integer.parseInt(s.substring(8, 10)),
                Integer.parseInt(s.substring(10, 12)),
                Integer.parseInt(s.substring(12, 14))
        );
        return ret;
    }
}
