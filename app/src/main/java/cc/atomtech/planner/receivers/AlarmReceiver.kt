package cc.atomtech.planner.receivers

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import cc.atomtech.planner.R
import cc.atomtech.planner.dataEntities.Reminder

object AlarmManager {
   private final lateinit var manager: AlarmManager

   public fun setManager(context: Context) {
      manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
   }

   public fun enableReminderNotification(reminder: Reminder, context: Context?) {
      if(context == null) return
      val intent = Intent(context, AlarmReceiver::class.java)
      intent.putExtra("title", reminder.title)
      intent.putExtra("id", reminder.id)
      intent.putExtra("project", reminder.projectIdentifier)
      intent.putExtra("labels", reminder.labels)

      val pendingIntent = PendingIntent.getService(context, reminder.id?.toInt() ?: 0, intent, PendingIntent.FLAG_IMMUTABLE)

      if(reminder.notifies && reminder.notificationDate != null)
         scheduleAlarm(context, pendingIntent, reminder.notificationDate!!)
      else
         TODO("Add big ass error message")


   }

   private fun scheduleAlarm(context: Context?, intentAction: PendingIntent, timestamp: Long) {
      if(!canSchedule()) {
         TODO("Add a snackbar or some kind of warning")
      }
      this.manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, intentAction)

   }

   private fun scheduleImportantAlarm(context: Context?, intentAction: PendingIntent, timestamp: Long) {
      if(!canSchedule())
         TODO("Like scheduleAlarm()")

      Toast.makeText(context, context?.getString(R.string.not_yet_implemented) ?: "", Toast.LENGTH_SHORT).show()

//      this.manager.setAlarmClock(timestamp.toInt(), intentAction)
   }

   fun canSchedule(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      this.manager.canScheduleExactAlarms()
   } else {
      true
   }
}

class AlarmReceiver(context: Context) : BroadcastReceiver() {
   override fun onReceive(context: Context?, intent: Intent?) {
      TODO("Not yet implemented")
   }

}