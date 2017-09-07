package com.swen;

import android.support.test.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

public class StorageTest
{
    NewsAPI mAPI;

    @Before
    public void setUp() throws Exception
    {
        mAPI = mock(NewsAPI.class);
        doAnswer(invocation -> {
            News news = new News();
            news.news_ID = (String)(invocation.getArguments()[0]);
            return news;
        }).when(mAPI).getNews(anyString());
    }

    @Test
    public void testMemCache() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 10);
        News news = storage.getNewsCached("123");
        assertEquals("123", news.news_ID);
        storage.getNewsCached("123");
        assertEquals("123", news.news_ID);
        verify(mAPI, times(1)).getNews("123");
    }

    @Test
    public void testOutOfCapacity() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 1);
        News news = storage.getNewsCached("123");
        assertEquals("123", news.news_ID);
        storage.getNewsCached("456");
        storage.getNewsCached("123");
        assertEquals("123", news.news_ID);
        verify(mAPI, times(2)).getNews("123");
    }

    @Test
    public void testDiskStore() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 1);
        News news = storage.getNewsCached("123");
        assertEquals("123", news.news_ID);
        storage.mark("123");
        storage.getNewsCached("456");
        storage.getNewsCached("123");
        assertEquals("123", news.news_ID);
        verify(mAPI, times(1)).getNews("123");
    }

    @Test
    public void testUnmark() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 1);
        News news = storage.getNewsCached("123");
        assertEquals("123", news.news_ID);
        storage.mark("123");
        storage.unmark("123");
        storage.getNewsCached("456");
        storage.getNewsCached("123");
        assertEquals("123", news.news_ID);
        verify(mAPI, times(2)).getNews("123");
    }

    @Test
    public void testDownloadWhenMark() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 1);
        storage.mark("123");
        News news = storage.getNewsCached("123");
        assertEquals("123", news.news_ID);
    }

    @Test
    public void testGetMarked() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 10);
        storage.mark("123");
        storage.mark("456");
        storage.mark("789");
        storage.unmark("456");
        List<String> list = storage.getMarked();
        assertEquals(2, list.size());
        assertEquals("123", list.get(0));
        assertEquals("789", list.get(1));
    }
}