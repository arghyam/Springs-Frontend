package com.arghyam.commons.utils

import android.content.Context
import android.content.SharedPreferences



class SharedPreferenceFactory(var context: Context) {

    var pref: SharedPreferences = context.getSharedPreferences(Constants.APPLICATION_PREFERENCE, 0) // 0 - for private mode
    var editor = pref.edit()

    fun setString(key:String,value: String)= run {
        editor.putString(key,value)
        editor.commit()
    }

    fun getString(key:String): String? {
        return pref.getString(key,"")
    }

    fun setInt(key: Int, value: Int)= run {
        editor.putInt(key.toString(),value)
        editor.commit()
    }

    fun getInt(key: Int): Int? {
        return pref.getInt(key.toString(),0)
    }

    fun setBoolean(key:String,value: Boolean)= run {
        editor.putBoolean(key,value)
        editor.commit()
    }

    fun getBoolean(key:String): Boolean? {
        return pref.getBoolean(key,false)
    }

    fun clearAll() {
        
    }
    
}