package cc.atomtech.planner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SwitchRow(value: MutableState<Boolean>,
              onValueChanged: (Boolean) -> Unit,
              label: String) {
   Row(
      modifier = Modifier
         .fillMaxWidth(0.975f)
         .height(50.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically
   ) {
      Switch(checked = value.value, onCheckedChange = onValueChanged)
      Text(text = label, maxLines = 1)
   }
}
