package cc.atomtech.planner

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object AppPreferences {
   suspend fun readBoolean(context: Context, key: String): Boolean {
      val KEY = booleanPreferencesKey(key)
      val flow: Flow<Boolean> = context.dataStore.data.map {
         it[KEY] ?: false
      }
      return flow.first()
   }

   suspend fun writeBoolean(context: Context, key: String, boolean: Boolean) {
      val KEY = booleanPreferencesKey(key)
      context.dataStore.edit { it[KEY] = boolean }
   }

   suspend fun readString(context: Context, key: String): String {
      val KEY = stringPreferencesKey(key)
      val flow: Flow<String> = context.dataStore.data.map {
         it[KEY] ?: ""
      }
      return flow.first()
   }

   suspend fun writeString(context: Context, key: String, string: String) {
      val KEY = stringPreferencesKey(key)
      context.dataStore.edit { it[KEY] = string }
   }
}