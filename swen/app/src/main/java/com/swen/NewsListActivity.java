package com.swen;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.swen.promise.*;
import com.wang.avi.AVLoadingIndicatorView;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.Random;

public abstract class NewsListActivity extends BaseActivity implements View.OnClickListener{
    protected SwipeMenuRecyclerView mView;
    protected AppendableNewsList mAppendableList;
    protected Random random = new Random(System.currentTimeMillis());
    protected NewsListAdapter mAdapter;
    protected LinearLayout mLinearLayout;



//    protected void showLoading() {
//        mHint.setVisibility(View.GONE);
//        mLoading.show();
//    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fl_no_network) {
//            showLoading();
            SystemClock.sleep(1000);
            if(isNetworkConnected()) {
                updateUI();
            } else {
                showNoNetwork();
            }
        }
    }

    protected void updateUI() {
        //Toast.makeText(this, "正在加载新闻列表", Toast.LENGTH_LONG).show();
        mErrorNotified = false;
        Promise promise = mAppendableList.append();
        promise.thenUI(new Callback<Object, Object>() {
            @Override
            public Object run(Object result) throws Exception {
                //TODO:停止加载动画
                //TODO:判断有没有加载出新东西（考虑API的bug）
                showNews();
                Log.e("NewsListActivity", "in here " + mAppendableList.list.size());
                LayoutInflater inflater = LayoutInflater.from(NewsListActivity.this);
                mLinearLayout.addView(inflater.inflate(R.layout.news_list_page, null));
                mView = (SwipeMenuRecyclerView) findViewById(R.id.rv_main);
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
                                mErrorNotified = false;
                                mAdapter.updateData(random);
                                mView.loadMoreFinish(false, true);
                                return null;
                            }
                        }).failUI(new Callback<Exception, Object>() {
                            @Override
                            public Object run(final Exception result) throws Exception {
                                if(mErrorNotified) {
                                    return null;
                                }
                                mErrorNotified = true;
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
                Log.e("NewsListActivity", "in here " + mView.getFooterItemCount());
                return null;
            }

        }).failUI(new Callback<Exception, Object>() {
            @Override
            public Object run(Exception result) throws Exception {
                //TODO:停止加载动画
                showLoadError();
                for (StackTraceElement e : result.getStackTrace()) {
                    Log.e("MainActivity", e.toString());
                }
                return null;
            }
        });
    }

    protected void initialize() {
        mLinearLayout = (LinearLayout)findViewById(R.id.content_main);
        mNoNetwork = (FrameLayout) findViewById(R.id.fl_no_network);
        mHint = (TextView)findViewById(R.id.tv_no_network);
//        mLoading = (AVLoadingIndicatorView) findViewById(R.id.fl_loading);
        mNoNetwork.setOnClickListener(this);
        mErrorNotified = false;
        if(isNetworkConnected()) {
            updateUI();
        } else {
            showNoNetwork();
        }
    }
}