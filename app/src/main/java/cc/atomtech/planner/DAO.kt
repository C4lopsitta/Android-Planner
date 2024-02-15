package cc.atomtech.planner

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

@Dao
interface ProjectsDAO {
   @Query("SELECT * FROM projects WHERE rowid = :id")
   suspend fun read(id: Long): Project

   @Query("SELECT * FROM projects")
   suspend fun readAll(): List<Project>

   @Insert
   fun create(project: Project): Long

   @Update
   fun update(project: Project)

   @Delete
   fun delete(project: Project)
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

@Database(entities = [Reminder::class, Project::class], version = 2)
@TypeConverters(Converters::class)
abstract class DAO: RoomDatabase() {
   abstract fun reminders(): RemindersDAO
   abstract fun projects(): ProjectsDAO
}

class DB() {
   companion object {
      private var db: RoomDatabase? = null

      fun Connect(context: Context, allowDestructiveMigration: Boolean = false) {
         if(db != null)
            return
         if(allowDestructiveMigration) {
            db = Room.databaseBuilder(context, DAO::class.java, "planner_db")
               .fallbackToDestructiveMigration()
               .fallbackToDestructiveMigrationOnDowngrade()
               .build()
            return
         }
         db = Room.databaseBuilder(context, DAO::class.java, "planner_db")
            .build()
      }

      fun getRemindersDAO(): RemindersDAO? {
         if(db != null)
            return (db as DAO).reminders()
         return null
      }

      fun getProjectsDAO(): ProjectsDAO? {
         if(db != null)
            return (db as DAO).projects()
         return null
      }
   }
}

object Migrations {
   val v1_2 = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {

      }
   }
}
