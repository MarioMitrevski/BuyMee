package com.buymee.data

import android.content.Context
import com.buymee.R


class UserPrefs private constructor(val context: Context){

    fun storeToken(token: String){
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.user_preference),
            Context.MODE_PRIVATE
        )
        with(sharedPref.edit()) {
            putString("token_key", token)
            apply()
        }
    }

    fun getToken(): String?{
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.user_preference),
            Context.MODE_PRIVATE
        )
        return sharedPref.getString("token_key", "")
    }

    fun logout(){
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.user_preference),
            Context.MODE_PRIVATE
        )
        with(sharedPref.edit()) {
            putString("token_key", "")
            apply()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPrefs? = null
        fun getInstance(context: Context): UserPrefs =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPrefs(context).also { INSTANCE = it }
            }
    }
}
