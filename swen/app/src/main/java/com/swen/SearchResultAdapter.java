package com.swen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

class SearchResultAdapter extends BaseAdapter {
    private List<News> list;
    private Context context;
    private LayoutInflater inflater;

    public SearchResultAdapter(List<News> list, Context context) {
        this.list = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_item, null);
        }
        News news = (News)getItem(position);
        TextView TitleTv = (TextView)convertView.findViewById(R.id.search_item_title);

        TitleTv.setText(news.news_Title);

        return convertView;
    }
}
