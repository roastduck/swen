package com.swen;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.swen.promise.Callback;
import com.swen.promise.Promise;

import org.ansj.splitWord.analysis.ToAnalysis;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by Teon on 2017/9/11.
 */

public class RecommendationActivity extends NewsListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do nothing but launching the library
        // It takes long to do ToAnalysis.parse for the first time
        new Promise<Object, Object>(new Callback<Object, Object>() {
            @Override
            public Object run(Object o) {
                ToAnalysis.parse("This is to launch the library.");
                return new Object();
            }
        }, null);


        mAppendableList = new AppendableNewsList(15, null, null, true, ((ApplicationWithStorage)getApplication()).getBehavior());
        mAppendableList.setKeywordFilter(((ApplicationWithStorage)getApplication()).getKeywordFilter());
        initialize();

        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.menu_open, R.string.menu_close);
        toggle.syncState();

        ShareSDK.initSDK(this);
    }
}
