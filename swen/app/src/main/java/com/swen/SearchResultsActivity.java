package com.swen;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.swen.promise.Callback;
import com.swen.promise.Promise;


public class SearchResultsActivity extends BaseActivity {
    private int preLastItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = (LinearLayout)findViewById(R.id.content_main);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout.addView(inflater.inflate(R.layout.activity_search, null));
        if(TransientSetting.isNightMode()) {
            layout.setBackgroundColor(getResources().getColor(R.color.foreground_dark));
            findViewById(R.id.search_list).setBackgroundColor(getResources().getColor(R.color.foreground_dark));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView searchList = (ListView)findViewById(R.id.search_list);

        Context context = getApplicationContext();

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = (News)parent.getAdapter().getItem(position);
                Intent intent = new Intent();
                intent.setAction(NewsContentActivity.ACTION_NAME);
                Bundle data = new Bundle();
                data.putString(context.getString(R.string.bundle_news_title), news.news_Title);
                data.putString(context.getString(R.string.bundle_news_id), news.news_ID);
                data.putStringArray(context.getString(R.string.bundle_news_pictures)
                        , news.getNewsPictures().toArray(new String[news.getNewsPictures().size()]));
                intent.putExtras(data);
                context.startActivity(intent);
            }
        });

        handleIntent(getIntent());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            AppendableNewsList list = new AppendableNewsList(30, query, null, false, ((ApplicationWithStorage)getApplication()).getBehavior());
            list.setKeywordFilter(((ApplicationWithStorage)getApplication()).getKeywordFilter());
            ListView lv = (ListView)findViewById(R.id.search_list);
            SearchResultAdapter adapter = new SearchResultAdapter(list.list, query, getApplicationContext());
            lv.setAdapter(adapter);

            Promise promise = list.append();
            promise.thenUI(new Callback<Object, Object>() {
                @Override
                public Object run(final Object result) throws Exception {
                    lv.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView absListView, int i) {

                        }

                        @Override
                        public void onScroll(AbsListView absListView, final int firstVisibleItem,
                                             final int visibleItemCount, final int totalItemCount) {
                            if (absListView.getId() == R.id.search_list) {
                                int lastItem = firstVisibleItem + visibleItemCount;
                                if (lastItem == totalItemCount && preLastItem != lastItem) {
                                    preLastItem = lastItem;

                                    Promise promise1 = list.append();
                                    promise1.thenUI(new Callback<Object, Object>() {
                                        @Override
                                        public Object run(Object result) throws Exception {
                                            adapter.notifyDataSetChanged();
                                            return null;
                                        }
                                    }).failUI(new Callback<Exception, Object>() {
                                        @Override
                                        public Object run(final Exception result) throws Exception {
                                            return null;
                                        }
                                    });
                                }
                            }
                        }
                    });
                    adapter.notifyDataSetChanged();
                    return null;
                }
            }).failUI(new Callback<Exception, Object>() {
                @Override
                public Object run(Exception result) throws Exception {
                    return null;
                }
            });
        }
    }
}
