package cc.atomtech.planner

import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cc.atomtech.planner.dataEntities.Reminder
import cc.atomtech.planner.ui.theme.PlannerTheme

class CreateActivity : ComponentActivity() {
   val reminder = Reminder()
   @OptIn(ExperimentalMaterial3Api::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      setContent {
         PlannerTheme {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

            Scaffold(
               modifier = Modifier.fillMaxSize(),
               topBar = {
                  CenterAlignedTopAppBar(
                     title = {
                        Text(
                           text = "${getString(R.string.activity_create)} ${getString(R.string.word_reminder)}"
                        )
                     },
                     scrollBehavior = scrollBehavior,
                     navigationIcon = {
                        IconButton(onClick = {
                           navigateUpTo(Intent(this@CreateActivity, MainActivity::class.java))
                        }) {
                           Icon(
                              imageVector = Icons.Rounded.ArrowBack,
                              contentDescription = getString(R.string.btn_back_desc)
                           )
                        }
                     }
                  )
               },
               floatingActionButton = {
                  ExtendedFloatingActionButton(onClick = {
                        // TODO: make async
                        reminder.store()
                     },
                     icon = { Icon(
                        imageVector = Icons.Rounded.Save,
                        contentDescription = getString(R.string.fab_save_label)
                     ) },
                     text = { Text(text = getString(R.string.fab_save_label)) }
                  )
               },
               content = {
                  CreateColumn(this@CreateActivity, it, reminder)
               }
            )
         }
      }
   }
}

@Composable
fun CreateColumn(context: Context?, paddingValues: PaddingValues, reminder: Reminder) {
   val title = remember { mutableStateOf("") }
   val notifies = remember { mutableStateOf(false) }

   Column (
      modifier = Modifier
         .padding(paddingValues)
         .padding(horizontal = 12.dp)
         .fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
   ) {
      SpanningTextField(value = title, onValueChanged = {title.value = it; reminder.title = it}, icon = Icons.Rounded.TextFields, context = context)
      SwitchRow(value = notifies, onValueChanged = {notifies.value = it; reminder.notifies = it}, label = context?.getString(R.string.lbl_recieve_notification) ?: "Receive a Notification")
   }
}

@Composable
fun SwitchRow(value: MutableState<Boolean>,
              onValueChanged: (Boolean) -> Unit,
              label: String) {
   Row(
      modifier = Modifier
         .fillMaxWidth(0.975f)
         .height(50.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
   ) {
      Switch(checked = value.value, onCheckedChange = onValueChanged)
      Text(text = label, maxLines = 1)
   }
}

// TODO: pullup text labels
@Composable
fun SpanningTextField(value: MutableState<String>,
                      onValueChanged: (String) -> Unit,
                      icon: ImageVector,
                      context: Context?) {
   OutlinedTextField(
      value = value.value,
      onValueChange = onValueChanged,
      placeholder = { Text(text = context?.getString(R.string.txt_reminder_title) ?: "Title") },
      label = { Text(text = context?.getString(R.string.txt_reminder_title) ?: "Title") },
      leadingIcon = { Icon(imageVector = icon, contentDescription = context?.getString(R.string.txt_reminder_title) ?: "Title") },
      modifier = Modifier
         .fillMaxWidth()
   )
}

@Composable
fun DateDialog(onDismissRequest: () -> Unit) {
   Dialog(
      onDismissRequest = onDismissRequest,
      content = {}
      )
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CreatorPreview() {
   PlannerTheme {
      val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

      Scaffold(
         modifier = Modifier.fillMaxSize(),
         topBar = {
            CenterAlignedTopAppBar(
               title = {
                  Text(
                     text = "Create reminder"
                  )
               },
               scrollBehavior = scrollBehavior,
               navigationIcon = {
                  IconButton(onClick = {}) {
                     Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = ""
                     )
                  }
               }
            )
         },
         floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {},
               icon = { Icon(
                  imageVector = Icons.Rounded.Save,
                  contentDescription = "Add") },
               text = { Text(text = "Add") },

            )
         },
         content = {
            CreateColumn(null, it, Reminder())
         }
      )
   }
}

