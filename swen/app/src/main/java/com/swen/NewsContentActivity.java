package com.swen;

import android.content.res.ObbInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.swen.promise.Callback;
import com.swen.promise.Promise;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teon on 2017/9/11.
 */

public class NewsContentActivity extends BaseActivity {

    //TODO: update Behavior, grey the news item in parent list
    private News mNews;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        LinearLayout layout = (LinearLayout)findViewById(R.id.content_main);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout.addView(inflater.inflate(R.layout.news_content_page, null));

        Bundle bundle = getIntent().getExtras();
        String[] pictures = bundle.getStringArray("pictures");
        String news_id = bundle.getString("news_id");
        String news_title = bundle.getString("news_title");
        ((TextView)findViewById(R.id.tv_content_title)).setText(news_title);
        Storage storage = ((ApplicationWithStorage)getApplication()).getStorage();
        Callback<Exception, Object> failCallback = new Callback<Exception, Object>() {
            @Override
            public Object run(Exception exception) {
                Toast.makeText(NewsContentActivity.this,
                    "新闻详情加载失败", Toast.LENGTH_SHORT).show();
                return null;
            }
        };
        try {
            Promise news_promise = storage.getNewsCached(news_id);
            news_promise.failUI(failCallback);
            news_promise.thenUI(new Callback<News, Object>() {
                @Override
                public Object run(News news) {
                    mNews = news;
                    //TODO:Temporarily show all the text
                    return null;
                }
            }).waitUntilHasRun();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //TODO：根据图片和文字内容先做好布局，再进行图片加载
        Promise[] picture_promise = new Promise[pictures.length];
        for(int i = 0; i < picture_promise.length; i++) {
            picture_promise[i] = storage.getPicCached(pictures[i]);
            picture_promise[i].failUI(failCallback);
            picture_promise[i].thenUI(new Callback<Bitmap, Object>() {
                @Override
                public Object run(Bitmap picture) {
                    //TODO：根据之前做好的布局显示图片
                    return null;
                }
            });
        }
    }
}
