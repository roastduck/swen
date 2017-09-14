package com.java.g15;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Teon Sean on 2017/9/5.
 */
public class BehaviorTest {

    private Behavior mBehavior;
    private Context mContext;
    private News news;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getContext();
        mBehavior = new Behavior(mContext);
        news = new News();
        news.news_Content = "　　【环球网综合报道】据俄罗斯新闻频道8月7日消息，德国《世界报》评论俄罗斯在里约奥运会获得其首枚金牌时称，俄罗斯柔道选手夺得金牌与俄罗斯总统弗拉基米尔·普京密切相关。 　　文章称，俄首枚金牌得主、柔道运动员穆德拉诺夫不止一次与俄罗斯总统弗拉基米尔·普京一起训练。柔道正是普京喜欢的体育运动，因此此次夺冠对俄罗斯来说非常重要。报道还提到，普京作为国际柔道联合会荣誉主席，已获柔道8段。 　　穆德拉诺夫本人也表示，这枚金牌意义重大。他说：“俄罗斯承受了最大的心理压力，但我们准备好了，也没有垮掉。”他称，还有很多值得尊敬的运动员本可以为俄罗斯获得最高奖励，但被禁赛了。他说，这不是俄罗斯的最后一枚金牌。 　　此前国际奥委会就因兴奋剂丑闻而完全取消俄罗斯代表团的参赛资格问题讨论了数周，最终共有271名俄罗斯运动员被允许参加此次奥运会。(实习编译：易武平 审稿：翟潞曼)";
        news.locations = new ArrayList<>();
        news.persons = new ArrayList<>();
        News.WordCnt test1 = new News.WordCnt();
        test1.word = "里约";
        News.WordCnt test2 = new News.WordCnt();
        test2.word = "俄罗斯";
        News.WordCnt test3 = new News.WordCnt();
        test3.word = "德国";
        News.WordCnt test4 = new News.WordCnt();
        test4.word = "弗拉基米尔";
        News.WordCnt test5 = new News.WordCnt();
        test5.word = "穆德拉诺夫";
        News.WordCnt test6 = new News.WordCnt();
        test6.word = "普京";
        news.locations.add(test1);
        news.locations.add(test2);
        news.locations.add(test3);
        news.persons.add(test4);
        news.persons.add(test5);
        news.persons.add(test6);
        news.setNewsClassTag("体育");
    }

    @Test
    public void markHaveRead() throws Exception {
        mBehavior.markHaveRead(news);
        Assert.assertEquals(2, mBehavior.getCategoryPreference(9));
    }

    @Test
    public void getPreference() throws Exception {
        double score = mBehavior.getPreference(news);
    }
}