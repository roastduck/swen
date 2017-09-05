package com.swen;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class NewsTest
{
    private News mNews;

    @Before
    public void setUp() throws Exception
    {
        mNews = new News();
    }

    @Test
    public void testGetNewsClassTag() throws Exception
    {
        mNews.newsClassTag = "教育";
        assertEquals(News.Category.EDUCATION, mNews.getNewsClassTag());
    }

    @Test
    public void testGetNewsTime() throws Exception
    {
        mNews.news_Time = "20161213214510";
        Calendar ret = mNews.getNewsTime();
        assertEquals(2016, ret.get(Calendar.YEAR));
        assertEquals(11, ret.get(Calendar.MONTH)); // MONTH is 0-based
        assertEquals(13, ret.get(Calendar.DAY_OF_MONTH));
        assertEquals(21, ret.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, ret.get(Calendar.MINUTE));
        assertEquals(10, ret.get(Calendar.SECOND));
    }

    @Test
    public void testGetCrawlTime() throws Exception
    {
        mNews.crawl_Time = "20161213214510";
        Calendar ret = mNews.getCrawlTime();
        assertEquals(2016, ret.get(Calendar.YEAR));
        assertEquals(11, ret.get(Calendar.MONTH)); // MONTH is 0-based
        assertEquals(13, ret.get(Calendar.DAY_OF_MONTH));
        assertEquals(21, ret.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, ret.get(Calendar.MINUTE));
        assertEquals(10, ret.get(Calendar.SECOND));
    }
}
