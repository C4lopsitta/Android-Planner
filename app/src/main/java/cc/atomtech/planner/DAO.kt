package cc.atomtech.planner

import android.content.Context
import androidx.room.*
import cc.atomtech.planner.dataEntities.Project
import cc.atomtech.planner.dataEntities.Reminder

@Dao
interface RemindersDAO {
   @Query("SELECT * FROM reminders WHERE rowid = :id")
   suspend fun read(id: Long): Reminder

   @Query("SELECT * FROM reminders")
   suspend fun readAll(): List<Reminder>

   @Insert
   fun create(reminder: Reminder): Long

   @Update
   fun update(reminder: Reminder)

   @Delete
   fun delete(reminder: Reminder)
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

class DB() {
   companion object {
      var db: RoomDatabase? = null

      fun Connect(context: Context) {
         if(db != null)
            return
         db = Room.databaseBuilder(context, DAO::class.java, "planner_db")
            .build()
      }

      fun getRemindersDAO(): RemindersDAO? {
         if(db != null)
            return (db as DAO).reminders()
         return null
      }
   }
}
