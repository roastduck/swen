package com.swen;

import android.app.Application;

public class ApplicationWithStorage extends Application
{
    private Storage mStorage;
    private CategorySetting mCategorySetting;
    private Behavior mBehavior;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mStorage = new Storage(this);
        mCategorySetting = new CategorySetting(this);
        mBehavior = new Behavior(this);
    }

    public Storage getStorage() { return mStorage; }

    public CategorySetting getCategorySetting() { return mCategorySetting; }

    public Behavior getBehavior() { return mBehavior; }
}
