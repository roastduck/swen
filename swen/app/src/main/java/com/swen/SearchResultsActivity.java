package com.swen;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.swen.promise.Callback;


public class SearchResultsActivity extends BaseActivity {
    private int preLastItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = (LinearLayout)findViewById(R.id.content_main);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout.addView(inflater.inflate(R.layout.activity_search, null));

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            AppendableNewsList list = new AppendableNewsList(30, query, null, false, new Behavior(this));
            ListView lv = (ListView)findViewById(R.id.search_list);
            SearchResultAdapter adapter = new SearchResultAdapter(list.list, query, getApplicationContext());
            lv.setAdapter(adapter);


            list.append().thenUI(new Callback<Object, Object>() {
                @Override
                public Object run(final Object result) throws Throwable {
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
                                    list.append().thenUI(new Callback<Object, Object>() {
                                        @Override
                                        public Object run(Object result) throws Throwable {
                                            adapter.notifyDataSetChanged();
                                            return null;
                                        }
                                    }).failUI(new Callback<Throwable, Object>() {
                                        @Override
                                        public Object run(final Throwable result) throws Throwable {
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
            }).failUI(new Callback<Throwable, Object>() {
                @Override
                public Object run(Throwable result) throws Throwable {
                    return null;
                }
            });
        }
    }
}
