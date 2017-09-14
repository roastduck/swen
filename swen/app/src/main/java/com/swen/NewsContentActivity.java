package com.swen;

import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.swen.promise.Callback;
import com.swen.promise.Promise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teon on 2017/9/11.
 */

public class NewsContentActivity extends BaseActivity implements View.OnClickListener {

    public static final String ACTION_NAME = "com.swen.action.CONTENT";
    protected boolean mIsRefreshing = false;
    //TODO: grey the news item in parent list
    private News mNews = null;
    private List<String> mUrls;
    private List<LoadingImageView> mImageViews;
    private List<TextView> mTextViews;
    private LinearLayout mLinearLayout;
    private MenuItem mLike;
    private boolean mMarked = false;
    private External mExternal = new External(this);
    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Bundle bundle = msg.getData();
                String[] texts = bundle.getStringArray("texts");
                for (int i = 0; i < texts.length; i++) {
                    mTextViews.get(i).setText(Html.fromHtml(texts[i] + "<br/>"));
                }
            }
            super.handleMessage(msg);
        }
    };
    private FloatingActionButton mShare;
    private FloatingActionButton mRead;
    private List<Promise> mPromises;
    private Thread mThread;
    private Callback mFailCallback;
    private LinearLayout mRootLayout;

    /* 加载过程：
     * 文字加载完成后，立即显示，并得知段数。根据pictures长度与文字段数来分配view。
     */
    private int mTotalPictures;

    protected void addImageView(String url) {
        FrameLayout spacing = new FrameLayout(this);
        spacing.setMinimumHeight(12);
        mLinearLayout.addView(spacing);
        LoadingImageView iv = new LoadingImageView(this);
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        iv.getImageView().setBackgroundColor(getResources().getColor(R.color.transparent));
        iv.getImageView().setMaxWidth(screenWidth);
        iv.getImageView().setMaxHeight(screenWidth * 5);
        iv.getImageView().setMinimumHeight(screenWidth / 2);
        iv.getImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(1, 8, 1, 8);
        mLinearLayout.addView(iv, param);
        mImageViews.add(iv);
        mPromises.add(iv.showPictureByUrl(url,
            ((ApplicationWithStorage) getApplication()).getStorage(), mFailCallback));
        FrameLayout spacing2 = new FrameLayout(this);
        spacing2.setMinimumHeight(12);
        mLinearLayout.addView(spacing2);
    }

    protected synchronized void computeLayout() {
        //TODO:处理新闻中的无用信息，相关新闻等
        String header = "<ul><li><i><font color='gray'>作者："
                + mNews.news_Author
                + "</font></i></li>"
                + "<li><i><font color='gray'>日期："
                + new SimpleDateFormat("yyyy-MM-dd").format(mNews.getNewsTime().getTime())
                + "</font></i></li></ul>";
        ((TextView) findViewById(R.id.after_title)).setText(Html.fromHtml(header));
        String content = mNews.news_Content;
        String[] paragraph = content.split(" 　　");
        int paragraphCount = paragraph.length;
        if (TransientSetting.isNoImage() || mUrls.isEmpty()) { // 从收藏夹进入的时候，或是还没来得及的时候，没有图片
            for (int i = 0; i < paragraphCount; i++) {
                String text = "　　" + paragraph[i].replaceAll("　", "") + "\n";
                addTextView(text);
            }
            mThread = ContentPolisher.addHref(mNews, mTextViews, mHandler);
            return;
        }
        if (mTotalPictures == 1) {
            int pos = paragraphCount / 5;
            for (int i = 0; i < pos; i++) {
                String text = paragraph[i].replaceAll("　", "") + "\n";
                addTextView(text);
            }
            addImageView(mUrls.get(0));
            for (int i = pos; i < paragraphCount; i++) {
                String text = paragraph[i].replaceAll("　", "") + "\n";
                addTextView(text);
            }
        } else if (mTotalPictures >= paragraphCount) {
            int picturePerParagraph = mTotalPictures / paragraphCount;
            int picturesOfFirstParagraph = mTotalPictures - picturePerParagraph * (paragraphCount - 1);
            int index = 0;
            for (int i = 0; i < picturesOfFirstParagraph; i++) {
                addImageView(mUrls.get(index++));
            }
            addTextView(paragraph[0].replaceAll("　", "") + "\n");
            for (int i = 1; i < paragraphCount; i++) {
                for (int j = 0; j < picturePerParagraph; j++) {
                    addImageView(mUrls.get(index++));
                }
                String text = paragraph[i].replaceAll("　", "") + "\n";
                addTextView(text);
                //Log.e("NewsContentActivity", text);
            }
        } else {
            int paragraphPerPicture = paragraphCount / mTotalPictures;
            int index = 0;
            int urlIndex = 0;
            for (int i = 0; i < mTotalPictures - 1; i++) {
                addImageView(mUrls.get(urlIndex++));
                int end = index + paragraphPerPicture;
                for (; index < end; index++) {
                    String text = paragraph[index].replaceAll("　", "") + "\n";
                    addTextView(text);
                }
            }
            addImageView(mUrls.get(urlIndex++));
            for (; index < paragraphCount; index++) {
                String text = paragraph[index].replaceAll("　", "") + "\n";
                addTextView(text);
            }
        }
        mThread = ContentPolisher.addHref(mNews, mTextViews, mHandler);
    }

    protected void addTextView(String text) {
        TextView textView = new TextView(this);
        textView.setVerticalScrollBarEnabled(true);
        textView.setScrollbarFadingEnabled(true);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextSize(15);
        textView.setText(text);
        if (TransientSetting.isNightMode()) {
            textView.setTextColor(getResources().getColor(R.color.intro_night));
        }
        textView.setLineSpacing(0, (float)1.5);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(1, 5, 1, 5);
        mLinearLayout.addView(textView, param);
        mTextViews.add(textView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPromises != null) {
            for (Promise p : mPromises) {
                p.cancel();
            }
        }
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.content_menu, menu);

        mLike = menu.findItem(R.id.item_like);
        if (mMarked) {
            mLike.setIcon(R.drawable.marked);
        } else {
            mLike.setIcon(R.drawable.unmarked);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_like:
                if (mNews == null) {
                    return true;
                }
                if (mMarked) {
                    ((ApplicationWithStorage) getApplication()).getBehavior().like(mNews);
                    Promise promise = ((ApplicationWithStorage) getApplication())
                        .getStorage().unmark(mNews.news_ID);
                    mLike.setEnabled(false);
                    mPromises.add(promise);
                    promise.failUI(new Callback() {
                        @Override
                        public Object run(Object result) throws Exception {
                            Toast.makeText(NewsContentActivity.this,
                                "取消收藏失败", Toast.LENGTH_SHORT).show();
                            mLike.setEnabled(true);
                            return null;
                        }
                    });
                    promise.thenUI(new Callback() {
                        @Override
                        public Object run(Object result) throws Exception {
                            Toast.makeText(NewsContentActivity.this,
                                "成功取消收藏", Toast.LENGTH_SHORT).show();
                            mLike.setIcon(R.drawable.unmarked);
                            mMarked = false;
                            mLike.setEnabled(true);
                            return null;
                        }
                    });
                } else {
                    Promise promise = ((ApplicationWithStorage) getApplication())
                        .getStorage().mark(mNews.news_ID);
                    mLike.setEnabled(false);
                    mPromises.add(promise);
                    promise.failUI(new Callback() {
                        @Override
                        public Object run(Object result) throws Exception {
                            Toast.makeText(NewsContentActivity.this,
                                "收藏失败", Toast.LENGTH_SHORT).show();
                            mLike.setEnabled(true);
                            return null;
                        }
                    });
                    promise.thenUI(new Callback() {
                        @Override
                        public Object run(Object result) throws Exception {
                            Toast.makeText(NewsContentActivity.this,
                                "成功收藏", Toast.LENGTH_SHORT).show();
                            mLike.setIcon(R.drawable.marked);
                            mMarked = true;
                            mLike.setEnabled(true);
                            return null;
                        }
                    });
                }
                return true;
            case 16908332:       // do not know what the corresponding 'R.id' but it works
                finish();
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    protected void updateUI() {
        showNews();
        LayoutInflater inflater = LayoutInflater.from(this);
        mRootLayout.addView(inflater.inflate(R.layout.news_content_page, null));
        if (TransientSetting.isNightMode()) {
            mRootLayout.setBackgroundColor(getResources().getColor(R.color.foreground_dark));
        }
        mUrls = new ArrayList<>(); // 这些变量不能在函数外初始化
        mImageViews = new ArrayList<>();
        mTextViews = new ArrayList<>();
        mPromises = new ArrayList<>();
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_content);
        if (TransientSetting.isNightMode()) {
            mLinearLayout.setBackgroundColor(getResources().getColor(R.color.foreground_dark));
        }
        mShare = (FloatingActionButton) findViewById(R.id.bt_share);
        mRead = (FloatingActionButton) findViewById(R.id.bt_read);
        mShare.setEnabled(false);
        mRead.setEnabled(false);
        mShare.setOnClickListener(this);
        mRead.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        String[] pictures = bundle.getStringArray(getString(R.string.bundle_news_pictures));
        String news_id = bundle.getString(getString(R.string.bundle_news_id));
        String news_title = bundle.getString(getString(R.string.bundle_news_title));
        new Promise<Object, Boolean>(new Callback<Object, Boolean>() {
            @Override
            public Boolean run(Object o) {
                return ((ApplicationWithStorage) getApplication()).getStorage().isMarked(news_id);
            }
        }, null).thenUI(new Callback<Boolean, Object>() {
            @Override
            public Object run(Boolean b) {
                mMarked = b;
                if (mMarked) {
                    mLike.setIcon(R.drawable.marked);
                } else {
                    mLike.setIcon(R.drawable.unmarked);
                }
                return new Object();
            }
        });

        TextView title = (TextView) findViewById(R.id.tv_content_title);
        title.setText(news_title);
        if (TransientSetting.isNightMode()) {
            title.setTextColor(getResources().getColor(R.color.title_night));
        }
        Storage storage = ((ApplicationWithStorage) getApplication()).getStorage();
        mFailCallback = new Callback<Exception, Object>() {
            @Override
            public Object run(Exception exception) {
                Toast.makeText(NewsContentActivity.this,
                    "新闻详情加载失败", Toast.LENGTH_SHORT).show();
                return null;
            }
        };

        Callback<News, Object> computeLayoutCallback = new Callback<News, Object>() {
            @Override
            public Object run(News news) {
                mNews = news;
                mShare.setEnabled(true);
                mRead.setEnabled(true);
                ((ApplicationWithStorage) getApplication()).getBehavior().markHaveRead(mNews);
                computeLayout();
                return null;
            }
        };

        Promise news_promise = storage.getNewsCached(news_id);
        mPromises.add(news_promise);

        news_promise.failUI(mFailCallback);
        if (TransientSetting.isNoImage()) {
            mTotalPictures = 0;
            news_promise.thenUI(computeLayoutCallback);
            return;
        }
        mTotalPictures = pictures.length == 0 ? 1 : pictures.length;
        news_promise.thenUI(computeLayoutCallback).failUI(new Callback<Exception, Object>() {
            @Override
            public Object run(Exception result) throws Exception {
                for (StackTraceElement e : result.getStackTrace()) {
                    Log.e("NCA", e.toString());
                }
                showNoNetwork();
                return null;
            }
        });
        if (pictures.length == 0) {

            Promise outterPromise = News.searchPicture(news_title);
            mPromises.add(outterPromise);
            outterPromise.then(new Callback<String, Object>() {
                @Override
                public Object run(String url) {
                    mUrls.add(url);
                    news_promise.thenUI(computeLayoutCallback); // 重新加载
                    return null;
                }
            });
            outterPromise.failUI(mFailCallback);
        } else {
            for (String url : pictures)
                mUrls.add(url);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mRootLayout = (LinearLayout) findViewById(R.id.content_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNoNetwork = (FrameLayout) findViewById(R.id.fl_no_network);
        mHint = (TextView) findViewById(R.id.tv_no_network);
        mNoNetwork.setOnClickListener(this);
        mErrorNotified = false;

        updateUI();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_read:
                mExternal.readOut(mNews.news_Content);
                break;
            case R.id.bt_share:
                mExternal.share(mNews, mUrls.get(0));
                break;
            case R.id.fl_no_network:
                if (mIsRefreshing) {
                    return;
                }
                mIsRefreshing = true;
                if (isNetworkConnected()) {
                    updateUI();
                    mIsRefreshing = false;
                } else {
                    showNoNetwork();
                    mIsRefreshing = false;
                }
                break;
        }
    }
}
