package cc.atomtech.planner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.ui.theme.PlannerTheme

class SettingsActivity : ComponentActivity() {
   @OptIn(ExperimentalMaterial3Api::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      setContent {
         PlannerTheme {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
                           Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = getString(R.string.btn_back_desc))
                        }
                     }
                  )
               },
               content = {
                  Column (
                     modifier = Modifier.padding(it)
                  ) {
                     VersionInfo(this@SettingsActivity)
                  }
               }
            )
         }
      }
   }
}

@Composable
fun VersionInfo(context: Context) {
   val version = context.packageManager.getPackageInfo(context.packageName, 0).versionName
   Text(
      text = "Version " + version,
      textAlign = TextAlign.Center,
      fontSize = TextUnit(2.75f, TextUnitType.Em),
      modifier = Modifier
         .fillMaxWidth()
   )
}

