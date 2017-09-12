package com.swen;

import android.os.Bundle;

/**
 * Created by Teon on 2017/9/11.
 */

public class CategoryActivity extends NewsListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        int categoryId = data.getInt("category");
        try
        {
            News.Category category = News.Category.fromId(categoryId);
            mAppendableList = new AppendableNewsList(50, null, category);
            initialize();
        } catch (IndexOutOfBoundsException ignored) {}
    }
}
