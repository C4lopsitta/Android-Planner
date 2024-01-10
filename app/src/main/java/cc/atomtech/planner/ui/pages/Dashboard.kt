package cc.atomtech.planner.ui.pages

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Explicit
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.ButtonData
import cc.atomtech.planner.ui.theme.PlannerTheme

class DashboardCompanion {
   companion object {
      val Blocks = listOf<ButtonData>(
         ButtonData("Reminders", Icons.Rounded.Notifications) {},
         ButtonData("Due soon", Icons.Rounded.Alarm) {},
         ButtonData("Forgetters", Icons.Rounded.Explicit) {},
         ButtonData("Forgetters", Icons.Rounded.Explicit) {}
      )
   }
}

@Composable
fun Dashboard() {
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
         items(count = DashboardCompanion.Blocks.size, key = null) { index ->
            val buttonData = DashboardCompanion.Blocks[index]
            IconChip(buttonData = buttonData)
         }
      })
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconChip(buttonData: ButtonData) {
   Card(
      onClick = buttonData.onClick,
      modifier = Modifier
         .height(80.dp)
   ) {
      buttonData.icon
         ?.let{Icon( imageVector = it, contentDescription = buttonData.label) }
      Text(text = buttonData.label)
   }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
   PlannerTheme {
      Dashboard()
   }
}
