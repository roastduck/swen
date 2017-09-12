package com.swen;

import android.os.Bundle;

/**
 * Created by Teon on 2017/9/11.
 */

public class RecommendationActivity extends NewsListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppendableList = new AppendableNewsList(50, null, null, true, Behavior.getInstance(this));
        initialize();
    }
}
