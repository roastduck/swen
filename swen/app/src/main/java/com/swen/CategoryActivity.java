package com.swen;

import android.os.Bundle;

/**
 * Created by Teon on 2017/9/11.
 */

public class CategoryActivity extends NewsListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle data = getIntent().getExtras();
        int categoryId = data.getInt("category");
        try
        {
            News.Category category = News.Category.fromId(categoryId);
            getSupportActionBar().setTitle(category.getStr());
            mAppendableList = new AppendableNewsList(15, null, category);
            mAppendableList.setKeywordFilter(((ApplicationWithStorage)getApplication()).getKeywordFilter());
            initialize();
        } catch (IndexOutOfBoundsException ignored)
        {
            throw new RuntimeException(); // impossible
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
