package com.swen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Teon on 2017/9/8.
 */

public class RecommendationView extends RecyclerView {

    private List<News> mData;
    private AppendableNewsList mAppendableList;

    public RecommendationView(Context context, AppendableNewsList appendableNewsList) {
        ;
    }

    class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RAViewHolder> {

        @Override
        public int getItemViewType(int position) {
            //TODO:根据新闻图片内容选择item style
            return 1;
        }

        @Override
        public RAViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
                return new RAViewHolder(view, viewType);
            } catch (RAViewHolder.InvalidItemStyleException e) {
                //TODO:错误的ItemStyle
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(RAViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class RAViewHolder extends RecyclerView.ViewHolder {

            final private int style;
            private TextView textView;
            private TextView textViewRight;
            private ImageView imageView;
            private ImageView imageViewMid;
            private ImageView imageViewRight;

            public class InvalidItemStyleException extends Exception {
            }

            public RAViewHolder(View itemView, int style) throws InvalidItemStyleException {
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
}
