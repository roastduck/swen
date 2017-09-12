package com.swen;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Vector;

import static org.junit.Assert.*;

public class CategorySettingTest
{
    @Rule
    public ActivityTestRule<RecommendationActivity> mActivityRule = new ActivityTestRule<>(RecommendationActivity.class);

    CategorySetting categorySetting;

    private void clearSharedPreference()
    {
        SharedPreferences.Editor editor = mActivityRule.getActivity().getApplicationContext()
                .getSharedPreferences("categoryPreference", Context.MODE_PRIVATE)
                .edit();
        editor.remove("selectedCategories");
        editor.apply();
    }

    @Before
    public void setUp() throws Exception
    {
        categorySetting = ((ApplicationWithStorage)(mActivityRule.getActivity().getApplication())).getCategorySetting();
        clearSharedPreference();
    }

    @After
    public void tearDown() throws Exception
    {
        clearSharedPreference();
    }

    @Test
    public void testInit() throws Exception
    {
        assertEquals(12, categorySetting.getCategories().size());
    }

    @Test
    public void testEmpty() throws Exception
    {
        categorySetting.setCategories(new Vector<>());
        assertEquals(0, categorySetting.getCategories().size());
    }

    @Test
    public void testSetAndGet() throws Exception
    {
        List<News.Category> list1 = new Vector<>();
        list1.add(News.Category.CAR);
        list1.add(News.Category.DOMESTIC);
        categorySetting.setCategories(list1);
        List<News.Category> list2 = categorySetting.getCategories();
        assertEquals(2, list2.size());
        assertEquals(News.Category.CAR, list2.get(0));
        assertEquals(News.Category.DOMESTIC, list2.get(1));
    }
}