package com.swen;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.List;

public class Behavior
{
    private Context context;
    private SharedPreferences sharedPreferences;
    /*
     * Keys in preferences:
     * CP_i means the category preference of the ith category. Value is an integer.
     * CH_i means whether the ith category is hided. Value is a boolean.
     * A keyword means the preference of this keyword. Value is an float.
     */


    public Behavior(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("readingPreferences", Context.MODE_PRIVATE);
    }

    public void markHaveRead(News news) {
        news.setAlreadyRead(true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            int category = news.getNewsClassTag().getId();
            int previousPref = sharedPreferences.getInt("CP_" + category, 0);
            editor.putInt("CP_" + category, previousPref + 1);
        } catch (News.Category.InvalidCategoryException e) {
            //TODO: invalid category. Temporarily ignore it.
            e.printStackTrace();
        }
        //TODO: keyword score
        List<News.WeightedKeyword> keywords = news.Keywords;
        if(keywords.isEmpty()) {
            return;
        }
        double maximumScore = keywords.get(0).score;
        for(News.WeightedKeyword wk: keywords) {
            String word = wk.word;
            double prefUpdate = wk.score / maximumScore;
            double previousPref = sharedPreferences.getFloat(word, 0);
            editor.putFloat(word, (float)(previousPref + prefUpdate));
        }
        editor.apply();
    }

    public double getPreference(News news) {}
}
