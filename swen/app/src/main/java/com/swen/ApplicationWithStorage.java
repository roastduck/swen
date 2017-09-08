package com.swen;

import android.app.Application;

public class ApplicationWithStorage extends Application
{
    private Storage mStorage;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mStorage = new Storage(this);
    }

    public Storage getStorage() { return mStorage; }
}
