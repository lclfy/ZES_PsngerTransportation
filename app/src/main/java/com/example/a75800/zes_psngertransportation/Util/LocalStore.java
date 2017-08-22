package com.example.a75800.zes_psngertransportation.Util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.Arrays;

/**
 * Created by 75800 on 2017/8/22.
 */

public class LocalStore {

    public static void saveApkEnalbleArray(Context context, boolean[] booleanArray) {
        SharedPreferences prefs = context.getSharedPreferences("Stations", Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        for (boolean b : booleanArray) {
            jsonArray.put(b);
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Stations",jsonArray.toString());
        editor.commit();
    }

    public static boolean[] getApkEnableArray(Context context,int arrayLength)
    {
        SharedPreferences prefs = context.getSharedPreferences("Stations", Context.MODE_PRIVATE);
        boolean[] resArray=new boolean[arrayLength];
        Arrays.fill(resArray, false);
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString("Stations", "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                resArray[i] = jsonArray.getBoolean(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resArray;
    }
}
