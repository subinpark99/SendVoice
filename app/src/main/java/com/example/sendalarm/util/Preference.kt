package com.example.sendalarm.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class Preference(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user", Context.MODE_PRIVATE)


//    fun getUser(): User? {  // user 정보 조회
//        val userInfo = prefs.getString("userInfo", "").toString()
//        return Gson().fromJson(userInfo, User::class.java)
//    }
//
//    fun setUser(user: User) {  // user 정보 저장
//        val userInfo = Gson().toJson(user)
//        prefs.edit().putString("userInfo", userInfo).apply()
//    }
//
//
//    fun setAutoLogin(autologin: Boolean) {
//        prefs.edit().putBoolean("autologin", autologin).apply()
//    }
//
//    fun getAutoLogin(): Boolean {
//        return prefs.getBoolean("autologin", false)
//    }


}