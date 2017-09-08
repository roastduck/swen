package com.swen;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageSearcher {
    final private String urlFormat = "http://image.baidu.com/search/index?tn=baiduimage&ps=1&ct=201326592&lm=-1&cl=2&nc=1&ie=utf-8&word=%s";
    private int maxSearchTimes;

    public ImageSearcher(int maxSearchTimes) {
        this.maxSearchTimes = maxSearchTimes;
    }

    public ImageSearcher() {
        this(1);
    }

    public String search(String keyword) throws IOException, ImageNotFoundException {
        String[] words = keyword.split("\\s+");
        String concat = "";
        for (String word: words) {
            concat += word + "+";
        }
        String content = (new Scanner(new URL(String.format(urlFormat, concat)).openStream())).useDelimiter("\\Z").next();
        Pattern pattern = Pattern.compile("\"objURL\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(content);

        String imgUrl;
        for (int k = 0; ; ++k) {
            if (!matcher.find()) {
                throw new ImageNotFoundException();
            }
            imgUrl = matcher.group(1);
            if (maxSearchTimes >0 && k == maxSearchTimes - 1 || checkImgUrl(imgUrl)) {
                break;
            }
        }
        return imgUrl;
    }

    private boolean checkImgUrl(String imgUrl) {
        try {
            HttpURLConnection conn = (HttpURLConnection)new URL(imgUrl).openConnection();
            return conn.getResponseCode() == 200;
        }
        catch (MalformedURLException e) {
            return false;
        }
        catch (IOException e) {
            return false;
        }
    }
}

class ImageNotFoundException extends Exception {
    @Override
    public String toString() {
        return "Not found appropriate image";
    }
}
