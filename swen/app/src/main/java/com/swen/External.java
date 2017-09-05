package com.swen;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class External
{
    private MediaPlayer player;
    private Thread thread;
    private final String urlFormat = "http://tts.baidu.com/text2audio?lan=zh&ie=UTF-8&spd=5&text=%s";

    public class Target {}

    public void share(News news, Target target) {}

    public void readyRead(Context context, String str) {
        thread = new Thread(() ->
            player = MediaPlayer.create(context, Uri.parse(String.format(urlFormat, str)))
        );
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
