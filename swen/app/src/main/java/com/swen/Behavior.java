package com.swen;

import android.content.Context;
import android.content.Intent;
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

    public Behavior(Context context) {
        this.context = context;
        readLocalPreference();
    }

    private void readLocalPreference() {
        FileInputStream fis;
        try {
            fis = context.openFileInput("preference.dat");
        } catch (FileNotFoundException e) {
            try {
                FileOutputStream fos = context.openFileOutput("preference.dat", Context.MODE_APPEND);
                BufferedWriter bufferedWriter =
                    new BufferedWriter(new OutputStreamWriter(fos));
                for(int i = 1; i <= 12; i++) {
                    bufferedWriter.write(0 + " ");
                }
                bufferedWriter.newLine();
                bufferedWriter.flush();
                bufferedWriter.close();
                for(int i = 1; i < 13; i++) {
                    categoryPreference.put(i, 0);
                }
                return;
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }
        }
        BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(fis));
        try {
            String line = bufferedReader.readLine();
            String[] split = line.split(" ");
            for(int i = 1; i < 13; i++) {
                int preference = Integer.valueOf(split[i - 1]);
                categoryPreference.put(i, preference);
            }
            while((line = bufferedReader.readLine()) != null) {
                split = line.split(" ");
                keywordPreference.put(split[0],
                    Integer.valueOf(split[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void markHaveRead(News news) {}

    public double getPreference(News news) {}
}
