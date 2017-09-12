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
    public ActivityTestRule<RecommendationActivity> mActivityRule = new ActivityTestRule<>(RecommendationActivity.class);

    private NewsAPI mAPI;
    private StreamFactory mStreamFactory = spy(StreamFactory.class);

    final String IMG_URL = "http://news.xinhuanet.com/info/2015-10/26/134748860_14458157112331n.jpg";

    private void clearFiles() throws Exception
    {
        File[] subFiles = mActivityRule.getActivity().getApplicationContext().getFilesDir().listFiles();
        if (subFiles != null)
            for (File file : subFiles)
                if (file.getName().startsWith(Storage.NEWS_PREFIX) || file.getName().startsWith(Storage.IMG_PREFIX))
                    file.delete();
    }

    @Before
    public void setUp() throws Exception
    {
        mAPI = mock(NewsAPI.class);
        doAnswer(invocation -> {
            News news = new News();
            news.news_ID = (String)(invocation.getArguments()[0]);
            news.setNews_Pictures(IMG_URL);
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
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 10, 10);
        storage.markSync("123");
        List<String> list = storage.getMarked();
        assertEquals(1, list.size());
        assertEquals("123", list.get(0));

        storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 10, 10);
        list = storage.getMarked();
        assertEquals(1, list.size());
        assertEquals("123", list.get(0));
    }

    @Test
    public void testMemCache() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 10, 10);
        storage.getNewsCached("123").then(news1 -> {
            assertEquals("123", news1.news_ID);
            storage.getNewsCached("123").then(news2 -> {
                assertEquals("123", news2.news_ID);
                verify(mAPI, times(1)).getNews("123");
                return null;
            }).waitUntilHasRun();
            return null;
        }).waitUntilHasRun();
    }

    @Test
    public void testOutOfCapacity() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 1, 10);
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
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 1, 10);
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
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 1, 10);
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
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 1, 10);
        storage.markSync("123");
        News news = storage.getNewsCachedSync("123");
        assertEquals("123", news.news_ID);
    }

    @Test
    public void testGetMarked() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 10, 10);
        storage.markSync("123");
        storage.markSync("456");
        storage.markSync("789");
        storage.unmarkSync("456");
        List<String> list = storage.getMarked();
        assertEquals(2, list.size());
        assertEquals("123", list.get(0));
        assertEquals("789", list.get(1));
    }

    @Test
    public void testGetPic() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 10, 10);
        storage.getPicCached(IMG_URL).then(img -> {
            verify(mStreamFactory, times(1)).fromUrl(IMG_URL);
            return null;
        }).waitUntilHasRun();
    }

    @Test
    public void testPicMemCache() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 10, 10);
        storage.getPicCachedSync(IMG_URL);
        storage.getPicCachedSync(IMG_URL);
        verify(mStreamFactory, times(1)).fromUrl(IMG_URL);
    }

    @Test
    public void testPicMark() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 10, 0);
        storage.markSync("123");
        verify(mStreamFactory, times(1)).fromUrl(IMG_URL);
        storage.getPicCachedSync(IMG_URL);
        verify(mStreamFactory, times(1)).fromUrl(IMG_URL);
    }

    @Test
    public void testPicUnmark() throws Exception
    {
        Storage storage = new Storage(InstrumentationRegistry.getTargetContext(), mAPI, mStreamFactory, 10, 0);
        storage.markSync("123");
        verify(mStreamFactory, times(1)).fromUrl(IMG_URL);
        storage.unmarkSync("123");
        storage.getPicCachedSync(IMG_URL);
        verify(mStreamFactory, times(2)).fromUrl(IMG_URL);
    }
}