package com.swen;

import java.util.List;

public class News
{
    public class WeightedKeyword
    {
        String word;
        double score;
    }

    public class WordBag // TODO: What's this? Counting? If you know, write down here.
    {
        String word;
        int score;
    }

    public class WordCnt
    {
        String word;
        int count;
    }

    public String lang_Type; // e.g. ":"zh-CN"
    public String newsClassTag; // e.g. "科技"
    public String news_Author;
    public String news_ID; // e.g. "20160913041301d5fc6a41214a149cd8a0581d3a014f"
    public String news_Pictures; // A url
    public String news_Source; // e.g. "新浪新闻"
    public String news_Time; // e.g. "20160912000000" TODO: A getter can be added to return a LocalDateTime object
    public String news_Title;
    public String news_URL;
    public String news_Video;
    public String news_Intro;
    // Properties above are returned in `NewsAPI.list`

    public List<WeightedKeyword> Keywords;
    public List<WordBag> bagOfWords;
    public String crawl_Source; // A host name
    public String crawl_Time; // TODO: can be converted to a date-time format as above
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
}
