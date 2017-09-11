package com.swen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;
import com.swen.promise.*;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mView;
    private AppendableNewsList mAppendableList;
    private boolean loading = false;
    private Random random = new Random(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (RecyclerView) findViewById(R.id.rv_main);
        mAppendableList = new AppendableNewsList(50, null, null, true, new Behavior(this));
        Toast.makeText(this, "正在加载新闻列表", Toast.LENGTH_LONG).show();
        Promise promise = mAppendableList.append();
        promise.thenUI(new Callback<Object, Object>() {
            @Override
            public Object run(Object result) throws Throwable {
                //TODO:停止加载动画
                Toast.makeText(MainActivity.this, "新闻列表加载完毕", Toast.LENGTH_SHORT).show();
                mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                        int totalItemCount = recyclerView.getAdapter().getItemCount();
                        int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                        int visibleItemCount = recyclerView.getChildCount();
                        if(newState == RecyclerView.SCROLL_STATE_IDLE
                            && lastVisibleItemPosition == totalItemCount - 1
                            && visibleItemCount > 0
                            && !loading) {
                            loading = true;
                            mAppendableList.append().thenUI(new Callback<Object, Object>() {
                                @Override
                                public Object run(final Object result) throws Throwable {
                                    Toast.makeText(MainActivity.this,
                                        "成功获取更多新闻条目", Toast.LENGTH_SHORT).show();
                                    ((NewsListAdapter)(mView.getAdapter())).updateData(random);
                                    loading = false;
                                    return null;
                                }
                            }).failUI(new Callback<Throwable, Object>() {
                                @Override
                                public Object run(final Throwable result) throws Throwable {
                                    Toast.makeText(MainActivity.this,
                                        result.getMessage(), Toast.LENGTH_SHORT).show();
                                    loading = false;
                                    return null;
                                }
                            });
                        }
                    }
                });
                mView.setAdapter(new NewsListAdapter(mAppendableList, MainActivity.this, random));
                mView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                return null;
            }
        });
        promise.failUI(new Callback<Throwable, Object>() {

            @Override
            public Object run(Throwable result) throws Throwable {
                //TODO:停止加载动画
                for(StackTraceElement e: result.getStackTrace()) {
                    Log.e("MainActivity", e.toString());
                }
                //Toast.makeText(MainActivity.this, "加载新闻列表失败", Toast.LENGTH_SHORT).show();
                return null;
            }
        });
    }
}
