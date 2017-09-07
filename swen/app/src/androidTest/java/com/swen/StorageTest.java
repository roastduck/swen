package com.swen;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

public class StorageTest
{
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private NewsAPI mAPI;

    @Before
    public void setUp() throws Exception
    {
        mAPI = mock(NewsAPI.class);
        /*doAnswer(invocation -> {
            News news = new News();
            news.news_ID = (String)(invocation.getArguments()[0]);
            return news;
        }).when(mAPI).getNews(anyString());*/
        // The code above will cause AppendableNewsListTest to fail for unknown reason
        News news123 = new News(), news456 = new News(), news789 = new News();
        news123.news_ID = "123"; doReturn(news123).when(mAPI).getNews("123");
        news456.news_ID = "456"; doReturn(news456).when(mAPI).getNews("456");
        news789.news_ID = "789"; doReturn(news789).when(mAPI).getNews("789");

    }

    @Test
    public void testGetFromApplication() throws Exception
    {
        Storage storage = ((ApplicationWithStorage)(mActivityRule.getActivity().getApplication())).getStorage();
        assertTrue(storage.getMarked().isEmpty());
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