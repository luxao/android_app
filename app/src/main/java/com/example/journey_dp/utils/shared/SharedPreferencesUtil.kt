package com.example.journey_dp.utils.shared



import android.content.Context
import android.content.SharedPreferences


class SharedPreferencesUtil private constructor() {
    private fun getSharedPreferences(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(
            shpKey, Context.MODE_PRIVATE
        )
    }

    fun clearData(context: Context?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun putUserItem(context: Context?, userUid: String) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.putString(userUid, userUid)
        editor.apply()
    }


    fun getUserUid(context: Context?, uid: String): String? {
        val sharedPref = getSharedPreferences(context) ?: return null
        return sharedPref.getString(uid, null) ?: ""
    }

    companion object {
        @Volatile
        private var INSTANCE: SharedPreferencesUtil? = null

        fun getInstance(): SharedPreferencesUtil =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: SharedPreferencesUtil().also { INSTANCE = it }
            }

        private const val shpKey = "all_users_uid"

    }
}