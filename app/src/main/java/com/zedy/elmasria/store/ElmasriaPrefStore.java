package com.zedy.elmasria.store;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mostafa_anter on 9/26/16.
 */

public class ElmasriaPrefStore {
    private static final String PREFKEY = "ElmasriaPreferencesStore";
    private SharedPreferences elmasriaPreferences;

    public ElmasriaPrefStore(Context context){
        elmasriaPreferences = context.getSharedPreferences(PREFKEY, Context.MODE_PRIVATE);
    }

    public void clearPreference(){
        SharedPreferences.Editor editor = elmasriaPreferences.edit();
        editor.clear().apply();
    }

    public void addPreference(String key, String value){
        SharedPreferences.Editor editor = elmasriaPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void addPreference(String key, int value){
        SharedPreferences.Editor editor = elmasriaPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void removePreference(String key){
        SharedPreferences.Editor editor = elmasriaPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public String getPreferenceValue(String key){
        return elmasriaPreferences.getString(key, "");
    }

    public int getIntPreferenceValue(String key){
        return elmasriaPreferences.getInt(key, -1);
    }
}
