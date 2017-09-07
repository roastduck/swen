package com.swen;

import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

public class AppendableNewsListTest
{
    NewsAPI mAPI;
    Behavior mBehavior;

    @Before
    public void setUp() throws Exception
    {
        NewsList ret1 = new NewsList();
        ret1.list = new Vector<>();
        News news1 = new News(), news2 = new News();
        news1.news_Title = "lower";
        news2.news_Title = "higher";
        ret1.list.add(news1);
        ret1.list.add(news2);
        ret1.pageNo = 1;
        ret1.pageSize = 2;
        ret1.totalPages = 1;
        ret1.totalRecords = 2;

        mAPI = mock(NewsAPI.class);
        doReturn(ret1).when(mAPI).getList(eq(1), anyInt(), anyString(), any(News.Category.class));
        doReturn(ret1).when(mAPI).getList(eq(1), anyInt());
        doReturn(ret1).when(mAPI).getList(eq(1), anyInt(), anyString());
        doReturn(ret1).when(mAPI).getList(eq(1), anyInt(), any(News.Category.class));

        mBehavior = mock(Behavior.class);
        doReturn(1.0).when(mBehavior).getPreference(news1);
        doReturn(2.0).when(mBehavior).getPreference(news2);
    }

    @Test
    public void testAppend() throws Exception
    {
        AppendableNewsList appendable = new AppendableNewsList(2, "keyword", News.Category.CAR, false, null, mAPI);
        assertEquals(0, appendable.pageNo);
        assertEquals(0, appendable.list.size());

        appendable.append().done(o -> {
            try
            {
                verify(mAPI).getList(1, 2, "keyword", News.Category.CAR);
            } catch (Exception ignored) {}
            assertEquals(1, appendable.pageNo);
            assertEquals(2, appendable.list.size());
        });
    }

    @Test
    public void testRecommend() throws Exception
    {
        AppendableNewsList appendable = new AppendableNewsList(1, "keyword", News.Category.CAR, true, mBehavior, mAPI);
        appendable.append().done(o -> {
            try
            {
                verify(mAPI).getList(1, 10, "keyword", News.Category.CAR);
            } catch (Exception ignored) {}
            assertEquals(1, appendable.list.size());
            assertEquals("higher", appendable.list.get(0).news_Title);
        });
    }
}