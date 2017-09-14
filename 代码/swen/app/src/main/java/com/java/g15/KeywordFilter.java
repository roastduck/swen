package com.java.g15;

import android.content.Context;
import android.content.SharedPreferences;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

/** What keywords should be filtered out
 *  PLEASE DO NOT INSTANTIATE THIS CLASS, USE ((ApplicationWithStorage)getApplication()).getKeywordFilter() INSTEAD
 */
public class KeywordFilter
{
    private SharedPreferences sharedPreferences;

    KeywordFilter(Context context)
    {
        sharedPreferences = context.getSharedPreferences("keyword_filter", Context.MODE_PRIVATE);
    }

    public List<String> getList()
    {
        return JSON.parseObject(sharedPreferences.getString("words", "[]"), new TypeReference< List<String> >() {});
    }

    public void setList(List<String> list)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("words", JSON.toJSONString(list));
        editor.apply();
    }
}
