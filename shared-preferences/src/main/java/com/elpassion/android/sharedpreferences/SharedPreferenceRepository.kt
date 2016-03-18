package com.elpassion.android.sharedpreferences

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface SharedPreferenceRepository<T> {

    fun write(key: String, value: T?)

    fun read(key: String): T?
}

inline fun <reified T> createSharedPrefs(noinline sharedPreferencesProvider: () -> SharedPreferences): SharedPreferenceRepository<T> {
    return createSharedPrefs(sharedPreferencesProvider, { Gson() })
}

inline fun <reified T> createSharedPrefs(noinline sharedPreferencesProvider: () -> SharedPreferences, noinline gsonProvider: () -> Gson): SharedPreferenceRepository<T> {
    return object : SharedPreferenceRepository<T> {

        private val sharedPreferences by lazy(sharedPreferencesProvider)
        private val gson by lazy(gsonProvider)
        private val type = object : TypeToken<T>() {}.type

        override fun write(key: String, value: T?) {
            sharedPreferences.edit()
                    .putString(key, gson.toJson(value, type))
                    .apply()
        }

        override fun read(key: String): T? {
            val value = sharedPreferences.getString(key, null)
            if (value == null) {
                return null
            } else {
                return gson.fromJson<T>(value, type)
            }
        }
    }
}
