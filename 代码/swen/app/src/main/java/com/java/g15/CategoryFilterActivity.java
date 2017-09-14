package com.java.g15;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class CategoryFilterActivity extends BaseActivity
{
    private SwipeMenuRecyclerView rv;
    private List<CategorySelect> list;

    protected void toNightMode() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        LinearLayout layout = (LinearLayout)findViewById(R.id.content_main);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout.addView(inflater.inflate(R.layout.activity_category_filter, null));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.category_filter_title);

        CategorySetting categorySetting = ((ApplicationWithStorage)getApplication()).getCategorySetting();
        list = CategorySelect.fromCategoryList(categorySetting.getCategories());

        CategoryFilterAdapter adapter = new CategoryFilterAdapter(list, this);

        OnItemMoveListener itemMoveListener = new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();

                Collections.swap(list, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                categorySetting.setCategories(CategorySelect.toCategoryList(list));
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) { /* Unused */ }
        };

        SwipeItemClickListener itemClickListener = new SwipeItemClickListener()
        {
            @Override
            public void onItemClick(View itemView, int position)
            {
                list.get(position).toggle();
                adapter.notifyItemChanged(position);
                categorySetting.setCategories(CategorySelect.toCategoryList(list));
            }
        };

        rv = (SwipeMenuRecyclerView)findViewById(R.id.cat_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color)));
        rv.setAdapter(adapter);
        rv.setLongPressDragEnabled(true);
        rv.setOnItemMoveListener(itemMoveListener);
        rv.setSwipeItemClickListener(itemClickListener);

        Toast.makeText(this, R.string.cat_guide, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private static class CategorySelect
    {
        News.Category category;
        boolean enabled;

        CategorySelect(News.Category category, boolean enabled)
        {
            this.category = category;
            this.enabled = enabled;
        }

        void toggle() { enabled = !enabled; }

        static List<CategorySelect> fromCategoryList(List<News.Category> list)
        {
            List<CategorySelect> ret = new Vector<>();
            for (News.Category item : list)
                ret.add(new CategorySelect(item, true));
            for (int i = 1; i <= 12; i++)
            {
                News.Category category = News.Category.fromId(i);
                if (!list.contains(category))
                    ret.add(new CategorySelect(category, false));
            }
            return ret;
        }

        static List<News.Category> toCategoryList(List<CategorySelect> list)
        {
            List<News.Category> ret = new Vector<>();
            for (CategorySelect item : list)
                if (item.enabled)
                    ret.add(item.category);
            return ret;
        }
    }

    private static class CategoryFilterAdapter extends RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder>
    {
        private List<CategorySelect> list;
        Context context;

        public CategoryFilterAdapter(List<CategorySelect> list, Context context)
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
        public CategoryFilterActivity.CategoryFilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            return new CategoryFilterActivity.CategoryFilterAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item, parent, false));
        }

        @Override
        public void onBindViewHolder(CategoryFilterActivity.CategoryFilterAdapter.ViewHolder holder, int position)
        {
            holder.setData(list.get(position));
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView tvTitle;
            ImageView icon;

            public ViewHolder(View itemView)
            {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.cat_title);
                icon = (ImageView) itemView.findViewById(R.id.cat_icon);
            }

            public void setData(CategorySelect category)
            {
                this.tvTitle.setText(category.category.getStr());
                if (category.enabled)
                {
                    this.tvTitle.setTextColor(ContextCompat.getColor(context,
                            TransientSetting.isNightMode() ? R.color.font_disabled : R.color.font_enabled)
                    );
                    this.tvTitle.getPaint().setFlags(0);
                } else
                {
                    this.tvTitle.setTextColor(ContextCompat.getColor(context,
                            TransientSetting.isNightMode() ? R.color.font_enabled : R.color.font_disabled)
                    );
                    this.tvTitle.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
                this.icon.setImageResource(category.category.getIcon());
            }
        }
    }
}
