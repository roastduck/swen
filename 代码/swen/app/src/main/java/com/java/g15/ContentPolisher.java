package com.java.g15;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class ContentPolisher
{
    public static Thread addHref(News news, List<TextView> textViews, Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Log.e("ContentPolisher", "开始检索百度百科词条");
                ArrayList<String> keywords = new ArrayList<>();
                for(News.WeightedKeyword w: news.Keywords) {
                    keywords.add(w.word);
                }
                List<String> texts = new ArrayList<>();
                for(TextView tv: textViews) {
                    texts.add(tv.getText().toString());
                }
                String[] modified = texts.toArray(new String[texts.size()]);
                Log.e("ContentPolisher", "待检验数:" + (news.persons.size() + news.organizations.size()));
                if(news.locations != null) {
                    for (News.WordCnt wordCnt : news.locations) {
                        String word = wordCnt.word;
                        if(!keywords.contains(word) || !isBaikeItem(word)) {
                            continue;
                        }
                        String replacement = "<a href=\"https://baike.baidu.com/item/"
                            + word + "\">" + word + "</a>";
                        for(int i = 0; i < modified.length; i++) {
                            if(modified[i].contains(word)) {
                                modified[i] = modified[i].replaceFirst(word, replacement);
                                break;
                            }
                        }
                    }
                }
                if(news.persons != null) {
                    for (News.WordCnt wordCnt : news.persons) {
                        String word = wordCnt.word;
                        if((news.news_Author != null && word.contains(news.news_Author))
                            || !isBaikeItem(word)) {
                            continue;
                        }
                        String replacement = "<a href=\"https://baike.baidu.com/item/"
                            + word + "\">" + word + "</a>";
                        for(int i = 0; i < modified.length; i++) {
                            if(modified[i].contains(word)) {
                                modified[i] = modified[i].replaceFirst(word, replacement);
                                break;
                            }
                        }
                    }
                }
                if(news.organizations != null) {
                    for (News.WordCnt wordCnt : news.organizations) {
                        String word = wordCnt.word;
                        if(!isBaikeItem(word)) {
                            continue;
                        }
                        String replacement = "<a href=\"https://baike.baidu.com/item/"
                            + word + "\">" + word + "</a>";
                        for(int i = 0; i < modified.length; i++) {
                            if(modified[i].contains(word)) {
                                modified[i] = modified[i].replaceFirst(word, replacement);
                                break;
                            }
                        }
                    }
                }
                Log.e("ContentPolisher", "完成检索百度百科词条");
                Message msg = handler.obtainMessage();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putStringArray("texts", modified);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        };
        thread.start();
        return thread;
    }

    public static boolean isBaikeItem(String word) {
        StringBuffer sb = new StringBuffer();
        try {
            java.net.URL url = new java.net.URL("https://baike.baidu.com/item/" + word);
            BufferedReader in = new BufferedReader(new InputStreamReader(url
                .openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb.toString());
            in.close();
            if(!sb.toString().contains("<!--STATUS OK-->")) {
                Log.e("ContentPolisher", "No such item: " + word);
                return false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("ContentPolisher", "IOException: " + word);
            return false;
        }
        return true;
    }

}
