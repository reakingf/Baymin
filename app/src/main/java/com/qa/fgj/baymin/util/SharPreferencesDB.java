package com.qa.fgj.baymin.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * Created by FangGengjia on 2017/2/19.
 */

public class SharPreferencesDB {

    Context mContext;
    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    public SharPreferencesDB(Context context) {
        this.mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public SharPreferencesDB(Context context, String preferencesName) {
        this.mContext = context;
        setPreferences(preferencesName);
    }

    public void setPreferences(String name){
        if (Build.VERSION.SDK_INT > 11){
            mPreferences = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        } else {
            mPreferences = mContext.getSharedPreferences(name, Context.MODE_MULTI_PROCESS);
        }
    }

    public int getInt(String key){
        return mPreferences.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue){
        return mPreferences.getInt(key, defaultValue);
    }

    public long getLong(String key){
        return mPreferences.getLong(key, 0);
    }

    public long getLong(String key, long defaultValue){
        return mPreferences.getLong(key, defaultValue);
    }

    public String getString(String key){
        return mPreferences.getString(key, "");
    }

    public String getString(String key, String defaultValue){
        return mPreferences.getString(key, defaultValue);
    }

    public Boolean getBoolean(String key){
        return mPreferences.getBoolean(key, false);
    }

    public Boolean getString(String key, Boolean defaultValue){
        return mPreferences.getBoolean(key, defaultValue);
    }


}
