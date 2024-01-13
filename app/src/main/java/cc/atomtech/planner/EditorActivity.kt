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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.Instant

class EditorActivity : ComponentActivity() {
   lateinit var isCreator: MutableState<Boolean>
   private lateinit var reminder: Reminder
   @OptIn(ExperimentalMaterial3Api::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      val superIntent = intent

      //if we are not creating a reminder, we're editing one, so let's read it from the DB
      if(!superIntent.getBooleanExtra("isCreator", true))
         GlobalScope.launch {
            reminder = DB.getRemindersDAO()
               ?.read(superIntent.getLongExtra("rowid", 0)) ?: Reminder()
         }
      else
         reminder = Reminder()

      super.onCreate(savedInstanceState)

      setContent {
         PlannerTheme {
            isCreator = remember { mutableStateOf(superIntent.getBooleanExtra("isCreator", true)) }
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
                              reminder.delete()
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
                           reminder.store()
                        else
                           reminder.update()
                     },
                     icon = { Icon(
                        imageVector = if(isCreator.value) Icons.Rounded.Save else Icons.Rounded.Update,
                        contentDescription = getString(R.string.fab_save_label)
                     ) },
                     text = { Text(text = (if(isCreator.value) getString(R.string.fab_save_label) else getString(R.string.fab_update_label ))) }
                  )
               },
               content = {
                  EditorColumn(this@EditorActivity, it, reminder, isCreator)
               }
            )
         }
      }
   }
}

@Composable
fun EditorColumn(context: Context?, paddingValues: PaddingValues, reminder: Reminder, isCreator: MutableState<Boolean>) {
   val title = remember { mutableStateOf(reminder.title) }
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
         Column (
            //modifier = Modifier.fillMaxWidth()
         ) {
            Text(text = context?.getString(R.string.btn_editor_notification_date) ?: "Notification date")
            Text(text = reminder.getBeautifiedNotification())
         }
         IconButton(
            modifier = Modifier.width(32.dp),
            onClick = {

            },

         ) {
            Icon(
               imageVector = Icons.Rounded.Edit,
               contentDescription = context?.getString(R.string.word_edit)
            )
         }
      }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(title: String = "DatePicker" , onDismissRequest: () -> Unit) {
   Dialog(
      onDismissRequest = onDismissRequest,
      content = {
         Card(
            modifier = Modifier
               .fillMaxWidth()
               .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
         ){
            Column {
               val time = Time.from(Instant.now())

               Text(text = title)
               DatePicker(state = DatePickerState(
                  initialSelectedDateMillis = time.time,
                  initialDisplayedMonthMillis = null,
                  yearRange = IntRange(2024, 2025),
                  initialDisplayMode = DisplayMode.Picker
               ))
            }
         }
      }
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
            EditorColumn(null, it, Reminder(), remember { mutableStateOf(false) })
         }
      )
   }
}

