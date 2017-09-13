package com.swen;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.yanzhenjie.recyclerview.swipe.*;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.List;

public class FilterSettingActivity extends BaseActivity
{
    private SwipeMenuRecyclerView rv;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LinearLayout layout = (LinearLayout)findViewById(R.id.content_main);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout.addView(inflater.inflate(R.layout.activity_filter_setting, null));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.keyword_filter_title);

        KeywordFilter keywordFilter = ((ApplicationWithStorage)getApplication()).getKeywordFilter();
        list = keywordFilter.getList();

        FilterSettingAdapter adapter = new FilterSettingAdapter(list, this);

        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

                int height = ViewGroup.LayoutParams.MATCH_PARENT;

                SwipeMenuItem closeItem = new SwipeMenuItem(FilterSettingActivity.this)
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
                keywordFilter.setList(list);
                rv.removeViewAt(listItemId);
                adapter.notifyItemRemoved(listItemId);
                adapter.notifyItemRangeChanged(listItemId, list.size());
            }
        };

        rv = (SwipeMenuRecyclerView)findViewById(R.id.fil_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color)));
        rv.setAdapter(adapter);
        rv.setSwipeMenuCreator(swipeMenuCreator);
        rv.setSwipeMenuItemClickListener(menuItemClickListener);

        ((Button)findViewById(R.id.fil_btn)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                list.add(((EditText)findViewById(R.id.fil_text)).getText().toString());
                keywordFilter.setList(list);
                adapter.notifyItemRangeInserted(list.size() - 1, list.size());
                Toast.makeText(FilterSettingActivity.this, R.string.fil_success, Toast.LENGTH_LONG).show();
                ((EditText)findViewById(R.id.fil_text)).setText("");
            }
        });

        Toast.makeText(this, R.string.fil_guide, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private static class FilterSettingAdapter extends RecyclerView.Adapter<FilterSettingAdapter.ViewHolder>
    {
        private List<String> list;
        Context context;

        public FilterSettingAdapter(List<String> list, Context context)
        {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getItemCount()
        {
            return list.size();
        }

        @Override
        public FilterSettingActivity.FilterSettingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new FilterSettingActivity.FilterSettingAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fil_item, parent, false));
        }

        @Override
        public void onBindViewHolder(FilterSettingActivity.FilterSettingAdapter.ViewHolder holder, int position)
        {
            holder.setData(list.get(position));
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView tvTitle;

            public ViewHolder(View itemView)
            {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.fil_title);
            }

            public void setData(String title)
            {
                this.tvTitle.setText(title);
            }
        }
    }
}
