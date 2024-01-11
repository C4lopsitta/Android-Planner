package cc.atomtech.planner

import androidx.room.*
import cc.atomtech.planner.dataEntities.Project
import cc.atomtech.planner.dataEntities.Reminder

@Dao
interface RemindersDAO {

}

class Converters {
   @TypeConverter
   fun labelsToDb(labels: ArrayList<String>): String {
      var csv = ""
      for (label in labels) {
         csv += "$label;"
      }
      csv.dropLast(1)
      return csv
   }

   @TypeConverter
   fun dbToLabels(labels: String): ArrayList<String> {
      val list = ArrayList<String>()
      labels.split(";").forEach { string ->
         list.add(string)
      }
      return list
   }
}

@Database(entities = [Reminder::class, Project::class], version = 1)
@TypeConverters(Converters::class)
abstract class DAO: RoomDatabase() {
   abstract fun reminders(): RemindersDAO
}
