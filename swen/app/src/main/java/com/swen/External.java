package com.swen;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class External
{
    private MediaPlayer player, lastPlayer, finalPlayer;
    private ConcurrentLinkedQueue<MediaPlayer> playerQueue;
    private Semaphore semPrepare, semUse, semPlay;
    private boolean finishedPlaying;
    private boolean shouldStopReading;
    private Object lock1, lock2;
    private Thread readThread;
    private final String urlFormat = "http://tts.baidu.com/text2audio?lan=zh&ie=UTF-8&spd=5&text=%s";
    private final Context context;

    public External(final Context context) {
        this.context = context;
        this.playerQueue = new ConcurrentLinkedQueue<>();
        this.readThread = null;
        this.semPrepare = new Semaphore(10);
        this.semUse = new Semaphore(0);
        this.semPlay = new Semaphore(1);
        this.finishedPlaying = false;
        this.shouldStopReading = false;
        this.lock1 = new Object();
        this.lock2 = new Object();
    }

    public void share(News news, String imgUrl) {
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setTitle(news.news_Title);
        oks.setUrl(news.news_URL);
        oks.setTitleUrl(news.news_URL);
        oks.setImageUrl(imgUrl);    // TODO: parse news_Pictures
        oks.setSite("Swen");
        oks.show(context);
    }

    public void readOut(String text) {
        String[] segments = segment(text);

        Thread prepareThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer tempPlayer = null;
                for (String seg: segments) {
                    if (seg.equals("")) {
                        continue;
                    }
                    try {
                        tempPlayer = MediaPlayer.create(context, Uri.parse(String.format(urlFormat, seg)));
                        semPrepare.acquire();
                        playerQueue.add(tempPlayer);
                        semUse.release();
                    }
                    catch (InterruptedException e) {}
                    catch (IllegalStateException e) {}
                }

                synchronized (lock1) {
                    finalPlayer = tempPlayer;
                }
            }
        });
        Thread playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        semPlay.acquire();
                    }
                    catch (InterruptedException e) {}
                    synchronized (lock2) {
                        if (finishedPlaying) {
                            break;
                        }
                    }
                    try {
                        semUse.acquire();
                    }
                    catch (InterruptedException e) {}

                    MediaPlayer tempPlayer = playerQueue.remove();
                    semPrepare.release();
                    tempPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            boolean fin;
                            synchronized (lock1) {
                                fin = (mp == finalPlayer);
                            }
                            if (fin) {
                                synchronized (lock2) {
                                    finishedPlaying = true;
                                }
                            }
                            semPlay.release();
                            mp.release();
                        }
                    });
                    tempPlayer.start();
                }
            }
        });

        prepareThread.start();
        playThread.start();
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
