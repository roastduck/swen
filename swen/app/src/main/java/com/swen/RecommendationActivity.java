package com.swen;

import android.os.Bundle;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by Teon on 2017/9/11.
 */

public class RecommendationActivity extends NewsListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppendableList = new AppendableNewsList(10, null, null, true, ((ApplicationWithStorage)getApplication()).getBehavior());
        initialize();

        ShareSDK.initSDK(this);
    }
}
