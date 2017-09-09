package com.swen;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

public class StorageTest
{
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private NewsAPI mAPI;

    private void clearFiles() throws Exception
    {
        File[] subFiles = mActivityRule.getActivity().getApplicationContext().getFilesDir().listFiles();
        if (subFiles != null)
            for (File file : subFiles)
                if (file.getName().startsWith(Storage.FILE_PREFIX))
                    file.delete();
    }

    @Before
    public void setUp() throws Exception
    {
        mAPI = mock(NewsAPI.class);
        doAnswer(invocation -> {
            News news = new News();
            news.news_ID = (String)(invocation.getArguments()[0]);
            return news;
        }).when(mAPI).getNews(anyString());
        // The code above might cause Promise to fail, if lambda expressions are passed in within production code, for unknown reason

        clearFiles();
    }

    @After
    public void tearDown() throws Exception
    {
        clearFiles();
    }

    @Test
    public void testGetFromApplication() throws Exception
    {
        Storage storage = ((ApplicationWithStorage)(mActivityRule.getActivity().getApplication())).getStorage();
        assertTrue(storage.getMarked() != null); // might not be empty because there might already be files
    }

    @Test
    public void testRestoreWhenConstruct() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 10);
        storage.markSync("123");
        List<String> list = storage.getMarked();
        assertEquals(1, list.size());
        assertEquals("123", list.get(0));

        storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 10);
        list = storage.getMarked();
        assertEquals(1, list.size());
        assertEquals("123", list.get(0));
    }

    @Test
    public void testMemCache() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 10);
        storage.getNewsCached("123").then(news1 -> {
            assertEquals("123", news1.news_ID);
            storage.getNewsCached("123").then(news2 -> {
                assertEquals("123", news2.news_ID);
                verify(mAPI, times(1)).getNews("123");
                return new Object();
            }).waitUntilHasRun();
            return new Object();
        }).waitUntilHasRun();
    }

    @Test
    public void testOutOfCapacity() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 1);
        News news = storage.getNewsCachedSync("123");
        assertEquals("123", news.news_ID);
        news = storage.getNewsCachedSync("456");
        news = storage.getNewsCachedSync("123");
        assertEquals("123", news.news_ID);
        verify(mAPI, times(2)).getNews("123");
    }

    @Test
    public void testDiskStore() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 1);
        News news = storage.getNewsCachedSync("123");
        assertEquals("123", news.news_ID);
        storage.markSync("123");
        news = storage.getNewsCachedSync("456");
        news = storage.getNewsCachedSync("123");
        assertEquals("123", news.news_ID);
        verify(mAPI, times(1)).getNews("123");
    }

    @Test
    public void testUnmark() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 1);
        News news = storage.getNewsCachedSync("123");
        assertEquals("123", news.news_ID);
        storage.markSync("123");
        storage.unmarkSync("123");
        news = storage.getNewsCachedSync("456");
        news = storage.getNewsCachedSync("123");
        assertEquals("123", news.news_ID);
        verify(mAPI, times(2)).getNews("123");
    }

    @Test
    public void testDownloadWhenMark() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 1);
        storage.markSync("123");
        News news = storage.getNewsCachedSync("123");
        assertEquals("123", news.news_ID);
    }

    @Test
    public void testGetMarked() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, 10);
        storage.markSync("123");
        storage.markSync("456");
        storage.markSync("789");
        storage.unmarkSync("456");
        List<String> list = storage.getMarked();
        assertEquals(2, list.size());
        assertEquals("123", list.get(0));
        assertEquals("789", list.get(1));
    }
}