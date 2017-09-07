package com.swen;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Vector;
import java.util.stream.Collectors;

/** This news list keeps the metadata of how to append itself
 */
public class AppendableNewsList extends NewsList
{
    public final String keyword;
    public final News.Category category;
    public final boolean isRecommend;

    private final int RECOMMEND_FACTOR = 10; /// Find recommended news from 10x pieces of news

    private NewsAPI mAPI;
    private Behavior mBehavior;

    /** Construct a new Extendable NewsList
     *  This is a overload function with isRecommend = false
     *  @param pageSize : How many new pieces of news to get each time
     *  @param keyword : Keyword to be searched. Null for not to search
     *  @param category : Category to be filtered out. Null for not to filter
     */
    public AppendableNewsList(int pageSize, String keyword, News.Category category)
    {
        this(pageSize, keyword, category, false, null);
    }

    /** Construct a new Extendable NewsList
     *  @param pageSize : How many new pieces of news to get each time
     *  @param keyword : Keyword to be searched. Null for not to search
     *  @param category : Category to be filtered out. Null for not to filter
     *  @param isRecommend : Select news with high preference when set to true
     *  @param behavior : Must not be null is isRecommend = true
     */
    public AppendableNewsList(int pageSize, String keyword, News.Category category, boolean isRecommend, Behavior behavior)
    {
        this(pageSize, keyword, category, isRecommend, behavior, new NewsAPI());
    }

    AppendableNewsList(int pageSize, String keyword, News.Category category, boolean isRecommend, Behavior behavior, NewsAPI api)
    {
        this.list = new Vector<>();
        this.pageNo = 0; // Valid pageNo starts from 1
        this.pageSize = pageSize;
        this.totalPages = this.totalRecords = Integer.MAX_VALUE;
        this.keyword = keyword;
        this.category = category;
        this.isRecommend = isRecommend;
        this.mBehavior = behavior;
        this.mAPI = api;
    }

    /** Fetch a new page and append to itself
     *  You can refer to the unit test as an example of how to use Promise
     *  When fail, it throws IOException (ues .fail to catch) , and guarantee nothing will be changed
     */
    public Promise append()
    {
        final Deferred deferred = new DeferredObject();
        deferred.resolve(new Object()); // must
        return deferred.promise().then(new DoneCallback() {
            // For unknown reason, there will be a error when replaced with a lambda-expression
            // Error message: java.lang.NoSuchMethodError: No direct method <init>(Ljava/lang/Object;Ljava/lang/Object;)V in class Lcom/swen/-$Lambda$0; or its super classes (declaration of 'com.swen.-$Lambda$0' appears in /data/app/com.swen.test-1/base.apk)
            @Override
            public void onDone(Object o)
            {
                try
                {
                    AppendableNewsList.this.appendSync();
                } catch (IOException e)
                {
                    deferred.reject(e);
                }
            }
        });
    }

    /** Synchronized version of append
     */
    public synchronized void appendSync() throws IOException
    {
        int fetchPageSize = (isRecommend ? RECOMMEND_FACTOR : 1) * pageSize;
        NewsList fetched;
        if (keyword == null)
            if (category == null)
                fetched = mAPI.getList(pageNo + 1, fetchPageSize);
            else
                fetched = mAPI.getList(pageNo + 1, fetchPageSize, category);
        else
            if (category == null)
                fetched = mAPI.getList(pageNo + 1, fetchPageSize, keyword);
            else
                fetched = mAPI.getList(pageNo + 1, fetchPageSize, keyword, category);
        if (fetched.list.isEmpty())
            return;

        // Now the throwable method has been called, then we can change properties
        if (isRecommend)
        {
            ComparableNews tmpArr[] = new ComparableNews[fetched.list.size()];
            for (int i = 0; i < tmpArr.length; i++)
                tmpArr[i] = new ComparableNews(fetched.list.get(i));
            Arrays.sort(tmpArr);
            fetched.list = new Vector<>(pageSize);
            for (int i = 0; i < pageSize; i++)
                fetched.list.add(tmpArr[i].mNews);
        }

        pageNo ++;
        list.addAll(fetched.list);
    }

    private class ComparableNews implements Comparable<ComparableNews>
    {
        private double score;
        News mNews;

        ComparableNews(News news)
        {
            mNews = news;
            score = -mBehavior.getPreference(news);
        }

        @Override
        public int compareTo(ComparableNews o)
        {
            double diff = this.score - o.score;
            if (diff < 0) return -1;
            if (diff > 0) return 1;
            return 0;
        }
    }
}
