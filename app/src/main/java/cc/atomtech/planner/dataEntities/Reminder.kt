package cc.atomtech.planner.dataEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Time
import java.time.Instant

@Entity(
   tableName = "reminders",
   foreignKeys = arrayOf(
      ForeignKey(
         entity = Project::class,
         parentColumns = arrayOf("rowid"),
         childColumns = arrayOf("rowid")
      )
   )
)
data class Reminder (
   @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Long,
   @ColumnInfo()                    var title: String,
   @ColumnInfo()                    var isCompleted: Boolean = false,
   @ColumnInfo()                    var creationDate: Long,
   @ColumnInfo()                    var completionDate: Long?,
   @ColumnInfo()                    var notificationDate: Long?,
   @ColumnInfo()                    var notifies: Boolean = false,
   @ColumnInfo()                    var labels: ArrayList<String>,
   @ColumnInfo()                    var projectIdentifier: Long?
) {
   fun isLate(): Boolean {
      val now: Long = Time.from(Instant.now()).time
      return (notificationDate ?: 0) < now
   }
}
