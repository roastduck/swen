package com.swen;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class External
{
    private MediaPlayer player, lastPlayer;
    private boolean shouldStopReading;
    private Object lock;
    private Thread readThread;
    private final String urlFormat = "http://tts.baidu.com/text2audio?lan=zh&ie=UTF-8&spd=5&text=%s";
    private final Context context;

    public External(final Context context) {
        this.context = context;
        this.readThread = null;
        this.shouldStopReading = false;
        this.lock = new Object();
        //ShareSDK.initSDK(context);
    }

    public void share(News news) {
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setTitle(news.news_Title);
        oks.setUrl(news.news_URL);
        oks.setTitleUrl(news.news_URL);
        List<String> picList = news.getNewsPictures();
        if (picList.size() >= 1) {
            oks.setImageUrl(news.getNewsPictures().get(0));    // TODO: parse news_Pictures
        }
        oks.setSite("Swen");
        oks.show(context);
    }

    public synchronized void readOut(String str) {
        if (readThread != null) {
            synchronized (lock) {
                shouldStopReading = true;
            }
            try {
                wait();
            }
            catch (InterruptedException e) {}
        }
        readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                readInThread(str);
            }
        });
        readThread.start();
    }

    private void readInThread(String str) {
        String[] segments = segment(str);
        lastPlayer = null;
        for (String seg: segments) {
            if (seg.equals("")) {
                continue;
            }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    player = new MediaPlayer();
                    try {
                        player.setDataSource(context, Uri.parse(String.format(urlFormat, seg)));
                        player.prepare();
                    }
                    catch (IOException e) {}
                }
            });

            synchronized (lock) {
                if (shouldStopReading) {
                    break;
                }
            }

            thread.start();
            try {
                thread.join();
            }
            catch (InterruptedException e) {}
            if (lastPlayer == null) {
                player.start();
            }
            else {
                lastPlayer.setNextMediaPlayer(player);
            }
            lastPlayer = player;
        }
        if (player != null) {
            player.release();
        }
        if (lastPlayer != null) {
            lastPlayer.release();
        }
        notify();
    }

    private String[] segment(String text) {
        String text0 = text.replaceAll("，|。|！|？|；|：|（|）", " ")
                .replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9 ]", "").trim();
        return text0.split("\\s+");
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        if(readThread != null) {
            readThread.interrupt();
        }
        if(player != null) {
            player.release();
        }
        if(lastPlayer != null) {
            lastPlayer.release();
        }
    }
}
