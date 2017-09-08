package com.swen;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jdeferred.android.AndroidDoneCallback;
import org.jdeferred.android.AndroidExecutionScope;
import org.jdeferred.android.AndroidFailCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Teon on 2017/9/8.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NLViewHolder> {

    private boolean loading = false;
    private HashMap<String, News> neighbor = new HashMap<>();
    private List<News> mData;
    private AppendableNewsList mAppendableList;
    private Context mContext;

    public NewsListAdapter(List<News> data, AppendableNewsList appendableList, Context context) {
        mData = data;
        mAppendableList = appendableList;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        int threshold = (int) (getItemCount() * 0.8);
        if (!loading && position >= threshold) {
            loading = true;
            mAppendableList.append().then(new AndroidDoneCallback() {
                @Override
                public void onDone(Object result) {
                    notifyDataSetChanged();
                    loading = false;
                }

                @Override
                public AndroidExecutionScope getExecutionScope() {
                    return AndroidExecutionScope.UI;
                }
            }).fail(new AndroidFailCallback() {
                @Override
                public void onFail(Object result) {
                    Toast.makeText(mContext, "未能获取更多新闻条目", Toast.LENGTH_LONG);
                    loading = false;
                }

                @Override
                public AndroidExecutionScope getExecutionScope() {
                    return AndroidExecutionScope.UI;
                }
            });
        }
        List<String> pictures = mData.get(position).getNewsPictures();
        if (pictures.isEmpty()) {
            mData.remove(position);
            notifyDataSetChanged();
        }
        if (position == 0) {     //First item in view
            return 1;
        }
        if (pictures.size()
            >= 5) {      //If this news contains many pictures, then we try to show more of them
            return 4;
        }
        int style = 0;
        if (pictures.size() >= 3) {
            Random rnd = new Random(System.currentTimeMillis());
            style = rnd.nextInt(4) + 1;
        } else {
            Random rnd = new Random(System.currentTimeMillis());
            style = rnd.nextInt(3) + 1;
        }
        if (style == 3) {
            News nextNews = mData.remove(position + 1);
            notifyDataSetChanged();
            neighbor.put(mData.get(position).news_ID, nextNews);
        }
        return style;
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
                break;
            case 2: //TODO: 显示图片，利用html区分标题和简介
                break;
            case 3: //TODO：分别显示左右分栏的图片和标题
                News left = mData.get(position);
                News right = neighbor.get(left.news_ID);
                break;
            case 4: //TODO：显示图片，显示标题
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
