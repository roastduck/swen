package com.swen;

public class NewsAPI
{
    public enum Category
    {
        // TODO
    }

    public NewsList getList(int pageNo, int pageSize) {}

    public NewsList getList(int pageNo, int pageSize, String keyword) {}

    public NewsList getList(int pageNo, int pageSize, String keyword, Category category) {}

    public News getNews(String id) {}
}
