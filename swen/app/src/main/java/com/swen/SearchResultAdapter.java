package com.swen;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.Arrays;
import java.util.List;

class SearchResultAdapter extends BaseAdapter {
    private List<News> list;
    private String query;
    private String[] queryWords;
    private Context context;
    private LayoutInflater inflater;

    public SearchResultAdapter(List<News> list, String query, Context context) {
        this.list = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        this.queryWords = ToAnalysis.parse(query.trim()).toString().replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9/]|/\\w*", " ").split("\\s+");
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
        TextView titleTv = (TextView)convertView.findViewById(R.id.search_item_title);
        TextView digestTv = (TextView)convertView.findViewById(R.id.search_item_digest);

        String title = news.news_Title.trim();
        int len = title.length();
        boolean[] marked = new boolean[len];
        Arrays.fill(marked, false);
        for (String queryWord: queryWords) {
            int len1 = queryWord.length();
            for (int pos = 0; pos < len; ) {
                int tp = title.indexOf(queryWord, pos);
                if (tp == -1) {
                    break;
                }
                int l;
                for (pos = tp, l = 0; l < len1; ++l, ++pos) {
                    marked[pos] = true;
                }
            }
        }
        String markedText = "";
        for (int i = 0; i < len; ++i) {
            if ((i == 0 || !marked[i - 1]) && marked[i]) {
                markedText += "<font color='#ff0000'>";
            }
            if (i > 0 && marked[i - 1] && !marked[i]) {
                markedText += "</font>";
            }
            markedText += title.charAt(i);
        }
        if (marked[len - 1]) {
            markedText += "</font>";
        }

        titleTv.setText(Html.fromHtml(markedText));
        digestTv.setText(news.news_Intro.trim());

        return convertView;
    }
}
