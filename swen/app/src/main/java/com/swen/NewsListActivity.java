package com.swen;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.swen.promise.*;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.List;
import java.util.Random;

public abstract class NewsListActivity extends BaseActivity {
    protected SwipeMenuRecyclerView mView;
    protected AppendableNewsList mAppendableList;
    protected Random random = new Random(System.currentTimeMillis());
    protected NewsListAdapter mAdapter;

    protected void initialize() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.content_main);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout.addView(inflater.inflate(R.layout.news_list_page, null));

        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.menu_open, R.string.menu_close);
        toggle.syncState();

        mView = (SwipeMenuRecyclerView) findViewById(R.id.rv_main);
        //Toast.makeText(this, "正在加载新闻列表", Toast.LENGTH_LONG).show();
        Promise promise = mAppendableList.append();
        promise.thenUI(new Callback<Object, Object>() {
            @Override
            public Object run(Object result) throws Exception {
                //TODO:停止加载动画
                //TODO:判断有没有加载出新东西（考虑API的bug）
                mView.useDefaultLoadMore();
                mView.setLoadMoreListener(new SwipeMenuRecyclerView.LoadMoreListener()
                {
                    @Override
                    public void onLoadMore()
                    {
                        mAppendableList.append().thenUI(new Callback<Object, Object>() {
                            @Override
                            public Object run(final Object result) throws Exception {
                                //Toast.makeText(NewsListActivity.this,
                                //    "成功获取更多新闻条目", Toast.LENGTH_SHORT).show();
                                mAdapter.updateData(random);
                                mView.loadMoreFinish(false, true);
                                return null;
                            }
                        }).failUI(new Callback<Exception, Object>() {
                            @Override
                            public Object run(final Exception result) throws Exception {
                                Toast.makeText(NewsListActivity.this,
                                        result.getMessage(), Toast.LENGTH_SHORT).show();
                                mView.loadMoreFinish(false, true);
                                return null;
                            }
                        });
                    }
                });
                mView.loadMoreFinish(false, true); // 这条必须写，疑似是上游bug
                mAdapter = new NewsListAdapter(mAppendableList, NewsListActivity.this, random);
                mView.setAdapter(mAdapter);
                mView.setLayoutManager(new LinearLayoutManager(NewsListActivity.this));
                return null;
            }

        }).failUI(new Callback<Exception, Object>() {
            @Override
            public Object run(Exception result) throws Exception {
                //TODO:停止加载动画
                for (StackTraceElement e : result.getStackTrace()) {
                    Log.e("MainActivity", e.toString());
                }
                return null;
            }
        });
    }
}