package com.swen;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.swen.promise.*;

import java.util.List;
import java.util.Random;

public abstract class NewsListActivity extends BaseActivity {
    protected RecyclerView mView;
    protected AppendableNewsList mAppendableList;
    protected boolean loading = false;
    protected Random random = new Random(System.currentTimeMillis());
    protected NewsListAdapter mAdapter;

    protected void initialize() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.content_main);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout.addView(inflater.inflate(R.layout.news_list_page, null));

        mView = (RecyclerView) findViewById(R.id.rv_main);
        //Toast.makeText(this, "正在加载新闻列表", Toast.LENGTH_LONG).show();
        Promise promise = mAppendableList.append();
        promise.thenUI(new Callback<Object, Object>() {
            @Override
            public Object run(Object result) throws Exception {
                //TODO:停止加载动画
                //Toast.makeText(NewsListActivity.this, "新闻列表加载完毕", Toast.LENGTH_SHORT).show();
                mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                        int totalItemCount = recyclerView.getAdapter().getItemCount();
                        int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                        int visibleItemCount = recyclerView.getChildCount();
                        if (newState == RecyclerView.SCROLL_STATE_IDLE
                            && lastVisibleItemPosition == totalItemCount - 1
                            && visibleItemCount > 0
                            && !loading) {
                            loading = true;
                            Promise promise1 = mAppendableList.append();
                            promise1.thenUI(new Callback<Object, Object>() {
                                @Override
                                public Object run(final Object result) throws Exception {
                                    //Toast.makeText(NewsListActivity.this,
                                    //    "成功获取更多新闻条目", Toast.LENGTH_SHORT).show();
                                    ((NewsListAdapter) (mView.getAdapter())).updateData(random);
                                    loading = false;
                                    return null;
                                }
                            }).failUI(new Callback<Exception, Object>() {
                                @Override
                                public Object run(final Exception result) throws Exception {
                                    Toast.makeText(NewsListActivity.this,
                                        result.getMessage(), Toast.LENGTH_SHORT).show();
                                    loading = false;
                                    return null;
                                }
                            });
                        }
                    }
                });
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