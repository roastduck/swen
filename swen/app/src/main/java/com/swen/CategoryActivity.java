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
        String categoryName = data.getString("category");
        try {
            News.Category category = News.Category.fromString(categoryName);
            mAppendableList = new AppendableNewsList(50, null, category);
            initialize();
        } catch (News.Category.InvalidCategoryException e) {
            //TODO:invalid category
            e.printStackTrace();
        }
    }
}
