package com.swen;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
        News news = JSON.parseObject("{\"newsClassTag\":\"教育\"}", News.class);
        assertEquals(News.Category.EDUCATION, news.getNewsClassTag());
    }

    @Test
    public void testGetNewsTime() throws Exception
    {
        mNews.setNews_Time("20161213214510");
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
        mNews.setCrawl_Time("20161213214510");
        Calendar ret = mNews.getCrawlTime();
        assertEquals(2016, ret.get(Calendar.YEAR));
        assertEquals(11, ret.get(Calendar.MONTH)); // MONTH is 0-based
        assertEquals(13, ret.get(Calendar.DAY_OF_MONTH));
        assertEquals(21, ret.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, ret.get(Calendar.MINUTE));
        assertEquals(10, ret.get(Calendar.SECOND));
    }

    @Test
    public void testGetNewsPictures() throws Exception
    {
        mNews.setNews_Pictures("http://news.xinhuanet.com/info/2015-10/26/134748860_14458157112331n.jpg http://news.xinhuanet.com/info/2015-10/26/134748860_14458157538451n.jpg http://news.xinhuanet.com/info/2015-10/26/134748860_14458157538551n.jpg http://news.xinhuanet.com/info/2015-10/26/134748860_14458157539251n.jpg http://news.xinhuanet.com/info/2015-10/26/134748860_14458157539351n.jpg");
        List<String> picList = mNews.getNewsPictures();
        assertEquals(5, picList.size());
    }

    @Test
    public void testSearchPicture() throws Exception
    {
        mNews.searchPicture("Twitter推出新闻标签功能：降低用户门槛").done(imgUrl -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) (new URL((String)imgUrl).openConnection());
                assertEquals(200, conn.getResponseCode());
                Map<String, List<String>> map = conn.getHeaderFields();
                assertTrue(map.get("Content-Type").get(0).contains("image"));
                Log.v("test", (String)imgUrl);
            }
            catch (MalformedURLException e) {}
            catch (IOException e) {}
        });
    }
}
