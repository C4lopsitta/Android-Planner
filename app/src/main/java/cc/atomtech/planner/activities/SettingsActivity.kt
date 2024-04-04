package cc.atomtech.planner.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.AppPreferences
import cc.atomtech.planner.MainActivity
import cc.atomtech.planner.R
import cc.atomtech.planner.ui.components.SwitchRow
import cc.atomtech.planner.ui.theme.PlannerTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SettingsActivity : ComponentActivity() {
   @OptIn(ExperimentalMaterial3Api::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      setContent {
         PlannerTheme {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

            val usesHomeSearch = remember { mutableStateOf(false) }
            val showsQuickChips = remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
               usesHomeSearch.value =
                  AppPreferences.readBoolean(this@SettingsActivity, "useSearchTopBar")
               showsQuickChips.value =
                  AppPreferences.readBoolean(this@SettingsActivity, getString(R.string.datastore_show_dash_chips))
            }

            Scaffold (
               modifier = Modifier.fillMaxSize(),
               topBar = {
                  CenterAlignedTopAppBar(
                     title = { Text(getString(R.string.activity_settings)) },
                     scrollBehavior = scrollBehavior,
                     navigationIcon = {
                        IconButton(onClick = {
                           navigateUpTo(Intent(this@SettingsActivity, MainActivity::class.java))
                        }) {
                           Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = getString(
                              R.string.btn_back_desc
                           ))
                        }
                     }
                  )
               },
               content = {
                  Log.i("SETTINGS_ACTIVITY", "read value ${usesHomeSearch.value}")
                  Column (
                     modifier = Modifier
                        .padding(it)
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .fillMaxSize()
                  ) {
                     //TODO: Fix alignment
                     SwitchRow(
                        value = usesHomeSearch,
                        label = getString(R.string.lbl_settings_useSearch),
                        onValueChanged = {
                           usesHomeSearch.value = !usesHomeSearch.value
                           GlobalScope.launch {
                              AppPreferences.writeBoolean(
                                 this@SettingsActivity,
                                 "useSearchTopBar",
                                 usesHomeSearch.value
                              )
                           }
                        }
                     )
                     SwitchRow(
                        value = showsQuickChips,
                        label = getString(R.string.lbl_settings_showChips),
                        onValueChanged = {
                           showsQuickChips.value = !showsQuickChips.value
                           GlobalScope.launch {
                              AppPreferences.writeBoolean(
                                 this@SettingsActivity,
                                 getString(R.string.datastore_show_dash_chips),
                                 showsQuickChips.value
                              )
                           }
                        }
                     )

                     BottomInfo(this@SettingsActivity)
                  }
               }
            )
         }
      }
   }
}

@Composable
fun BottomInfo(context: Context) {
   val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
   val versionCode = context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
   Box(
      modifier = Modifier.height(128.dp),
      contentAlignment = Alignment.BottomCenter
   ) {
      Text(
         text =
         """
            ${context.getString(R.string.copyright_notice)}
            ${context.getString(R.string.license_notice)}
            ${context.getString(R.string.word_version)} $versionName ($versionCode)
         """.trimIndent(),
         textAlign = TextAlign.Center,
         fontSize = TextUnit(2.75f, TextUnitType.Em),
         lineHeight = TextUnit(1.25f, TextUnitType.Em),
         modifier = Modifier.fillMaxWidth()
      )
   }
}