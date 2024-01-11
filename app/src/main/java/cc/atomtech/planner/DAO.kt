package cc.atomtech.planner

import androidx.room.*
import cc.atomtech.planner.dataEntities.Reminder

@Dao
interface RemindersDAO {

}

class Converters {
   @TypeConverter
   fun reminderTimestampsTo
}

@Database(entities = [Reminder::class], version = 1)
abstract class DAO: RoomDatabase() {
   abstract fun reminders(): RemindersDAO
}
