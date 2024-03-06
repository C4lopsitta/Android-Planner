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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cc.atomtech.planner.DB
import cc.atomtech.planner.activities.ProjectEditorActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Entity(tableName = "projects")
data class Project(
   @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") var id: Long = 0,
   @ColumnInfo()                    var name: String = "",
   @ColumnInfo()                    var color: String = "ffffff",
   @ColumnInfo()                    var isImportant: Boolean = false,
   @ColumnInfo()                    var reminderCount: Int = 0,
) {
   fun loadCount() {
      GlobalScope.launch {
         val count = (DB.getRemindersDAO()?.countRemindersInProject((this@Project).id)) ?: 0
         reminderCount = count
      }
   }

   fun store() {
      GlobalScope.launch {
         DB.getProjectsDAO()?.create(this@Project)
      }
   }

   fun update() {
      GlobalScope.launch {
         DB.getProjectsDAO()?.update(this@Project)
      }
   }

   fun delete() {
      GlobalScope.launch {
         DB.getProjectsDAO()?.delete(this@Project)
      }
   }

   fun getJSONString(): String {
      return """
         {
            "name": "${this@Project.name}",
            "color": "#${this@Project.color}",
            "isImportant": ${this@Project.isImportant}
         }
      """.trimIndent()
   }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectRow(project: Project, context: Context?, onPressHold: (Project) -> Unit) {
   val haptics = LocalHapticFeedback.current

   OutlinedCard(
      modifier = Modifier
         .fillMaxWidth()
         .combinedClickable (
            onClick = {
               val intent = Intent(context, ProjectEditorActivity::class.java)
               intent.putExtra("isCreator", false)
               intent.putExtra("id", project.id)
               context?.startActivity(intent)
            },
            onLongClick = {
               haptics.performHapticFeedback(HapticFeedbackType.LongPress)
               onPressHold(project)
            },
            onLongClickLabel = ""
         ),
      shape = CardDefaults.outlinedShape,
      border = BorderStroke(width = 0.dp, color = Color.Transparent)
   ) {
      Row (
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier.defaultMinSize(minHeight = 80.dp)
      ) {
         val colorEntity = ColorEntity()
         colorEntity.buildByHex(project.color)
         Icon(
            modifier = Modifier.width(60.dp),
            imageVector = Icons.Filled.Circle,
            contentDescription = null,
            tint = Color(colorEntity.red, colorEntity.green, colorEntity.blue)
         )
         Column {
            Text(text = project.name)
            Text(
               text = "Contains ${project.reminderCount} reminders",
               fontSize = TextUnit(12F, TextUnitType.Sp),
               color = Color.Gray
            )
         }
      }
   }
}


