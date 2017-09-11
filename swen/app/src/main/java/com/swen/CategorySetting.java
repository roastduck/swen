package com.swen;

import android.content.Context;
import android.content.SharedPreferences;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

/** This class stores what categories are wanted by use
 *  PLEASE DO NOT INSTANTIATE THIS CLASS, USE ((ApplicationWithStorage)getApplication()).getCategorySetting() INSTEAD
 */
public class CategorySetting
{
    private SharedPreferences sharedPreferences;

    CategorySetting(Context context)
    {
        sharedPreferences = context.getSharedPreferences("categoryPreference", Context.MODE_PRIVATE);
    }

    public List<News.Category> getCategories()
    {
        return JSON.parseObject(sharedPreferences.getString("selectedCategories", "[]"), new TypeReference< List<News.Category> >(){});
    }

    public void setCategories(List<News.Category> list)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedCategories", JSON.toJSONString(list));
        editor.apply();
    }
}
