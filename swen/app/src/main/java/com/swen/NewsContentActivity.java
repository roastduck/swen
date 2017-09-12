package com.swen;

import android.os.Bundle;
import android.os.Message;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.swen.promise.Callback;
import com.swen.promise.Promise;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teon on 2017/9/11.
 */

public class NewsContentActivity extends BaseActivity implements View.OnClickListener {

    //TODO: grey the news item in parent list
    private News mNews = null;
    public static final String ACTION_NAME = "com.swen.action.CONTENT";
    private List<String> mUrls = new ArrayList<>();
    private List<LoadingImageView> mImageViews = new ArrayList<>();
    private List<TextView> mTextViews = new ArrayList<>();
    private LinearLayout mLinearLayout;
    private LinearLayout.LayoutParams mParam;
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
                    mTextViews.get(i).setText(Html.fromHtml(texts[i]));
                }
            }
            super.handleMessage(msg);
        }
    };
    private FloatingActionButton mShare;
    private FloatingActionButton mRead;
    private List<Promise> mPromises = new ArrayList<>();
    private Thread mThread;
    private Callback mFailCallback;

    /* 加载过程：
     * 文字加载完成后，立即显示，并得知段数。根据pictures长度与文字段数来分配view。
     */

    protected void addImageView(String url) {
        LoadingImageView iv = new LoadingImageView(this);
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        iv.getImageView().setBackgroundColor(getResources().getColor(R.color.transparent));
        iv.getImageView().setMaxWidth(screenWidth);
        iv.getImageView().setMaxHeight(screenWidth * 5);
        iv.getImageView().setMinimumHeight(screenWidth / 2);
        iv.getImageView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        mParam.setMargins(1, 4, 1, 4);
        mLinearLayout.addView(iv, mParam);
        mImageViews.add(iv);
        mPromises.add(iv.showPictureByUrl(url,
            ((ApplicationWithStorage) getApplication()).getStorage(), mFailCallback));

    }

    protected synchronized void computeLayout() {
        //TODO:处理新闻中的无用信息，相关新闻等
        String content = mNews.news_Content;
        String[] paragraph = content.split(" 　　");
        int paragraphCount = paragraph.length;
        mParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        if (TransientSetting.isNoImage()) {
            for (int i = 0; i < paragraphCount; i++) {
                String text = "　　" + paragraph[i].replaceFirst("　", "") + "\n";
                addTextView(text);
            }
            mThread = ContentPolisher.addHref(mNews, mTextViews, mHandler);
            return;
        }
        if (mUrls.size() == 1) {
            int pos = paragraphCount / 5;
            for (int i = 0; i < pos; i++) {
                String text = "　　" + paragraph[i].replaceFirst("　", "") + "\n";
                addTextView(text);
            }
            addImageView(mUrls.get(0));
            for (int i = pos; i < paragraphCount; i++) {
                String text = "　　" + paragraph[i].replaceFirst("　", "") + "\n";
                addTextView(text);
            }
        } else if (mUrls.size() >= paragraphCount) {
            int picturePerParagraph = mUrls.size() / paragraphCount;
            int picturesOfFirstParagraph = mUrls.size() - picturePerParagraph * (paragraphCount - 1);
            int index = 0;
            for (int i = 0; i < picturesOfFirstParagraph; i++) {
                addImageView(mUrls.get(index++));
            }
            addTextView(paragraph[0]);
            for (int i = 1; i < paragraphCount; i++) {
                for (int j = 0; j < picturePerParagraph; j++) {
                    addImageView(mUrls.get(index++));
                }
                String text = "　　" + paragraph[i].replaceFirst("　", "") + "\n";
                addTextView(text);
                //Log.e("NewsContentActivity", text);
            }
        } else {
            int paragraphPerPicture = paragraphCount / mUrls.size();
            int index = 0;
            int urlIndex = 0;
            for (int i = 0; i < mUrls.size() - 1; i++) {
                addImageView(mUrls.get(urlIndex++));
                int end = index + paragraphPerPicture;
                for (; index < end; index++) {
                    String text = "　　" + paragraph[index].replaceFirst("　", "") + "\n";
                    addTextView(text);
                }
            }
            addImageView(mUrls.get(urlIndex++));
            for (; index < paragraphCount; index++) {
                String text = "　　" + paragraph[index].replaceFirst("　", "") + "\n";
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
        textView.setTextSize(18);
        textView.setText(text);
        mParam.setMargins(1, 2, 1, 2);
        mLinearLayout.addView(textView, mParam);
        mTextViews.add(textView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Promise p : mPromises) {
            p.cancel();
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

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        LinearLayout layout = (LinearLayout) findViewById(R.id.content_main);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout.addView(inflater.inflate(R.layout.news_content_page, null));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLinearLayout = (LinearLayout) findViewById(R.id.ll_content);
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

        mMarked = ((ApplicationWithStorage) getApplication()).getStorage().isMarked(news_id);
        ((TextView) findViewById(R.id.tv_content_title)).setText(news_title);
        Storage storage = ((ApplicationWithStorage) getApplication()).getStorage();
        mFailCallback = new Callback<Exception, Object>() {
            @Override
            public Object run(Exception exception) {
                Toast.makeText(NewsContentActivity.this,
                    "新闻详情加载失败", Toast.LENGTH_SHORT).show();
                return null;
            }
        };
        Promise news_promise = storage.getNewsCached(news_id);
        mPromises.add(news_promise);

        news_promise.failUI(mFailCallback);
        news_promise.thenUI(new Callback<News, Object>() {
            @Override
            public Object run(News news) {
                //Toast.makeText(NewsContentActivity.this,
                //    "新闻详情加载完毕", Toast.LENGTH_SHORT).show();
                Log.e("NewsContentActivity", news.news_Content);
                Log.e("NewsContentActivity", news.news_Author);
                mNews = news;
                mShare.setEnabled(true);
                mRead.setEnabled(true);
                ((ApplicationWithStorage) getApplication()).getBehavior().markHaveRead(mNews);
                computeLayout();
                return null;
            }
        });
        if (TransientSetting.isNoImage()) {
            return;
        }
        if (pictures.length == 0) {
            Promise outterPromise = News.searchPicture(news_title);
            mPromises.add(outterPromise);
            outterPromise.then(new Callback<String, Object>() {
                @Override
                public Object run(String url) {
                    mUrls.add(url);
//                    Promise innerPromise = ((ApplicationWithStorage)getApplication()).getStorage().
//                        getPicCached(url);
//                    mPromises.add(innerPromise);
//                    innerPromise.thenUI(new Callback<Bitmap, Object>() {
//                        @Override
//                        public Object run(Bitmap picture) {
//                            mBitmaps.add(picture);
//                            tryShowPicture();
//                            return null;
//                        }
//                    });
//                    innerPromise.failUI(failCallback);
                    return null;
                }
            });
            outterPromise.failUI(mFailCallback);
        } else {
            for (String url : pictures) {
                mUrls.add(url);
//                Promise promise = ((ApplicationWithStorage)getApplication()).getStorage().
//                    getPicCached(url);
//                mPromises.add(promise);
//                promise.thenUI(new Callback<Bitmap, Object>() {
//                    @Override
//                    public Object run(Bitmap picture) {
//                        mBitmaps.add(picture);
//                        tryShowPicture();
//                        return null;
//                    }
//                });
//                promise.failUI(failCallback);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_read:
                mExternal.readOut(mNews.news_Content);
                break;
            case R.id.bt_share:
                mExternal.share(mNews);
                break;
        }
    }
}
