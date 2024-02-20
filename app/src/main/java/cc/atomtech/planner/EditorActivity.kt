package cc.atomtech.planner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.rounded.ArrowBack
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.dataEntities.ColorEntity
import cc.atomtech.planner.dataEntities.Project
import cc.atomtech.planner.dataEntities.Reminder
import cc.atomtech.planner.ui.components.IconText
import cc.atomtech.planner.ui.components.SwitchRow
import cc.atomtech.planner.ui.theme.PlannerTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.Instant
import java.util.Locale

class EditorActivity : ComponentActivity() {
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
                        Log.i("EDITOR", "User triggered store/update, returning")
                        navigateUpTo(Intent(this@EditorActivity, MainActivity::class.java))
                     },
                     icon = { Icon(
                        imageVector = if(isCreator.value) Icons.Rounded.Save else Icons.Rounded.Update,
                        contentDescription = getString(R.string.fab_save_label)
                     ) },
                     text = { Text(text = (if(isCreator.value) getString(R.string.fab_save_label) else getString(R.string.fab_update_label ))) }
                  )
               },
               content = {
                  EditorColumn(this@EditorActivity, it, reminder, projectList, showDatePickerDialog)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorColumn(context: Context?,
                 paddingValues: PaddingValues,
                 reminder: MutableState<Reminder>,
                 projects: MutableList<Project>,
                 showDialog: MutableState<Boolean>) {
   val title = remember { mutableStateOf(reminder.value.title) }
   val notifies = remember { mutableStateOf(false) }
   val isProjectDropdownExpanded = remember { mutableStateOf(false) }
   val chosenProject = remember {
      if(reminder.value.id == null)
         mutableStateOf(projects[0])
      else
         mutableStateOf(projects[((reminder.value.projectIdentifier ?: 1) - 1).toInt()])
   }

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
      )
      {
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
            onClick = { showDialog.value = true },
         ) {
            Icon(
               imageVector = Icons.Rounded.Edit,
               contentDescription = context?.getString(R.string.word_edit)
            )
         }
      }

      SwitchRow(value = notifies, onValueChanged = {notifies.value = it; reminder.value.notifies = it}, label = context?.getString(R.string.lbl_recieve_notification) ?: "Receive a Notification")
      if(notifies.value)
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
         TextField(
            value = chosenProject.value.name,
            onValueChange = {},
            readOnly = true,
            leadingIcon = {
//               Icon(imageVector = Icons.Filled.Circle, contentDescription = null, tint = Color(colors.red, colors.green, colors.blue))
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


// TODO: pullup text labels
@Composable
fun SpanningTextField(value: MutableState<String>,
                      onValueChanged: (String) -> Unit,
                      icon: ImageVector,
                      singleLine: Boolean = false,
                      context: Context?) {
   OutlinedTextField(
      value = value.value,
      onValueChange = onValueChanged,
      placeholder = { Text(text = context?.getString(R.string.txt_reminder_title) ?: "Title") },
      label = { Text(text = context?.getString(R.string.txt_reminder_title) ?: "Title") },
      leadingIcon = { Icon(imageVector = icon, contentDescription = context?.getString(R.string.txt_reminder_title) ?: "Title") },
      modifier = Modifier
         .fillMaxWidth(),
      singleLine = singleLine
   )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(confirmText: String = "Ok", onDismissRequest: (Long?) -> Unit, reminder: Reminder) {
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

@Preview()
@Composable
fun Column() {
   PlannerTheme {
      val reminder = remember { mutableStateOf(Reminder(title = "Lorem ipsum")) }
      val isCreator = remember { mutableStateOf(false) }
      val showDatePickerDialog = remember { mutableStateOf(false) }
      val showTimePickerDialog = remember { mutableStateOf(false) }

      Scaffold(
         modifier = Modifier.fillMaxSize(),

         floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
               if (isCreator.value)
                  reminder.value.store()
               else
                  reminder.value.update()
               Log.i("EDITOR", "User triggered store/update, returning")
            },
               icon = { Icon(
                  imageVector = if(isCreator.value) Icons.Rounded.Save else Icons.Rounded.Update,
                  contentDescription = ""
               ) },
               text = { Text(text = "Save/Update") }
            )
         },
         content = {
            EditorColumn(null, it, reminder, mutableListOf(Project(name = "Lorem ipsum", color = "", isImportant = false)), showDatePickerDialog)
            if(showDatePickerDialog.value) {
               DateDialog (
                  onDismissRequest = {selectedDate ->
                     showDatePickerDialog.value = false
                     reminder.value.notificationDate = selectedDate
                  },
                  reminder = reminder.value,
                  confirmText = "Confirm"
               )
            }
         }
      )
   }
}
