package cc.atomtech.planner.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.DB
import cc.atomtech.planner.MainActivity
import cc.atomtech.planner.R
import cc.atomtech.planner.dataEntities.ColorEntity
import cc.atomtech.planner.dataEntities.Project
import cc.atomtech.planner.dataEntities.Reminder
import cc.atomtech.planner.receivers.AlarmManager
import cc.atomtech.planner.receivers.AlarmReceiver
import cc.atomtech.planner.ui.components.IconText
import cc.atomtech.planner.ui.components.SpanningTextField
import cc.atomtech.planner.ui.components.SwitchRow
import cc.atomtech.planner.ui.theme.PlannerTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.Instant
import java.util.Locale
import java.util.Objects

// TODO)) Componentify column

class EditorActivity : ComponentActivity() {
   companion object {
      val DIALOG_CLOSED = 0
      val DIALOG_CALENDAR = 1
      val DIALOG_TIME = 2
   }

   lateinit var isCreator: MutableState<Boolean>
   private lateinit var reminder: MutableState<Reminder>
   @OptIn(ExperimentalMaterial3Api::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      val superIntent = intent
      var localReminder: Reminder = Reminder()

      var projectList: MutableList<Project> = mutableListOf()

      //if we are not creating a reminder, we're editing one, so let's read it from the DB
      if(!superIntent.getBooleanExtra("isCreator", true))
         GlobalScope.launch {
            localReminder = DB.getRemindersDAO()
               ?.read(superIntent.getLongExtra("rowid", 0)) ?: Reminder()
         }

      GlobalScope.launch {
         projectList = (DB.getProjectsDAO()?.readAll())?.toMutableList()!!
      }

      super.onCreate(savedInstanceState)

      setContent {
         PlannerTheme {
            reminder = remember { mutableStateOf(localReminder) }
            isCreator = remember { mutableStateOf(superIntent.getBooleanExtra("isCreator", true)) }

            val showDialog = remember { mutableStateOf(EditorActivity.DIALOG_CLOSED) }
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
                           reminder.value.store(context = this@EditorActivity)
                        else
                           reminder.value.update()
                        Log.i("EDITOR", "User triggered store/update, returning")
                        navigateUpTo(Intent(this@EditorActivity, MainActivity::class.java))
                     },
                     icon = { Icon(
                        imageVector = if(isCreator.value) Icons.Rounded.Save else Icons.Rounded.Update,
                        contentDescription = getString(R.string.fab_save_label)
                     ) },
                     text = { Text(text = (if(isCreator.value) getString(R.string.fab_save_label) else getString(
                        R.string.fab_update_label
                     ))) }
                  )
               },
               content = {
                  EditorColumn(this@EditorActivity, it, reminder, projectList, showDialog = showDialog)
                  DialogHandler(dialog = showDialog)
                  //TODO)) Move to handler
                  if(showDialog.value == EditorActivity.DIALOG_CALENDAR) {
                     DateDialog (
                        onDismissRequest = {selectedDate ->
                           showDialog.value = EditorActivity.DIALOG_CLOSED
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EditorColumn(context: Context?,
                 paddingValues: PaddingValues,
                 reminder: MutableState<Reminder>,
                 projects: MutableList<Project>,
                 showDialog: MutableState<Int>) {
   val title = remember { mutableStateOf(reminder.value.title) }
   val notifies = remember { mutableStateOf(false) }
   val isProjectDropdownExpanded = remember { mutableStateOf(false) }


   //TODO)) Add already selected time
   val calendar = remember { mutableStateOf(object {
      var time: Long = 0
      var date: Long = 0
   }) }

   //TODO)) Fix preselected project
   val chosenProject = remember { mutableStateOf(projects[0]) }

   Column (
      modifier = Modifier
         .padding(paddingValues)
         .padding(horizontal = 12.dp)
         .fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
   ) {
      SpanningTextField(
         value = title,
         onValueChanged = {title.value = it; reminder.value.title = it},
         placeholder = context?.getString(R.string.txt_reminder_title) ?: "",
         label = context?.getString(R.string.txt_reminder_title) ?: "",
         leadingIcon = { Icon(imageVector = Icons.Rounded.TextFields, contentDescription = null) }
      )


      // Old notification row
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
            if(reminder.value.notificationDate != null)
               Text(text = reminder.value.getBeautifiedNotification() ?: "")
         }
         IconButton(
            modifier = Modifier.width(32.dp),
            onClick = { showDialog.value = EditorActivity.DIALOG_CALENDAR },
         ) {
            Icon(
               imageVector = Icons.Rounded.Edit,
               contentDescription = context?.getString(R.string.word_edit)
            )
         }
      }

      // New notification row
      Row(
         modifier = Modifier.fillMaxWidth(),
         verticalAlignment = Alignment.CenterVertically,
         horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
         OutlinedTextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            label = { Text(context?.getString(R.string.word_date) ?: "Date") },
            leadingIcon = { Icon(imageVector = Icons.Rounded.CalendarMonth, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.5F),
            singleLine = true,
            trailingIcon = { IconButton(onClick = { showDialog.value = EditorActivity.DIALOG_CALENDAR }) {
               Icon(imageVector = Icons.Rounded.Edit, contentDescription = null)
            }}
         )
         OutlinedTextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(context?.getString(R.string.word_time) ?: "Time") },
            leadingIcon = { Icon(imageVector = Icons.Rounded.AccessTime, contentDescription = null) },
            trailingIcon = { IconButton(onClick = { showDialog.value = EditorActivity.DIALOG_TIME }) {
               Icon(imageVector = Icons.Rounded.Edit, contentDescription = null)
            }}
         )
      }

      SwitchRow(value = notifies, onValueChanged = {notifies.value = it; reminder.value.notifies = it}, label = context?.getString(
         R.string.lbl_recieve_notification
      ) ?: "Receive a Notification")
      if(notifies.value && AlarmManager.canSchedule())
         IconText(
            imageVector = Icons.Rounded.Info,
            text = context?.getString(R.string.lbl_warn_no_notif) ?: "",
            modifier = Modifier
               .fillMaxWidth(),
            isError = true
         )

      ExposedDropdownMenuBox(
         expanded = isProjectDropdownExpanded.value,
         onExpandedChange = {isProjectDropdownExpanded.value = !isProjectDropdownExpanded.value},
         modifier = Modifier.fillMaxWidth()
      ) {
         OutlinedTextField(
            value = chosenProject.value.name,
            onValueChange = {},
            readOnly = true,
            leadingIcon = {
               val colors = ColorEntity()
               colors.buildByHex(chosenProject.value.color)
               Icon(
                  imageVector = Icons.Filled.Circle,
                  contentDescription = null,
                  tint = Color(colors.red, colors.green, colors.blue)
               )
            },

            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isProjectDropdownExpanded.value) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
               .menuAnchor()
               .fillMaxWidth()
         )

         ExposedDropdownMenu(
            expanded = isProjectDropdownExpanded.value,
            onDismissRequest = { isProjectDropdownExpanded.value = false },
            modifier = Modifier.fillMaxWidth()
         ) {
            projects.forEach { project ->
               val colors = ColorEntity()
               colors.buildByHex(project.color)

               DropdownMenuItem(
                  text = {
                     Text(text = project.name)
                  },
                  modifier = Modifier.fillMaxWidth(),
                  onClick = {
                     chosenProject.value = project
                     reminder.value.projectIdentifier = project.id
                     isProjectDropdownExpanded.value = false
                  },
                  leadingIcon = {
                     Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = null,
                        tint = Color(colors.red, colors.green, colors.blue)
                     )
                  }
               )
            }
         }
      }


   }
}

@Composable
fun DialogHandler(dialog: MutableState<Int>) {
   if(dialog.value == EditorActivity.DIALOG_CALENDAR)
      null

   if(dialog.value == EditorActivity.DIALOG_TIME)
      null
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(confirmText: String = "Ok",
               reminder: Reminder,
               onDismissRequest: (Long?) -> Unit) {

   val now = if(reminder.notificationDate == null) Time.from(Instant.now()).time else reminder.notificationDate

   //TODO: Remove hardcoded range
   val datePickerState = DatePickerState(Locale.getDefault(), now)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog() {

}
