package com.swen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.swen.promise.Callback;
import com.yanzhenjie.recyclerview.swipe.*;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.List;
import java.util.Vector;

public class FavoritesActivity extends BaseActivity
{
    private SwipeMenuRecyclerView rv;
    private List<String> list;
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LinearLayout layout = (LinearLayout)findViewById(R.id.content_main);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout.addView(inflater.inflate(R.layout.activity_favorites, null));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storage = ((ApplicationWithStorage)getApplication()).getStorage();

        list = storage.getMarked();

        FavoritesAdapter adapter = new FavoritesAdapter(list);

        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

                int height = ViewGroup.LayoutParams.MATCH_PARENT;

                SwipeMenuItem closeItem = new SwipeMenuItem(FavoritesActivity.this)
                        .setBackground(R.drawable.selector_purple)
                        .setImage(R.mipmap.ic_action_close)
                        .setWidth(width)
                        .setHeight(height);
                swipeLeftMenu.addMenuItem(closeItem);
                swipeRightMenu.addMenuItem(closeItem);
            }
        };

        SwipeMenuItemClickListener menuItemClickListener = new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                menuBridge.closeMenu();
                int listItemId = menuBridge.getAdapterPosition();
                storage.unmark(list.get(listItemId)).failUI(new Callback<Exception,Object>() {
                    @Override
                    public Object run(Exception e) throws Exception {
                        Toast.makeText(FavoritesActivity.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }); // This is done with no `then` callback
                list.remove(listItemId);
                rv.removeViewAt(listItemId);
                adapter.notifyItemRemoved(listItemId);
                adapter.notifyItemRangeChanged(listItemId, list.size());
            }
        };

        SwipeItemClickListener itemClickListener = new SwipeItemClickListener()
        {
            @Override
            public void onItemClick(View itemView, int position)
            {
                storage.getNewsCached(list.get(position)).thenUI(new Callback<News,Object>() {
                    @Override
                    public Object run(News news)
                    {
                        Intent intent = new Intent(FavoritesActivity.this, NewsContentActivity.class);
                        intent.putExtra(getString(R.string.bundle_news_pictures), news.getNewsPictures().toArray(new String[0]));
                        intent.putExtra(getString(R.string.bundle_news_id), news.news_ID);
                        intent.putExtra(getString(R.string.bundle_news_title), news.news_Title);
                        startActivity(intent);
                        return null;
                    }
                });
            }
        };

        rv = (SwipeMenuRecyclerView)findViewById(R.id.fav_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color)));
        rv.setAdapter(adapter);
        rv.setSwipeMenuCreator(swipeMenuCreator);
        rv.setSwipeMenuItemClickListener(menuItemClickListener);
        rv.setSwipeItemClickListener(itemClickListener);

        if (list.isEmpty())
            Toast.makeText(this, R.string.no_favorite_prompt, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder>
    {
        private List<String> list;

        public FavoritesAdapter(List<String> list)
        {
            this.list = list;
        }

        @Override
        public int getItemCount()
        {
            return list.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            holder.setData(list.get(position));
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView tvTitle;

            public ViewHolder(View itemView)
            {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.fav_title);
            }

            public void setData(String id)
            {
                storage.getNewsCached(id).thenUI(new Callback<News, Object>()
                {
                    @Override
                    public Object run(News news) throws Exception
                    {
                        ViewHolder.this.tvTitle.setText(news.news_Title);
                        return null;
                    }
                });
            }
        }
    }
}
