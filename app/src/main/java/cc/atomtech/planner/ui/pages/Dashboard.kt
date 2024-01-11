package cc.atomtech.planner.ui.pages

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.ButtonData
import cc.atomtech.planner.R
import cc.atomtech.planner.ui.theme.PlannerTheme

class DashboardCompanion(context: Context?) {
   val Blocks = listOf(
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
fun Dashboard(context: Context?) {
   val dashCompanion = DashboardCompanion(context)

   Column (
      modifier = Modifier
         .verticalScroll(enabled = true, state = ScrollState(0))
         .fillMaxSize()
   ) {
      LazyVerticalGrid(
         columns = GridCells.Fixed(2),
         modifier = Modifier.height(180.dp),
         verticalArrangement = Arrangement.spacedBy(8.dp),
         horizontalArrangement = Arrangement.spacedBy(8.dp),
         contentPadding = PaddingValues(12.dp),
         content = {
         items(count = dashCompanion.Blocks.size, key = null) { index ->
            val buttonData = dashCompanion.Blocks[index]
            IconCard(buttonData = buttonData)
         }
      })
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
      Dashboard(null)
   }
}

@Preview(showBackground = true)
@Composable
fun IconCardPreview() {
   PlannerTheme {
      IconCard(buttonData = ButtonData("What a card", Icons.Rounded.CreditCard) {})
   }
}
