package com.swen;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;

import static org.junit.Assert.*;

public class NewsAPITest
{
    private NewsAPI mNewsAPI;
    private StreamFactory mockStreamFactory;

    private void setMockRet(String url, String ret) throws Exception
    {
        doReturn(new ByteArrayInputStream(ret.getBytes(Charset.forName("utf-8")))).when(mockStreamFactory).fromUrl(url);
    }

    @Before
    public void setUp() throws Exception
    {
        mockStreamFactory = mock(StreamFactory.class);
        doThrow(new IOException()).when(mockStreamFactory).fromUrl(anyString());
        setMockRet(
                "http://166.111.68.66:2042/news/action/query/latest?pageNo=1&pageSize=1",
                "{\"list\":[{\"lang_Type\":\"zh-CN\",\"newsClassTag\":\"科技\",\"news_Author\":\"创事记 微博 作者： 广州阿超\",\"news_ID\":\"20160913041301d5fc6a41214a149cd8a0581d3a014f\",\"news_Pictures\":\"\",\"news_Source\":\"新浪新闻\",\"news_Time\":\"20160912000000\",\"news_Title\":\"iPhone 7归来，友商们吊打苹果的姿势正确吗？\",\"news_URL\":\"http://tech.sina.com.cn/zl/post/detail/mobile/2016-09-12/pid_8508491.htm\",\"news_Video\":\"\",\"news_Intro\":\"　　欢迎关注“创事记”的微信订阅号：sinachuangshiji 文/罗超...\"}],\"pageNo\":1,\"pageSize\":1,\"totalPages\":1373727,\"totalRecords\":1373727}"
        );
        setMockRet(
                "http://166.111.68.66:2042/news/action/query/latest?pageNo=1&pageSize=1&category=4",
                "{\"list\":[{\"lang_Type\":\"zh-CN\",\"newsClassTag\":\"国内\",\"news_Author\":\"来源：成都日报\",\"news_ID\":\"2016091304130fe65ea1feec44218427271d68cff98d\",\"news_Pictures\":\"  \",\"news_Source\":\"国际在线\",\"news_Time\":\"20160912000000\",\"news_Title\":\"国家使命成都担当 建国家中心城市的\\\"成都方略\\\"\",\"news_URL\":\"http://sc.cri.cn/549/2016/09/12/341s59028.htm\",\"news_Video\":\"\",\"news_Intro\":\"　　原标题：建设国家中心城市 国家使命 成都担当 　　解读建设国家中心城市的...\"}],\"pageNo\":1,\"pageSize\":1,\"totalPages\":168534,\"totalRecords\":168534}"
        );
        setMockRet(
                "http://166.111.68.66:2042/news/action/query/search?keyword=杭州&pageNo=1&pageSize=1",
                "{\"list\":[{\"lang_Type\":\"zh-CN\",\"newsClassTag\":\"国内\",\"news_Author\":\"来源：光明网-《光明日报》\",\"news_ID\":\"201609080402943cab6cb8fb46bfb37b9d7ff189b406\",\"news_Pictures\":\"http://img.gmw.cn/pic/content_logo.png\",\"news_Source\":\"光明网\",\"news_Time\":\"20160907000000\",\"news_Title\":\"杭州·握手\",\"news_URL\":\"http://news.gmw.cn/2016-09/07/content_21853855.htm\",\"news_Video\":\"\",\"news_Intro\":\"　　作者：薛保勤 万里迢迢 风尘仆仆   世界在这里握手   携五大洲的温度...\"}],\"pageNo\":1,\"pageSize\":1,\"totalPages\":2851,\"totalRecords\":2851}"
        );
        setMockRet(
                "http://166.111.68.66:2042/news/action/query/search?keyword=杭州&pageNo=1&pageSize=1&category=2",
                "{\"list\":[{\"lang_Type\":\"zh-CN\",\"newsClassTag\":\"教育\",\"news_Author\":\"钱江晚报\",\"news_ID\":\"2016091204005d715c75a4f940c490b8dedb1bb45267\",\"news_Pictures\":\"\",\"news_Source\":\"央广网\",\"news_Time\":\"20160911000000\",\"news_Title\":\"期待G20后的杭州秋天\",\"news_URL\":\"http://news.cnr.cn/native/gd/20160911/t20160911_523127787.shtml\",\"news_Video\":\"\",\"news_Intro\":\"　　G20杭州峰会之前，我去一家创业公司采访。办公室在西溪湿地边上的一个创业...\"}],\"pageNo\":1,\"pageSize\":1,\"totalPages\":189,\"totalRecords\":189}"
        );
        setMockRet(
                "http://166.111.68.66:2042/news/action/query/detail?newsId=20150826071307185ac850dd43d9ac9b2e94b9335779",
                "{ \"Keywords\" : [ { \"word\" : \"邮政\" , \"score\" : 655.6233585077603} , { \"word\" : \"保税\" , \"score\" : 449.27537673899826} , { \"word\" : \"超市\" , \"score\" : 403.10380114031454} , { \"word\" : \"跨境\" , \"score\" : 316.8707369684706} , { \"word\" : \"开业\" , \"score\" : 174.89743567397446} , { \"word\" : \"体验\" , \"score\" : 173.84077694840929} , { \"word\" : \"进出口\" , \"score\" : 85.14749798484092} , { \"word\" : \"首家\" , \"score\" : 82.40046962999168} , { \"word\" : \"连锁店\" , \"score\" : 78.13183020052271} , { \"word\" : \"选购\" , \"score\" : 63.07342898120844} , { \"word\" : \"名为\" , \"score\" : 59.064057344088866} , { \"word\" : \"创办\" , \"score\" : 58.74916617764039} , { \"word\" : \"开办\" , \"score\" : 56.854206059717534} , { \"word\" : \"顾客\" , \"score\" : 54.46756666425444} , { \"word\" : \"粮油\" , \"score\" : 53.93397838569468} , { \"word\" : \"当日\" , \"score\" : 53.422668019774974} , { \"word\" : \"具备\" , \"score\" : 50.68624275707213} , { \"word\" : \"公司\" , \"score\" : 47.21622420742737} , { \"word\" : \"商品\" , \"score\" : 45.80383419435514} , { \"word\" : \"条件\" , \"score\" : 44.550109614589125} , { \"word\" : \"合作\" , \"score\" : 42.499775167265106}] , \"bagOfWords\" : [ { \"word\" : \"将\" , \"score\" : 3.0} , { \"word\" : \"海\" , \"score\" : 3.0} , { \"word\" : \"中国\" , \"score\" : 10.0} , { \"word\" : \"新华社\" , \"score\" : 3.0} , { \"word\" : \"浙江省\" , \"score\" : 3.0} , { \"word\" : \"体验\" , \"score\" : 4.0} , { \"word\" : \"跨境\" , \"score\" : 7.0} , { \"word\" : \"邮政\" , \"score\" : 16.0} , { \"word\" : \"电商\" , \"score\" : 7.0} , { \"word\" : \"里\" , \"score\" : 3.0} , { \"word\" : \"发\" , \"score\" : 3.0} , { \"word\" : \"商品\" , \"score\" : 3.0} , { \"word\" : \"在\" , \"score\" : 9.0} , { \"word\" : \"杭州\" , \"score\" : 10.0} , { \"word\" : \"由\" , \"score\" : 3.0} , { \"word\" : \"巍\" , \"score\" : 3.0} , { \"word\" : \"所\" , \"score\" : 6.0} , { \"word\" : \"连锁店\" , \"score\" : 3.0} , { \"word\" : \"条件\" , \"score\" : 3.0} , { \"word\" : \"这家\" , \"score\" : 3.0} , { \"word\" : \"馆\" , \"score\" : 4.0} , { \"word\" : \" \" , \"score\" : 12.0} , { \"word\" : \"内\" , \"score\" : 6.0} , { \"word\" : \"选购\" , \"score\" : 3.0} , { \"word\" : \"当日\" , \"score\" : 3.0} , { \"word\" : \"月\" , \"score\" : 3.0} , { \"word\" : \"顾客\" , \"score\" : 3.0} , { \"word\" : \"开办\" , \"score\" : 3.0} , { \"word\" : \"OO\" , \"score\" : 3.0} , { \"word\" : \"日\" , \"score\" : 3.0} , { \"word\" : \"合作\" , \"score\" : 3.0} , { \"word\" : \"”\" , \"score\" : 3.0} , { \"word\" : \"“\" , \"score\" : 3.0} , { \"word\" : \"创办\" , \"score\" : 3.0} , { \"word\" : \"开业\" , \"score\" : 4.0} , { \"word\" : \"保税\" , \"score\" : 10.0} , { \"word\" : \"购\" , \"score\" : 3.0} , { \"word\" : \"超市\" , \"score\" : 10.0} , { \"word\" : \"的\" , \"score\" : 6.0} , { \"word\" : \"。\" , \"score\" : 9.0} , { \"word\" : \"与\" , \"score\" : 3.0} , { \"word\" : \"粮油\" , \"score\" : 3.0} , { \"word\" : \"进出口\" , \"score\" : 3.0} , { \"word\" : \"O2O\" , \"score\" : 1.0} , { \"word\" : \"，\" , \"score\" : 9.0} , { \"word\" : \"具备\" , \"score\" : 3.0} , { \"word\" : \"首家\" , \"score\" : 3.0} , { \"word\" : \"摄\" , \"score\" : 3.0} , { \"word\" : \"一\" , \"score\" : 3.0} , { \"word\" : \"名为\" , \"score\" : 3.0} , { \"word\" : \"龙\" , \"score\" : 3.0} , { \"word\" : \"乐\" , \"score\" : 3.0} , { \"word\" : \"公司\" , \"score\" : 3.0} , { \"word\" : \"淘\" , \"score\" : 3.0}] , \"crawl_Source\" : \"news.xinhuanet.com\" , \"crawl_Time\" : \"20150826071138\" , \"inborn_KeyWords\" : \"TRUE\" , \"lang_Type\" : \"zh-CN\" , \"locations\" : [ { \"word\" : \"浙江省\" , \"count\" : 3} , { \"word\" : \"杭州\" , \"count\" : 10} , { \"word\" : \"中国\" , \"count\" : 10}] , \"newsClassTag\" : \"科技\" , \"news_Author\" : \"\" , \"news_Content\" : \"    月日，顾客在中国邮政杭州跨境电商保税超市里选购商品。当日，中国邮政首家跨境电商保税超市OO体验馆在杭州一邮政所内开业。这家名为“海淘乐购”的保税超市由中国邮政与浙江省粮油进出口公司合作创办，将在杭州具备条件的邮政所内开办连锁店。新华社发 龙巍 摄\\n    月日，顾客在中国邮政杭州跨境电商保税超市里选购商品。当日，中国邮政首家跨境电商保税超市OO体验馆在杭州一邮政所内开业。这家名为“海淘乐购”的保税超市由中国邮政与浙江省粮油进出口公司合作创办，将在杭州具备条件的邮政所内开办连锁店。新华社发 龙巍 摄\\n    月日，顾客在中国邮政杭州跨境电商保税超市里选购商品。当日，中国邮政首家跨境电商保税超市OO体验馆在杭州一邮政所内开业。这家名为“海淘乐购”的保税超市由中国邮政与浙江省粮油进出口公司合作创办，将在杭州具备条件的邮政所内开办连锁店。新华社发 龙巍 摄\" , \"news_ID\" : \"20150826071307185ac850dd43d9ac9b2e94b9335779\" , \"news_Journal\" : \"廖国红\" , \"news_Pictures\" : \"http://news.xinhuanet.com/info/2015-08/25/134552305_11n.jpg  \" , \"news_Source\" : \"新华网\" , \"news_Time\" : \"20150825094600\" , \"news_Title\" : \"中国邮政跨境电商保税超市O2O体验馆杭州开业\" , \"news_URL\" : \"http://news.xinhuanet.com/info/2015-08/25/c_134552305.htm\" , \"news_Video\" : \"No Match\" , \"organizations\" : [ { \"word\" : \"新华社\" , \"count\" : 3}] , \"persons\" : [ ] , \"repeat_ID\" : \"0\" , \"seggedPListOfContent\" : [ \" /x  /x  /x  /x 月/n 日/g ，/w 顾客/n 在/p 中国/LOC 邮政/n 杭州/LOC 跨境/vn 电商/OTH 保税/vn 超市/n 里/f 选购/v 商品/n 。/w 当日/t ，/w 中国/LOC 邮政/n 首家/n 跨境/vn 电商/OTH 保税/vn 超市/n OO/x 体验/v 馆/g 在/p 杭州/LOC 一/m 邮政/n 所/q 内/f 开业/v 。/w 这家/r 名为/v “/w 海/n 淘/v 乐/g 购/g ”/w 的/u 保税/vn 超市/n 由/p 中国/LOC 邮政/n 与/p 浙江省/LOC 粮油/n 进出口/vn 公司/n 合作/v 创办/v ，/w 将/d 在/p 杭州/LOC 具备/v 条件/n 的/u 邮政/n 所/q 内/f 开办/v 连锁店/n 。/w 新华社/ORG 发/v  龙/n 巍/PER  摄/g \" , \" /x  /x  /x  /x 月/n 日/g ，/w 顾客/n 在/p 中国/LOC 邮政/n 杭州/LOC 跨境/vn 电商/OTH 保税/vn 超市/n 里/f 选购/v 商品/n 。/w 当日/t ，/w 中国/LOC 邮政/n 首家/n 跨境/vn 电商/OTH 保税/vn 超市/n OO/x 体验/v 馆/g 在/p 杭州/LOC 一/m 邮政/n 所/q 内/f 开业/v 。/w 这家/r 名为/v “/w 海/n 淘/v 乐/g 购/g ”/w 的/u 保税/vn 超市/n 由/p 中国/LOC 邮政/n 与/p 浙江省/LOC 粮油/n 进出口/vn 公司/n 合作/v 创办/v ，/w 将/d 在/p 杭州/LOC 具备/v 条件/n 的/u 邮政/n 所/q 内/f 开办/v 连锁店/n 。/w 新华社/ORG 发/v  龙/n 巍/PER  摄/g \" , \" /x  /x  /x  /x 月/n 日/g ，/w 顾客/n 在/p 中国/LOC 邮政/n 杭州/LOC 跨境/vn 电商/OTH 保税/vn 超市/n 里/f 选购/v 商品/n 。/w 当日/t ，/w 中国/LOC 邮政/n 首家/n 跨境/vn 电商/OTH 保税/vn 超市/n OO/x 体验/v 馆/g 在/p 杭州/LOC 一/m 邮政/n 所/q 内/f 开业/v 。/w 这家/r 名为/v “/w 海/n 淘/v 乐/g 购/g ”/w 的/u 保税/vn 超市/n 由/p 中国/LOC 邮政/n 与/p 浙江省/LOC 粮油/n 进出口/vn 公司/n 合作/v 创办/v ，/w 将/d 在/p 杭州/LOC 具备/v 条件/n 的/u 邮政/n 所/q 内/f 开办/v 连锁店/n 。/w 新华社/ORG 发/v  龙/n 巍/PER  摄/g \"] , \"seggedTitle\" : \"中国/LOC 邮政/n 跨境/vn 电商/OTH 保税/vn 超市/n O2O/x 体验/v 馆/g 杭州/LOC 开业/v \" , \"wordCountOfContent\" : 237 , \"wordCountOfTitle\" : 11}"
        );
        mNewsAPI = new NewsAPI(mockStreamFactory);
    }

    @Test
    public void testGetNews() throws Exception
    {
        News news = mNewsAPI.getNews("20150826071307185ac850dd43d9ac9b2e94b9335779");
        assertEquals("邮政", news.Keywords.get(0).word);
        assertEquals(2015, news.getNewsTime().get(Calendar.YEAR));
        assertEquals(2015, news.getCrawlTime().get(Calendar.YEAR));
        assertEquals(News.Category.TECH, news.getNewsClassTag());
    }

    @Test
    public void testGetNewsThrowsWhenNotFound() throws Exception
    {
        IOException except = null;
        try
        {
            mNewsAPI.getNews("invalid");
        } catch (IOException e)
        {
            except = e;
        }
        assertNotNull(except);
    }

    @Test
    public void testGetListDefault() throws Exception
    {
        NewsList newsList = mNewsAPI.getList(1, 1);
        assertEquals("创事记 微博 作者： 广州阿超" ,newsList.list.get(0).news_Author);
        assertEquals(1373727, newsList.totalRecords);
    }

    @Test
    public void testGetListCategory() throws Exception
    {
        NewsList newsList = mNewsAPI.getList(1, 1, News.Category.DOMESTIC);
        assertEquals("来源：成都日报", newsList.list.get(0).news_Author);
        assertEquals(168534, newsList.totalRecords);
    }

    @Test
    public void testGetListKeyWord() throws Exception
    {
        NewsList newsList = mNewsAPI.getList(1, 1, "杭州");
        assertEquals("来源：光明网-《光明日报》", newsList.list.get(0).news_Author);
        assertEquals(2851, newsList.totalRecords);
    }

    @Test
    public void testGetListKeywordCategory() throws Exception
    {
        NewsList newsList = mNewsAPI.getList(1, 1, "杭州", News.Category.EDUCATION);
        assertEquals("钱江晚报", newsList.list.get(0).news_Author);
        assertEquals(189, newsList.totalRecords);
    }
}