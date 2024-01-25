package cc.atomtech.planner

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class AppPreferences {
   companion object {
      suspend fun readBoolean(context: Context, key: String): Boolean {
         val KEY = booleanPreferencesKey(key)
         val flow: Flow<Boolean> = context.dataStore.data.map {
            it[KEY] ?: false
         }
         return flow.first()
      }

      //TODO: Switch to job
      suspend fun writeBoolean(context: Context, key: String, boolean: Boolean) {
         val KEY = booleanPreferencesKey(key)
         context.dataStore.edit { it[KEY] = boolean }
      }
   }
}