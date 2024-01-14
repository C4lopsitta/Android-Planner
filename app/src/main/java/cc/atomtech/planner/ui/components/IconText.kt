package cc.atomtech.planner.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

@Composable
fun IconText(imageVector: ImageVector,
             text: String,
             modifier: Modifier? = null,
             iconModifiers: Modifier? = null,
             textModifiers: Modifier? = null,
             isError: Boolean = false) {
   Row (
      modifier = modifier ?: Modifier,
      verticalAlignment = Alignment.CenterVertically,
   ) {
      if(!isError) {
         Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = iconModifiers ?: Modifier
         )
         Text(
            text = text,
            modifier = textModifiers ?: Modifier
         )
      } else {
         Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = iconModifiers ?: Modifier
         )
         Text(
            text = text,
            modifier = textModifiers ?: Modifier,
            fontSize = TextUnit(2.5f, TextUnitType.Em),
            color = Color.Red //todo fix color
         )
      }
   }
}