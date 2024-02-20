package cc.atomtech.planner

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

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