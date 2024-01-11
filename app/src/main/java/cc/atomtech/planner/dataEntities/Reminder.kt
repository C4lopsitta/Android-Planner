package cc.atomtech.planner.dataEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder (
   @PrimaryKey(autoGenerate = true) val id: Long,
   @ColumnInfo()                    var title: String,
   @ColumnInfo()                    var isCompleted: Boolean = false,
   @ColumnInfo()                    var timestamps: ReminderTimestamps,
   @ColumnInfo()                    var notifies: Boolean = false
) {

}

data class ReminderTimestamps (
   val creation: Long,
   val completion: Long?,
   val notifies: Long?
) {

}
