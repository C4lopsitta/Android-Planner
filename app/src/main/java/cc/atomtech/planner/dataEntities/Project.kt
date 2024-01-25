package cc.atomtech.planner.dataEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
   @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") var id: Long = 0,
   @ColumnInfo()                    var name: String,
   @ColumnInfo()                    var color: String,
   @ColumnInfo()                    var isImportant: Boolean
) {

}

