package com.swen;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

/**
 * Created by Teon on 2017/9/11.
 */

public class RecommendationActivity extends NewsListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppendableList = new AppendableNewsList(10, null, null, true, ((ApplicationWithStorage)getApplication()).getBehavior());
        initialize();
        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.menu_open, R.string.menu_close);
        toggle.syncState();
    }
}
