package com.swen;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.List;

/** User behavior statistics
 *  PLEASE DO NOT INSTANTIATE THIS CLASS, USE ((ApplicationWithStorage)getApplication()).getBehavior() INSTEAD
 */
public class Behavior
{
    private Context context;
    private SharedPreferences sharedPreferences;
    /*
     * Keys in preferences:
     * CP_i means the category preference of the ith category. Value is an integer, 1 as default.
     * CH_i means whether the ith category is hided. Value is a boolean, false as default.
     * A keyword means the preference of this keyword. Value is a float, 0 as default.
     */

    Behavior(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("readingPreferences", Context.MODE_PRIVATE);
    }

    public int getCategoryPreference(int i) {
        return sharedPreferences.getInt("CP_" + i, 1);
    }

    public synchronized void markHaveRead(News news) {
        if(news.isAlreadyRead()) {
            return;
        }
        news.setAlreadyRead(true);
        preferThisNews(news, 1);
    }

    public void like(News news) {
        preferThisNews(news, 2);
    }

    public void preferThisNews(News news, int ratio) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            int category = news.getNewsClassTag().getId();
            int previousPref = sharedPreferences.getInt("CP_" + category, 1);
            editor.putInt("CP_" + category, previousPref + ratio);
            editor.apply();
        } catch (News.Category.InvalidCategoryException e) {
            //TODO: invalid category. Temporarily ignore it.
            e.printStackTrace();
        }
        //TODO: keyword score
        List<News.WeightedKeyword> keywords = news.Keywords;
        if(keywords == null ||keywords.isEmpty()) {
            return;
        }
        double maximumScore = keywords.get(0).score;
        for(News.WeightedKeyword wk: keywords) {
            String word = wk.word;
            double prefUpdate = wk.score / maximumScore;
            double previousPref = sharedPreferences.getFloat(word, 0);
            editor.putFloat(word, (float)(previousPref + prefUpdate * ratio));
        }
        editor.apply();
    }

    public double getPreference(News news) {
        int category = -1;
        try {
            category = news.getNewsClassTag().getId();
            if(sharedPreferences.getBoolean("CH_" + category, false)) {
                return -1;
            }
        } catch (News.Category.InvalidCategoryException e) {
            //TODO: invalid category. Temporarily ignore it.
            e.printStackTrace();
        }
        int categoryPref = sharedPreferences.getInt("CP_" + category, 1);
        List<News.WeightedKeyword> keywords = news.Keywords;
        if(keywords == null ||keywords.isEmpty()) {
            return 0;
        }
        double score = 0;
        for(News.WeightedKeyword wk: keywords) {
            String word = wk.word;
            double pref = sharedPreferences.getFloat(word, 0);
            score += pref * wk.score;
        }
        return score * categoryPref / keywords.size();
    }
}
