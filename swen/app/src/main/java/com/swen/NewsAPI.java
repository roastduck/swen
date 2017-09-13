package com.swen;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Scanner;

public class NewsAPI
{
    private StreamFactory mStreamFactory;

    private final String HOST = "http://166.111.68.66:2042";

    public NewsAPI()
    {
        this(new StreamFactory());
    }

    NewsAPI(StreamFactory streamFactory)
    {
        mStreamFactory = streamFactory;
    }

    public NewsList getList(int pageNo, int pageSize) throws IOException
    {
        String url = String.format((Locale)null, "/news/action/query/latest?pageNo=%d&pageSize=%d",
                pageNo, pageSize
        );
        return JSON.parseObject(readRaw(HOST + url), NewsList.class);
    }

    public NewsList getList(int pageNo, int pageSize, News.Category category) throws IOException
    {
        String url = String.format((Locale)null, "/news/action/query/latest?pageNo=%d&pageSize=%d&category=%d",
                pageNo, pageSize, category.getId()
        );
        return JSON.parseObject(readRaw(HOST + url), NewsList.class);
    }

    public NewsList getList(int pageNo, int pageSize, String keyword) throws IOException
    {
        String url = String.format((Locale)null, "/news/action/query/search?keyword=%s&pageNo=%d&pageSize=%d",
                keyword, pageNo, pageSize
        );
        return JSON.parseObject(readRaw(HOST + url), NewsList.class);
    }

    public NewsList getList(int pageNo, int pageSize, String keyword, News.Category category) throws IOException
    {
        String url = String.format((Locale)null, "/news/action/query/search?keyword=%s&pageNo=%d&pageSize=%d&category=%d",
                keyword, pageNo, pageSize, category.getId()
        );
        return JSON.parseObject(readRaw(HOST + url), NewsList.class);
    }

    public News getNews(String id) throws IOException
    {
        String raw = readRaw(HOST + "/news/action/query/detail?newsId=" + id);
        return JSON.parseObject(raw, News.class);
    }

    private String readRaw(String url) throws IOException
    {
        return (new Scanner(mStreamFactory.fromUrl(url))).useDelimiter("\\Z").next();
    }
}

/** This class is split out here in order to be mocked
 */
class StreamFactory
{
    InputStream fromUrl(String url) throws IOException // Mockito doesn't mock static methods
    {
        URLConnection conn = new URL(url).openConnection();
        conn.setConnectTimeout(500);
        conn.setReadTimeout(1000);
        return conn.getInputStream();
    }
}
