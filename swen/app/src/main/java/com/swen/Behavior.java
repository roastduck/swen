package com.swen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Behavior
{
    private HashMap<String, Integer> keywordPreference = new HashMap<>();
    private HashMap<Integer, Integer> categoryPreference = new HashMap<>();
    private Context context;
    private SharedPreferences sharedPreferences;

    public Behavior(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("readingPreferences", Context.MODE_PRIVATE);
        readLocalPreference();
    }

    public void markHaveRead(News news) {}

    public double getPreference(News news) {}
}
