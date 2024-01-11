package cc.atomtech.planner.dataEntities

import android.content.Context
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import cc.atomtech.planner.DB
import cc.atomtech.planner.ui.theme.PlannerTheme
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
   @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Long = -1,
   @ColumnInfo()                    var title: String = "What a reminder",
   @ColumnInfo()                    var isCompleted: Boolean = false,
   @ColumnInfo()                    var creationDate: Long = Time.from(Instant.now()).time,
   @ColumnInfo()                    var completionDate: Long? = null,
   @ColumnInfo()                    var notificationDate: Long? = null,
   @ColumnInfo()                    var notifies: Boolean = false,
   @ColumnInfo()                    var labels: ArrayList<String> = ArrayList(),
   @ColumnInfo()                    var appertainsTo: String? = null,
   @ColumnInfo()                    var projectIdentifier: Long? = null
) {
   fun isLate(): Boolean {
      val now: Long = Time.from(Instant.now()).time
      return (notificationDate ?: 0) < now
   }

   fun updateCompletionStatus() {
      this.isCompleted = !this.isCompleted
      // TODO: Do database update
   }

   fun store() {
      DB.getRemindersDAO()?.create(this)
   }

   fun getBriefTitle() {
      val lines = this.title.lines()
      // TODO: Add setting for longer previews

   }

   // TODO: Actually beautify
   fun getBeautifiedCreation(): String {
      return Time.from(Instant.ofEpochMilli(this.creationDate)).toString()
   }

   fun getBeautifiedNotification(): String {
      return Time.from(Instant.ofEpochMilli(this.creationDate)).toString()
   }

   fun getBeautifiedCompletedOn(): String {
      return Time.from(Instant.ofEpochMilli(this.creationDate)).toString()
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderRow(context: Context?, reminder: Reminder) {
   OutlinedCard(
      onClick = { /* TODO: Implement opener */ },
      modifier = Modifier
         .fillMaxWidth(),
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
            Text(text = reminder.title)
            if(reminder.notificationDate != null)
               Text(
                  text = reminder.getBeautifiedNotification(),
                  fontSize = TextUnit(2.5F, TextUnitType.Em),
                  color = Color.Gray
               )
         }
      }
   }
}

@Preview(showBackground = true)
@Composable
fun ReminderRowPreview() {
   var reminder = Reminder(isCompleted = true, notificationDate = Time.from(Instant.now()).time)
   PlannerTheme {
      ReminderRow(context = null, reminder = reminder)
   }
}
