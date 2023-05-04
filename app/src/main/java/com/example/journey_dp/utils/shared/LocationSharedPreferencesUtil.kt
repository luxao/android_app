package com.example.journey_dp.utils.shared

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.maps.model.LatLng

class LocationSharedPreferencesUtil private constructor() {
    private fun getSharedPreferences(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(
            locKey, Context.MODE_PRIVATE
        )
    }

    fun clearData(context: Context?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun putUserLoc(context: Context?, cityName: String, userLoc: LatLng, uid: String) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        val loc = cityName.plus("=").plus(userLoc.latitude.toString()).plus(",").plus(userLoc.longitude.toString())
        Log.i("MYTEST", "SHARED LOCATION : $loc")
        editor.putString(uid, loc)
        editor.apply()
    }


    fun getUserUidLoc(context: Context?, uid: String): String? {
        val sharedPref = getSharedPreferences(context) ?: return null
        return sharedPref.getString(uid, null) ?: ""
    }

    companion object {
        @Volatile
        private var INSTANCE: LocationSharedPreferencesUtil? = null

        fun getInstance(): LocationSharedPreferencesUtil =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: LocationSharedPreferencesUtil().also { INSTANCE = it }
            }

        private const val locKey = "user_location"

    }
}