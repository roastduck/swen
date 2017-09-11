package com.swen;

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
import com.yanzhenjie.recyclerview.swipe.*;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.List;
import java.util.Vector;

public class FavoritesActivity extends BaseActivity
{
    private SwipeMenuRecyclerView rv;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LinearLayout layout = (LinearLayout)findViewById(R.id.content_main);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout.addView(inflater.inflate(R.layout.activity_favorites, null));

        list = new Vector<>();

        for (int i = 0; i <= 20; i++)
            list.add(Integer.valueOf(i).toString());

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
                list.remove(listItemId);
                // TODO: Storage.unmark
                rv.removeViewAt(listItemId);
                adapter.notifyItemRemoved(listItemId);
                adapter.notifyItemRangeChanged(listItemId, list.size());
            }
        };

        rv = (SwipeMenuRecyclerView)findViewById(R.id.fav_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color)));
        rv.setAdapter(adapter);
        rv.setSwipeMenuCreator(swipeMenuCreator);
        rv.setSwipeMenuItemClickListener(menuItemClickListener);
    }

    private static class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder>
    {
        private List<String> list;

        public FavoritesAdapter(List<String> list)
        {
            this.list = list;
        }

        @Override
        public int getItemCount() {
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

        public static class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView tvTitle;

            public ViewHolder(View itemView)
            {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.fav_title);
            }

            public void setData(String title)
            {
                this.tvTitle.setText(title);
            }
        }
    }
}
