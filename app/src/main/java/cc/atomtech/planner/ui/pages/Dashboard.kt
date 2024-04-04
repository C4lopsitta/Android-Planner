package cc.atomtech.planner.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.AppPreferences
import cc.atomtech.planner.dataEntities.ButtonData
import cc.atomtech.planner.R
import cc.atomtech.planner.dataEntities.Project
import cc.atomtech.planner.dataEntities.Reminder
import cc.atomtech.planner.dataEntities.ReminderCard
import cc.atomtech.planner.ui.components.ReminderSheet
import cc.atomtech.planner.ui.theme.PlannerTheme

class DashboardCompanion(context: Context?) {
   val blocks = listOf(
      ButtonData(
         context?.getString(R.string.dash_card_reminders_label) ?: "HC - Reminders",
         Icons.Rounded.Notifications) {},
      ButtonData(
         context?.getString(R.string.dash_card_due_soon_label) ?: "HC - Due soon",
         Icons.Rounded.Alarm) {},
      ButtonData(
         context?.getString(R.string.dash_card_important_label) ?: "HC - Important",
         Icons.Rounded.Star) {},
      ButtonData(
         context?.getString(R.string.dash_card_shared_with_you_label) ?: "HC - Shared with u",
         Icons.Rounded.People) {}
   )
}

@Composable
fun Dashboard(context: Context?,
              reminders: MutableList<Reminder>?,
              projects: MutableList<Project>?) {
   val dashCompanion = DashboardCompanion(context)
   val selectedReminder = remember { mutableStateOf<Reminder?>(null) }
   val showQuickChips = remember { mutableStateOf(true) }

   LaunchedEffect(Unit) {
      if(context != null)
         showQuickChips.value = AppPreferences.readBoolean(context, context.getString(R.string.datastore_show_dash_chips))
   }

   Column (
      modifier = Modifier
         .fillMaxSize()
   ) {
      if(showQuickChips.value) {
         LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
               .requiredHeight(200.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(12.dp),
            content = {
               items(count = dashCompanion.blocks.size, key = null) { index ->
                  val buttonData = dashCompanion.blocks[index]
                  IconCard(buttonData = buttonData)
               }
            })
      }

      LazyColumn(
         verticalArrangement = Arrangement.spacedBy(8.dp),
         contentPadding = PaddingValues(12.dp),
         content = {
            items(count = reminders?.size ?: 0, key = null) {
               val item = reminders?.get(it) ?: Reminder()
               ReminderCard(context = context, reminder = item) { selectedReminder.value = it }
            }
         }
      )
   }
   if(selectedReminder.value != null)
      ReminderSheet(
         context = context,
         reminder = selectedReminder.value!!
      ) { dismissedReminder, dismissAction ->
         selectedReminder.value = null

         if(dismissedReminder != null) {
            if (dismissAction == ReminderSheet.DismissAction.DELETE)
               reminders?.remove(dismissedReminder)
            if (dismissAction == ReminderSheet.DismissAction.EDIT)
               TODO()
            if (dismissAction == ReminderSheet.DismissAction.SHARE)
               dismissedReminder.share(context)
            if (dismissAction == ReminderSheet.DismissAction.COPY)
               TODO()
            if (dismissAction == ReminderSheet.DismissAction.JSON_SHARE) {
               val project = projects?.find { it.id == dismissedReminder.projectIdentifier }
               dismissedReminder.shareAsJSON(context, project)
            }
         }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconCard(buttonData: ButtonData) {
   Card(
      onClick = buttonData.onClick,
      modifier = Modifier
         .height(80.dp),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
   ) {
      Column(
         verticalArrangement = Arrangement.Bottom,
         modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
      ) {
         buttonData.icon
            ?.let{Icon( imageVector = it, contentDescription = buttonData.label) }
         Text(text = buttonData.label)
      }
   }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
   PlannerTheme {
      Dashboard(null, null, null)
   }
}

@Preview(showBackground = true)
@Composable
fun IconCardPreview() {
   PlannerTheme {
      IconCard(buttonData = ButtonData("What a card", Icons.Rounded.CreditCard) {})
   }
}
