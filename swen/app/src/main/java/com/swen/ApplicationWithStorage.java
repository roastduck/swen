package com.swen;

import android.app.Application;

public class ApplicationWithStorage extends Application
{
    private Storage mStorage;
    private CategorySetting mCategorySetting;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mStorage = new Storage(this);
        mCategorySetting = new CategorySetting(this);
    }

    public Storage getStorage() { return mStorage; }

    public CategorySetting getCategorySetting() { return mCategorySetting; }
}
