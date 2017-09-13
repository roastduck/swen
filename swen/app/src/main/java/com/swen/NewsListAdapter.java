package com.swen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.swen.promise.Callback;

import org.w3c.dom.Text;

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
        for(int i = lastIndex; i < mData.size(); i++) {
            final News news = mData.get(i).news;
            if(news.getNewsPictures().isEmpty()) {
                News.searchPicture(news.news_Title)
                    .then(new Callback<String, Object>() {
                        @Override
                        public Object run(String url) {
                            news.setNews_Pictures(url);
                            return null;
                        }
                    });
            }
            final News news2 = mData.get(i).rightNews;
            if(news2 != null && news2.getNewsPictures().isEmpty()) {
                News.searchPicture(news2.news_Title)
                    .then(new Callback<String, Object>() {
                        @Override
                        public Object run(String url) {
                            news2.setNews_Pictures(url);
                            return null;
                        }
                    });
            }
        }
        lastIndex = mAppendableList.list.size();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(TransientSetting.isNoImage()) {
            return 5;
        }
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
            case 5:
                view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_intro_5, parent, false);
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

    public void showPicture(News news, LoadingImageView iv) {
        Activity activity = (Activity) mContext;
        Storage storage = ((ApplicationWithStorage) activity.getApplication())
            .getStorage();
        iv.clearPicture();
        if(news.getNewsPictures().isEmpty()) {
            News.searchPicture(news.news_Title)
                .then(new Callback<String, Object>() {
                    @Override
                    public Object run(String url) {
                        news.setNews_Pictures(url);
                        iv.showPictureByUrl(url, storage);
                        return null;
                    }
                });
        } else {
            iv.showPictureByUrl(news.getNewsPictures().get(0), storage);
        }
    }

    public void showPicture(News news, LoadingImageView iv, LoadingImageView ivmid, LoadingImageView ivright) {
        Activity activity = (Activity) mContext;
        Storage storage = ((ApplicationWithStorage) activity.getApplication())
            .getStorage();
        iv.clearPicture();
        ivmid.clearPicture();
        ivright.clearPicture();
        iv.showPictureByUrl(news.getNewsPictures().get(0), storage);
        ivmid.showPictureByUrl(news.getNewsPictures().get(1), storage);
        ivright.showPictureByUrl(news.getNewsPictures().get(2), storage);
    }

    public void setOnClickListener(View itemView, News news, int position, TextView title) {
        setOnClickListener(itemView, news, position, title, false);
    }

    public void setOnClickListener(View itemView, News news, int position, TextView title, boolean rightside) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(NewsContentActivity.ACTION_NAME);
                Bundle data = new Bundle();
                data.putString(mContext.getString(R.string.bundle_news_title), news.news_Title);
                data.putString(mContext.getString(R.string.bundle_news_id), news.news_ID);
                data.putStringArray(mContext.getString(R.string.bundle_news_pictures)
                    , news.getNewsPictures().toArray(new String[news.getNewsPictures().size()]));
                intent.putExtras(data);
                mContext.startActivity(intent);
                title.setText(Html.fromHtml("<font color=\"#576069\">" + title.getText() + "</font>"));
            }
        });
    }

    @Override
    public void onBindViewHolder(NLViewHolder holder, int position) {
        switch (holder.style) {
            case 1:
                showPicture(mData.get(position).news, holder.loadingImageView);
                if(mData.get(position).news.isAlreadyRead()) {
                    holder.textView.setText(Html.fromHtml("<font color=\"#576069\">"
                        + mData.get(position).news.news_Title + "</font>"));
                } else {
                    holder.textView.setText(mData.get(position).news.news_Title);
                }
                holder.textViewAnother.setText(mData.get(position).news.news_Intro
                    .replace("\\s+", "").replace(" ", "").replace("　", ""));
                setOnClickListener(holder.itemView, mData.get(position).news, position, holder.textView);
                break;
            case 2:
                showPicture(mData.get(position).news, holder.loadingImageView);
                /*
                holder.textView.setText(Html.fromHtml("<strong>" + mData.get(position).news.news_Title +
                    "</strong><br/><br/>" + mData.get(position).news.news_Intro
                    .replace(" ", "").replace("　", "")));
                */
                if(mData.get(position).news.isAlreadyRead()) {
                    holder.textView.setText(Html.fromHtml("<font color=\"#576069\">"
                        + mData.get(position).news.news_Title + "</font>"));
                } else {
                    holder.textView.setText(mData.get(position).news.news_Title);
                }
                holder.textViewAnother.setText(mData.get(position).news.news_Intro
                    .replace("\\s+", "").replace(" ", "").replace("　", ""));
                setOnClickListener(holder.itemView, mData.get(position).news, position, holder.textView);
                break;
            case 3:
                showPicture(mData.get(position).news, holder.loadingImageView);
                showPicture(mData.get(position).rightNews, holder.loadingImageViewRight);
                if(mData.get(position).news.isAlreadyRead()) {
                    holder.textView.setText(Html.fromHtml("<font color=\"#576069\">"
                        + mData.get(position).news.news_Title + "</font>"));
                } else {
                    holder.textView.setText(mData.get(position).news.news_Title);
                }
                if(mData.get(position).news.isAlreadyRead()) {
                    holder.textViewAnother.setText(Html.fromHtml("<font color=\"#576069\">"
                        + mData.get(position).rightNews.news_Title + "</font>"));
                } else {
                    holder.textViewAnother.setText(mData.get(position).rightNews.news_Title);
                }
                setOnClickListener(holder.itemView.findViewById(R.id.item_intro_3_left),
                    mData.get(position).news, position, holder.textView);
                setOnClickListener(holder.itemView.findViewById(R.id.item_intro_3_right),
                    mData.get(position).rightNews, position, holder.textView);
                break;
            case 4:
                showPicture(mData.get(position).news, holder.loadingImageView,
                    holder.loadingImageViewMid, holder.loadingImageViewRight);
                if(mData.get(position).news.isAlreadyRead()) {
                    holder.textView.setText(Html.fromHtml("<font color=\"#576069\">"
                        + mData.get(position).news.news_Title + "</font>"));
                } else {
                    holder.textView.setText(mData.get(position).news.news_Title);
                }
                setOnClickListener(holder.itemView, mData.get(position).news, position, holder.textView);
                break;
            case 5:
                if(mData.get(position).news.isAlreadyRead()) {
                    holder.textView.setText(Html.fromHtml("<font color=\"#576069\">"
                        + mData.get(position).news.news_Title + "</font>"));
                } else {
                    holder.textView.setText(mData.get(position).news.news_Title);
                }
                holder.textViewAnother.setText(mData.get(position).news.news_Intro
                    .replace("\\s+", "").replace(" ", "").replace("　", ""));
                setOnClickListener(holder.itemView, mData.get(position).news, position, holder.textView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class NLViewHolder extends RecyclerView.ViewHolder{

        final int style;
        final View itemView;
        private TextView textView;
        private TextView textViewAnother;
        private LoadingImageView loadingImageView;
        private LoadingImageView loadingImageViewMid;
        private LoadingImageView loadingImageViewRight;

        //TODO:添加左滑不感兴趣

        public class InvalidItemStyleException extends Exception {
        }

        public NLViewHolder(View itemView, int style) throws InvalidItemStyleException {
            super(itemView);
            this.style = style;
            this.itemView = itemView;
            switch (style) {
                case 1:
                    textView = (TextView) itemView.findViewById(R.id.tv_intro1);
                    textViewAnother = (TextView)itemView.findViewById(R.id.tv_intro1_1);
                    loadingImageView = (LoadingImageView) itemView.findViewById(R.id.iv_intro1);
                    break;
                case 2:
                    textView = (TextView) itemView.findViewById(R.id.tv_intro2);
                    textViewAnother = (TextView)itemView.findViewById(R.id.tv_intro2_1);
                    loadingImageView = (LoadingImageView) itemView.findViewById(R.id.iv_intro2);
                    break;
                case 3:
                    textView = (TextView) itemView.findViewById(R.id.tv_intro3_left);
                    loadingImageView = (LoadingImageView) itemView.findViewById(R.id.iv_intro3_left);
                    textViewAnother = (TextView) itemView.findViewById(R.id.tv_intro3_right);
                    loadingImageViewRight = (LoadingImageView) itemView.findViewById(R.id.iv_intro3_right);
                    break;
                case 4:
                    //TODO:若有时间，可改成不限图片数量的左右滑动ListView
                    textView = (TextView) itemView.findViewById(R.id.tv_intro4);
                    loadingImageView = (LoadingImageView) itemView.findViewById(R.id.iv_intro_4_left);
                    loadingImageViewMid = (LoadingImageView) itemView.findViewById(R.id.iv_intro_4_middle);
                    loadingImageViewRight = (LoadingImageView) itemView.findViewById(R.id.iv_intro_4_right);
                    break;
                case 5:
                    textView = (TextView) itemView.findViewById(R.id.tv_intro5_title);
                    textViewAnother = (TextView) itemView.findViewById(R.id.tv_intro5_intro);
                    break;
                default:
                    throw new InvalidItemStyleException();
            }
        }
    }
}
