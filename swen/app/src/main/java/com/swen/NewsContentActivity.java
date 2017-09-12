package com.swen;

import android.content.Intent;
import android.content.res.ObbInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.swen.promise.Callback;
import com.swen.promise.Promise;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * Created by Teon on 2017/9/11.
 */

public class NewsContentActivity extends BaseActivity {

    //TODO: update Behavior, grey the news item in parent list
    private News mNews;
    public static final String ACTION_NAME = "com.swen.action.CONTENT";
    private int mTotalPicture = 0;
    private int mShownPicture = 0;
    private List<Bitmap> mBitmaps = new ArrayList<>();
    private List<ImageView> mImageViews = new ArrayList<>();
    private List<TextView> mTextViews = new ArrayList<>();
    private LinearLayout mLinearLayout;
    private LinearLayout.LayoutParams mParam;
    private boolean mTried = false;
    private Timer mTimer = new Timer();
    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                showPicture();
            } else if(msg.what == 1) {
                Bundle bundle = msg.getData();
                String[] texts = bundle.getStringArray("texts");
                for(int i = 0; i < texts.length; i++) {
                    mTextViews.get(i).setText(Html.fromHtml(texts[i]));
                }
            }
            super.handleMessage(msg);
        }
    };
    private List<Promise> mPromises = new ArrayList<>();

    /* 加载过程：
     * 文字加载完成后，立即显示，并得知段数。根据pictures长度与文字段数来分配view。
     */

    protected synchronized void showPicture() {
        if(mImageViews.size() < mBitmaps.size()) {
            return;
        }
        for(int i = mShownPicture; i < mBitmaps.size(); i++) {
            mImageViews.get(i).setImageBitmap(mBitmaps.get(i));
        }
        mShownPicture = mBitmaps.size();
        if(mShownPicture == mTotalPicture) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    protected void tryShowPicture() {
        if(mTried) {
            return;
        }
        mTried = true;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        };
        mTimer.schedule(task, 500, 500);
    }

    protected void addImageView() {
        ImageView iv = new ImageView(this);
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        iv.setMaxWidth(screenWidth);
        iv.setMaxHeight(screenWidth * 5);
        iv.setMinimumHeight(screenWidth / 2);
        mLinearLayout.addView(iv, mParam);
        mImageViews.add(iv);
    }

    protected synchronized void computeLayout() {
        //TODO:处理新闻中的无用信息，相关新闻等
        String content = mNews.news_Content;
        String[] paragraph = content.split(" 　　");
        int paragraphCount = paragraph.length;
        mParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        if(mTotalPicture == 1) {
            int pos = paragraphCount / 5;
            for(int i = 0; i < pos; i++) {
                String text = "　　" + paragraph[i] + "\n";
                addTextView(text);
            }
            addImageView();
            for(int i = pos; i < paragraphCount; i++) {
                String text = "　　" + paragraph[i] + "\n";
                addTextView(text);
            }
        } else if(mTotalPicture >= paragraphCount) {
            int picturePerParagraph = mTotalPicture / paragraphCount;
            int picturesOfFirstParagraph = mTotalPicture - picturePerParagraph * (paragraphCount - 1);

            for(int i = 0; i < picturesOfFirstParagraph; i++) {
                addImageView();
            }
            addTextView(paragraph[0]);
            for(int i = 1; i < paragraphCount; i++) {
                for(int j = 0; j < picturePerParagraph; j++) {
                    addImageView();
                }
                String text = "　　" + paragraph[i] + "\n";
                addTextView(text);
                //Log.e("NewsContentActivity", text);
            }
        } else {
            int paragraphPerPicture = paragraphCount / mTotalPicture;
            int index = 0;
            for(int i = 0; i < mTotalPicture - 1; i++) {
                addImageView();
                int end = index + paragraphPerPicture;
                for(; index < end; index++) {
                    String text = "　　" + paragraph[index] + "\n";
                    addTextView(text);
                }
            }
            addImageView();
            for(; index < paragraphCount; index++) {
                String text = "　　" + paragraph[index] + "\n";
                addTextView(text);
            }
        }
        ContentPolisher.addHref(mNews, mTextViews, mHandler);
    }

    protected void addTextView(String text) {
        TextView textView = new TextView(this);
        textView.setVerticalScrollBarEnabled(true);
        textView.setScrollbarFadingEnabled(true);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(text);
        mLinearLayout.addView(textView, mParam);
        mTextViews.add(textView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for(Promise p: mPromises) {
            p.cancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        LinearLayout layout = (LinearLayout) findViewById(R.id.content_main);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout.addView(inflater.inflate(R.layout.news_content_page, null));

        mLinearLayout = (LinearLayout) findViewById(R.id.ll_content);
        Bundle bundle = getIntent().getExtras();
        String[] pictures = bundle.getStringArray(getString(R.string.bundle_news_pictures));
        String news_id = bundle.getString(getString(R.string.bundle_news_id));
        String news_title = bundle.getString(getString(R.string.bundle_news_title));

        ((TextView)findViewById(R.id.tv_content_title)).setText(news_title);
        Storage storage = ((ApplicationWithStorage)getApplication()).getStorage();
        Callback<Exception, Object> failCallback = new Callback<Exception, Object>() {
            @Override
            public Object run(Exception exception) {
                Toast.makeText(NewsContentActivity.this,
                    "新闻详情加载失败", Toast.LENGTH_SHORT).show();
                return null;
            }
        };
        Promise news_promise = storage.getNewsCached(news_id);
        mPromises.add(news_promise);
        news_promise.failUI(failCallback);
        news_promise.thenUI(new Callback<News, Object>() {
            @Override
            public Object run(News news) {
                //Toast.makeText(NewsContentActivity.this,
                //    "新闻详情加载完毕", Toast.LENGTH_SHORT).show();
                Log.e("NewsContentActivity", news.news_Content);
                Log.e("NewsContentActivity", news.news_Author);
                mNews = news;
                computeLayout();
                return null;
            }
        });
        if(pictures.length == 0) {
            Promise outterPromise = News.searchPicture(news_title);
            mPromises.add(outterPromise);
            outterPromise.then(new Callback<String, Object>() {
                @Override
                public Object run(String url) {
                    mTotalPicture = 1;
                    Promise innerPromise = ((ApplicationWithStorage)getApplication()).getStorage().
                        getPicCached(url);
                    mPromises.add(innerPromise);
                    innerPromise.thenUI(new Callback<Bitmap, Object>() {
                        @Override
                        public Object run(Bitmap picture) {
                            mBitmaps.add(picture);
                            tryShowPicture();
                            return null;
                        }
                    });
                    innerPromise.failUI(failCallback);
                    return null;
                }
            });
            outterPromise.failUI(failCallback);
        } else {
            mTotalPicture = pictures.length;
            for(String url: pictures) {
                Promise promise = ((ApplicationWithStorage)getApplication()).getStorage().
                    getPicCached(url);
                mPromises.add(promise);
                promise.thenUI(new Callback<Bitmap, Object>() {
                    @Override
                    public Object run(Bitmap picture) {
                        mBitmaps.add(picture);
                        tryShowPicture();
                        return null;
                    }
                });
                promise.failUI(failCallback);
            }
        }
    }

}
