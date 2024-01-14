package cc.atomtech.planner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.dataEntities.Reminder
import cc.atomtech.planner.ui.components.SwitchRow
import cc.atomtech.planner.ui.theme.PlannerTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.Instant

class EditorActivity : ComponentActivity() {
   lateinit var isCreator: MutableState<Boolean>
   private lateinit var reminder: MutableState<Reminder>
   @OptIn(ExperimentalMaterial3Api::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      val superIntent = intent
      var localReminder: Reminder = Reminder()

      //if we are not creating a reminder, we're editing one, so let's read it from the DB
      if(!superIntent.getBooleanExtra("isCreator", true))
         GlobalScope.launch {
            localReminder = DB.getRemindersDAO()
               ?.read(superIntent.getLongExtra("rowid", 0)) ?: Reminder()
         }

      super.onCreate(savedInstanceState)

      setContent {
         PlannerTheme {
            reminder = remember { mutableStateOf(localReminder) }
            isCreator = remember { mutableStateOf(superIntent.getBooleanExtra("isCreator", true)) }
            val showDatePickerDialog = remember { mutableStateOf(false) }
            val showTimePickerDialog = remember { mutableStateOf(false) }
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

            Scaffold(
               modifier = Modifier.fillMaxSize(),
               topBar = {
                  CenterAlignedTopAppBar(
                     title = {
                        Text(
                           text = "${
                              if(isCreator.value) getString(R.string.activity_create)
                              else getString(R.string.activity_editor)
                           } ${getString(R.string.word_reminder)}"
                        )
                     },
                     scrollBehavior = scrollBehavior,
                     navigationIcon = {
                        IconButton(onClick = {
                           navigateUpTo(Intent(this@EditorActivity, MainActivity::class.java))
                        }) {
                           Icon(
                              imageVector = Icons.Rounded.ArrowBack,
                              contentDescription = getString(R.string.btn_back_desc)
                           )
                        }
                     },
                     actions = {
                        if(!isCreator.value) {
                           IconButton(onClick = {
                              reminder.value.delete()
                              navigateUpTo(Intent(this@EditorActivity, MainActivity::class.java))
                           }) {
                              Icon(
                                 imageVector = Icons.Rounded.Delete,
                                 contentDescription = ""
                              )
                           }
                        }
                     }
                  )
               },
               floatingActionButton = {
                  ExtendedFloatingActionButton(onClick = {
                        if (isCreator.value)
                           reminder.value.store()
                        else
                           reminder.value.update()
                     },
                     icon = { Icon(
                        imageVector = if(isCreator.value) Icons.Rounded.Save else Icons.Rounded.Update,
                        contentDescription = getString(R.string.fab_save_label)
                     ) },
                     text = { Text(text = (if(isCreator.value) getString(R.string.fab_save_label) else getString(R.string.fab_update_label ))) }
                  )
               },
               content = {
                  EditorColumn(this@EditorActivity, it, reminder, showDatePickerDialog)
                  if(showDatePickerDialog.value) {
                     DateDialog (
                        onDismissRequest = {selectedDate ->
                           showDatePickerDialog.value = false
                           reminder.value.notificationDate = selectedDate
                        },
                        reminder = reminder.value,
                        confirmText = getString(R.string.word_confirm)
                     )
                  }
               }
            )
         }
      }
   }
}

@Composable
fun EditorColumn(context: Context?,
                 paddingValues: PaddingValues,
                 reminder: MutableState<Reminder>,
                 showDialog: MutableState<Boolean>) {
   val title = remember { mutableStateOf(reminder.value.title) }
   val notifies = remember { mutableStateOf(false) }

   Column (
      modifier = Modifier
         .padding(paddingValues)
         .padding(horizontal = 12.dp)
         .fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
   ) {
      SpanningTextField(value = title, onValueChanged = {title.value = it; reminder.value.title = it}, icon = Icons.Rounded.TextFields, context = context)
      Row(
         modifier = Modifier
            .fillMaxWidth(),
         verticalAlignment = Alignment.CenterVertically,
         horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
         Icon(
            imageVector = Icons.Rounded.Notifications,
            contentDescription = context?.getString(R.string.btn_editor_notification_date),
            modifier = Modifier.width(32.dp)
         )
         Column {
            Text(text = context?.getString(R.string.btn_editor_notification_date) ?: "Notification date")
            Text(text = reminder.value.getBeautifiedNotification() ?: "")
         }
         IconButton(
            modifier = Modifier.width(32.dp),
            onClick = { showDialog.value = true },
         ) {
            Icon(
               imageVector = Icons.Rounded.Edit,
               contentDescription = context?.getString(R.string.word_edit)
            )
         }
      }

      SwitchRow(value = notifies, onValueChanged = {notifies.value = it; reminder.value.notifies = it}, label = context?.getString(R.string.lbl_recieve_notification) ?: "Receive a Notification")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(confirmText: String = "Ok", onDismissRequest: (Long?) -> Unit, reminder: Reminder) {
   val now = if(reminder.notificationDate == null) Time.from(Instant.now()).time else reminder.notificationDate

   //TODO: Remove hardcoded range
   val datePickerState = DatePickerState(now, now, IntRange(2024, 2025), DisplayMode.Picker)

   DatePickerDialog(
      onDismissRequest = { onDismissRequest(datePickerState.selectedDateMillis) },
      content = { DatePicker(state = datePickerState) },
      confirmButton = {
         Button(
            onClick = { onDismissRequest(datePickerState.selectedDateMillis) },
            content = { Text(text = confirmText) }
         )
      }
   )
}

/*@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
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
            EditorColumn(
               null,
               it,
               Reminder(),
               remember { mutableStateOf(false) },
               remember { mutableStateOf(false) }
            )
         }
      )
   }
}*/

