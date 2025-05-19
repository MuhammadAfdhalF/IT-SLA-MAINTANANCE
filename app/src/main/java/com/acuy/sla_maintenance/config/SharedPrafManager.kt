package com.acuy.sla_maintenance.config

import android.content.Context
import android.content.SharedPreferences

class SharedPrafManager(context: Context) {
    private val PREFS_NAME = "sharedpref12345"
    private val sharedPref: SharedPreferences
    val editor: SharedPreferences.Editor

    init {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }

    fun put(key: String, value: Any?) {
        when (value) {
            is String? -> editor.putString(key, value)
            is Boolean? -> editor.putBoolean(key, value ?: false)
            is Int? -> editor.putInt(key, value ?: 0)
        }
        editor.apply()
    }



    fun getBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun getString(key: String): String? {
        return sharedPref.getString(key, null)
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPref.getInt(key, defaultValue)
    }

    fun clear() {
        editor.clear().apply()
    }

    fun putInt(key: Int, value: Int) {
        editor.putInt(key.toString(), value)
        editor.apply()
    }



    fun putString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }



}



//package com.acuy.sla_maintenance.config
//
//import android.content.Context
//import android.content.SharedPreferences
//
////menyimpan dan mengambil data dari sharedpreference
//class SharedPrafManager(context: Context) {
//    private val PREFS_NAME ="sharedpref12345" //nama sharedpreference
//    private val sharedPref: SharedPreferences //objek
//    val editor: SharedPreferences.Editor //editor
//
//    init {
//        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        editor = sharedPref.edit()
//    }
////menyimpan data ke sharedpreference
//    fun put (key: String, value:Any){
//        when(value){
//            is String -> editor.putString(key,value)
//            is Boolean -> editor.putBoolean(key, value)
//            is Int -> editor.putInt(key,value)
//
//        }
//        editor.apply()
//    }
//
////    mendapatkan nilai boolean dari key
//    fun getBoolean(key: String):Boolean{
//        return sharedPref.getBoolean(key, false)
//
//    }
////    mendapatkan nilai string
//    fun getString(key: String): String? {
//        return sharedPref.getString(key, null)
//    }
//
////    menghapus data
//    fun clear(){
//        editor.clear()
//            .apply()
//    }
//}