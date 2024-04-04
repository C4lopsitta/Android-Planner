package cc.atomtech.planner.dataEntities

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import cc.atomtech.planner.DB
import cc.atomtech.planner.activities.EditorActivity
import cc.atomtech.planner.R
import cc.atomtech.planner.Values
import cc.atomtech.planner.receivers.AlarmManager
import cc.atomtech.planner.ui.theme.PlannerTheme
import com.google.gson.GsonBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.Instant

@Entity(
   tableName = "reminders",
   foreignKeys = [ForeignKey(
      entity = Project::class,
      parentColumns = arrayOf("rowid"),
      childColumns = arrayOf("projectIdentifier")
   )]
)
data class Reminder (
   @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Long? = null,
   @ColumnInfo()                    var title: String = "",
   @ColumnInfo()                    var isCompleted: Boolean = false,
   @ColumnInfo()                    var creationDate: Long = Time.from(Instant.now()).time,
   @ColumnInfo()                    var completionDate: Long? = null,
   @ColumnInfo()                    var notificationDate: Long? = null,
   @ColumnInfo()                    var notifies: Boolean = false,
   @ColumnInfo()                    var labels: ArrayList<String> = ArrayList(),
   @ColumnInfo(index = true) @Transient var projectIdentifier: Long? = 1
) {
   fun isLate(): Boolean {
      val now: Long = Time.from(Instant.now()).time
      return (notificationDate ?: 0) < now
   }

   @OptIn(DelicateCoroutinesApi::class)
   fun updateCompletionStatus() {
      this.isCompleted = !this.isCompleted
      if(isCompleted)
         this.completionDate = Time.from(Instant.now()).time
      else
         this.completionDate = null
      GlobalScope.launch { DB.getRemindersDAO()!!.update(this@Reminder) }
   }

   fun store(context: Context?) {
      GlobalScope.launch { DB.getRemindersDAO()?.create(this@Reminder) }

      if(this.notifies) {
         AlarmManager.enableReminderNotification(this@Reminder, context)
      }
   }

   fun update() {
      // TODO)) Add notif updater
      GlobalScope.launch { DB.getRemindersDAO()?.update(this@Reminder) }
   }

   fun delete() {
      // TODO)) Add notif updater
      GlobalScope.launch { DB.getRemindersDAO()?.delete(this@Reminder) }
   }

   fun share(context: Context?) {
      val intent = Intent().apply {
         action = Intent.ACTION_SEND
         putExtra(Intent.EXTRA_TITLE, context?.getString(R.string.reminder_share_title) ?: "")
         putExtra(Intent.EXTRA_TEXT, this@Reminder.title)
         type = "text/plain"
      }

      val shareIntent = Intent.createChooser(intent, context?.getString(R.string.reminder_share_title) ?: "")
      context?.startActivity(shareIntent)
   }

   fun shareAsJSON(context: Context?, project: Project?) {
      val gson = GsonBuilder().setPrettyPrinting().create()
      val data = gson.toJson(this)

      val intent = Intent().apply {
         action = Intent.ACTION_SEND
         putExtra(Intent.EXTRA_TITLE, context?.getString(R.string.reminder_share_json_title) ?: "")
         putExtra(Intent.EXTRA_TEXT, data);
         type = "text/json"
      }

      val shareIntent = Intent.createChooser(intent, context?.getString(R.string.reminder_share_json_title) ?: "")
      context?.startActivity(shareIntent)
   }

   fun getBriefTitle(lines: Int = Values.reminderLines, chars: Int = Values.reminderChars): String {
      var body = this.title
      if (body.length > chars) {
         body = this.title.substring(0, chars)
         body += "…"
      }


      // TODO)) Add device targets
      return body
   }

   // TODO: Actually beautify
   fun getBeautifiedCreation(): String {
      return Time.from(Instant.ofEpochMilli(this.creationDate)).toString()
   }

   fun getBeautifiedNotification(): String? {
      if(this.notificationDate == null)
         return null
      return Time.from(Instant.ofEpochMilli(this.notificationDate!!)).toString()
   }

   fun getBeautifiedCompletedOn(): String? {
      if(this.completionDate == null)
         return null
      return Time.from(Instant.ofEpochMilli(this.completionDate!!)).toString()
   }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReminderCard(context: Context?, reminder: Reminder, onLongClick: (Reminder) -> Unit) {
   val haptics = LocalHapticFeedback.current
   OutlinedCard(
      modifier = Modifier
         .fillMaxWidth()
         .combinedClickable(
            onClick = {
               val intent = Intent(context, EditorActivity::class.java)
                  .putExtra("isCreator", false)
                  .putExtra("rowid", reminder.id)
               context?.startActivity(intent)
            },
            onLongClick = {
               haptics.performHapticFeedback(HapticFeedbackType.LongPress)
               onLongClick(reminder)
            },
            onLongClickLabel = context?.getString(R.string.long_press_label) ?: ""
         ),
      shape = CardDefaults.outlinedShape,
      border = BorderStroke(width = 0.dp, color = Color.Transparent)
   ) {
      Row (
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier.defaultMinSize(minHeight = 80.dp)
      ) {
         val isChecked = remember { mutableStateOf(reminder.isCompleted) }
         Checkbox(
            checked = isChecked.value,
            modifier = Modifier.padding(start = 12.dp, end = 10.dp),
            onCheckedChange = {
               reminder.updateCompletionStatus()
               // required for UI live update
               isChecked.value = !isChecked.value
            })
         Column {
            Text(text = reminder.getBriefTitle())
            if(reminder.notificationDate != null)
               Text(
                  text = reminder.getBeautifiedNotification() ?: "",
                  fontSize = TextUnit(12F, TextUnitType.Sp),
                  color = Color.Gray
               )
         }
      }
   }
}

@Preview(showBackground = true)
@Composable
fun ReminderRowPreview() {
   val reminder = Reminder(isCompleted = true, title = "Lorem ipsum", notificationDate = Time.from(Instant.now()).time)
   PlannerTheme {
      ReminderCard(context = null, reminder = reminder) {}
   }
}
