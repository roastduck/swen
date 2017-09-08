package com.swen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private NewsListView mView;
    private List<News> mData;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (NewsListView) findViewById(R.id.rv_main);
        mView.init(this, new AppendableNewsList(50, null, null, true, new Behavior(this)));
    }
}
