package com.swen;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class External
{
    private MediaPlayer player;
    private Thread thread;
    private final String urlFormat = "http://tts.baidu.com/text2audio?lan=zh&ie=UTF-8&spd=5&text=%s";
    private final Context context;

    public External(final Context context) {
        this.context = context;
        ShareSDK.initSDK(context);
    }

    public void share(News news) {
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setTitle(news.news_Title);
        oks.setUrl(news.news_URL);
        oks.setTitleUrl(news.news_URL);
        oks.setImageUrl(news.news_Pictures);    // TODO: parse news_Pictures
        oks.setSite("Swen");
        oks.show(context);
    }

    public void readyRead(final String str) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                player = MediaPlayer.create(context, Uri.parse(String.format(urlFormat, str)));
            }
        });
        thread.start();
    }

    public void readOut() {
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            thread.interrupt();
            player.release();
        }
        player.start();
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        thread.interrupt();
        player.release();
    }
}
