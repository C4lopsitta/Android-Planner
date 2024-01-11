package cc.atomtech.planner.dataEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4
@Entity(tableName = "projects")
data class Project(
   @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") var id: Long,
) {

}