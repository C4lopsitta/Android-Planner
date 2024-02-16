package cc.atomtech.planner.dataEntities

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.room.Fts4
import androidx.room.Ignore
import androidx.room.PrimaryKey
import cc.atomtech.planner.DB
import cc.atomtech.planner.EditorActivity
import cc.atomtech.planner.ui.theme.PlannerTheme
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
}

@Composable
fun ProjectRow(project: Project) {
   OutlinedCard(
      onClick = {
//         val intent = Intent(context, EditorActivity::class.java)
//            .putExtra("isCreator", false)
//            .putExtra("rowid", reminder.id)
//         context?.startActivity(intent)
      },
      modifier = Modifier
         .fillMaxWidth(),
      shape = CardDefaults.outlinedShape,
      border = BorderStroke(width = 0.dp, color = Color.Transparent)
   ) {
      Row (
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier.defaultMinSize(minHeight = 80.dp)
      ) {
         val colorEntity = ColorEntity()
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

@Preview()
@Composable
fun ProjectRowPreview() {
   val project = Project(42, "Lorem Ipsum", "feba00", false)
   PlannerTheme {
      ProjectRow(project = project)
   }
}

