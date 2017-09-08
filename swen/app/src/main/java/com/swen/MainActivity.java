package com.swen;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;
import org.jdeferred.android.AndroidDoneCallback;
import org.jdeferred.android.AndroidExecutionScope;
import org.jdeferred.android.AndroidFailCallback;
import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mView;
    private List<News> mData;
    private AppendableNewsList mAppendableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (RecyclerView) findViewById(R.id.rv_main);
        mAppendableList = new AppendableNewsList(50, null, null, true, new Behavior(this));
        mData = mAppendableList.list;
        mAppendableList.append().done(new AndroidDoneCallback() {
            @Override
            public void onDone(Object result) {
                //TODO:停止加载动画
                mView.setAdapter(new NewsListAdapter(mData, mAppendableList, MainActivity.this));
                mView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }

            @Override
            public AndroidExecutionScope getExecutionScope() {
                return AndroidExecutionScope.UI;
            }
        }).fail(new AndroidFailCallback() {
            @Override
            public void onFail(Object result) {
                //TODO:停止加载动画
                Toast.makeText(MainActivity.this, "加载新闻列表失败", Toast.LENGTH_LONG);
            }

            @Override
            public AndroidExecutionScope getExecutionScope() {
                return AndroidExecutionScope.UI;
            }
        });
    }
}
