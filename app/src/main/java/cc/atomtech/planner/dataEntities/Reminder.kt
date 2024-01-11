package cc.atomtech.planner.dataEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4
@Entity(tableName = "reminders")
data class Reminder (
   @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Long,
   @ColumnInfo()                                                var title: String,
   @ColumnInfo()                                                var isCompleted: Boolean = false,
   @ColumnInfo()                                                var creationDate: Long,
   @ColumnInfo()                                                var completionDate: Long?,
   @ColumnInfo()                                                var notificationDate: Long?,
   @ColumnInfo()                                                var notifies: Boolean = false
) {

}