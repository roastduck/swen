package com.swen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swen.promise.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Teon on 2017/9/8.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NLViewHolder> {

    private boolean loading = false;
    private List<DemonstratedContent> mData;
    private AppendableNewsList mAppendableList;
    private Context mContext;
    private int lastIndex = 0;

    public NewsListAdapter(AppendableNewsList appendableList, Context context, Random random) {
        mData = DemonstratedContent.getDemonstratedContent(appendableList.list, random);
        notifyDataSetChanged();
        lastIndex = appendableList.list.size();
        mAppendableList = appendableList;
        mContext = context;
    }

    public void updateData(Random random) {
        DemonstratedContent.updateDemonstratedContent(mAppendableList.list,
            mData, lastIndex, random);
        lastIndex = mAppendableList.list.size();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).style;
    }

    @Override
    public NLViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_intro_1, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_intro_2, parent, false);
                break;
            case 3:
                view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_intro_3, parent, false);
                break;
            case 4:
                view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_intro_4, parent, false);
                break;
            default:
                view = null;
        }
        try {
            return new NLViewHolder(view, viewType);
        } catch (NLViewHolder.InvalidItemStyleException e) {
            //TODO:错误的ItemStyle
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBindViewHolder(NLViewHolder holder, int position) {
        switch (holder.style) {
            //TODO:显示什么文字内容？
            //TODO:如何显示图片？
            case 1: //TODO：显示图片，显示标题
                holder.textView.setText(mData.get(position).news.news_Title);
                break;
            case 2: //TODO: 显示图片，利用html区分标题和简介
                holder.textView.setText(mData.get(position).news.news_Title + "\n" + mData.get(position).news.news_Content);
                break;
            case 3: //TODO：分别显示左右分栏的图片和标题
                holder.textView.setText(mData.get(position).news.news_Title);
                holder.textViewRight.setText(mData.get(position).rightNews.news_Title);
                break;
            case 4: //TODO：显示图片，显示标题
                holder.textView.setText(mData.get(position).news.news_Title);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class NLViewHolder extends RecyclerView.ViewHolder {

        final public int style;
        private TextView textView;
        private TextView textViewRight;
        private ImageView imageView;
        private ImageView imageViewMid;
        private ImageView imageViewRight;

        public class InvalidItemStyleException extends Exception {
        }

        public NLViewHolder(View itemView, int style) throws InvalidItemStyleException {
            super(itemView);
            this.style = style;
            switch (style) {
                case 1:
                    textView = (TextView) itemView.findViewById(R.id.tv_intro1);
                    imageView = (ImageView) itemView.findViewById(R.id.iv_intro1);
                    break;
                case 2:
                    textView = (TextView) itemView.findViewById(R.id.tv_intro2);
                    imageView = (ImageView) itemView.findViewById(R.id.iv_intro2);
                    break;
                case 3:
                    textView = (TextView) itemView.findViewById(R.id.tv_intro3_left);
                    imageView = (ImageView) itemView.findViewById(R.id.iv_intro3_left);
                    textViewRight = (TextView) itemView.findViewById(R.id.tv_intro3_right);
                    imageViewRight = (ImageView) itemView.findViewById(R.id.iv_intro3_right);
                    break;
                case 4:
                    //TODO:若有时间，可改成不限图片数量的左右滑动ListView
                    textView = (TextView) itemView.findViewById(R.id.tv_intro4);
                    imageView = (ImageView) itemView.findViewById(R.id.iv_intro_4_left);
                    imageViewMid = (ImageView) itemView.findViewById(R.id.iv_intro_4_middle);
                    imageViewRight = (ImageView) itemView.findViewById(R.id.iv_intro_4_right);
                    break;
                default:
                    throw new InvalidItemStyleException();
            }
        }
    }
}
