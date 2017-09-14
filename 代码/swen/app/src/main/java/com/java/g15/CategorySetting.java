package com.java.g15;

import android.content.Context;
import android.content.SharedPreferences;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Vector;

/** This class stores what categories are wanted by use
 *  PLEASE DO NOT INSTANTIATE THIS CLASS, USE ((ApplicationWithStorage)getApplication()).getCategorySetting() INSTEAD
 */
public class CategorySetting
{
    private SharedPreferences sharedPreferences;
    private final String initStr;

    CategorySetting(Context context)
    {
        sharedPreferences = context.getSharedPreferences("categoryPreference", Context.MODE_PRIVATE);
        List<News.Category> initList = new Vector<>();
        for (int i = 1; i <= 12; i++)
            initList.add(News.Category.fromId(i));
        initStr = JSON.toJSONString(initList);
    }

    public List<News.Category> getCategories()
    {
        return JSON.parseObject(sharedPreferences.getString("selectedCategories", initStr), new TypeReference< List<News.Category> >(){});
    }

    public void setCategories(List<News.Category> list)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedCategories", JSON.toJSONString(list));
        editor.apply();
    }
}
