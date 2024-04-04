package cc.atomtech.planner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
fun IconText(imageVector: ImageVector,
             text: String,
             modifier: Modifier? = null,
             iconModifiers: Modifier? = null,
             textModifiers: Modifier? = null,
             isError: Boolean = false) {
   val rowGap = if(text.length < 80) 5.dp else 8.dp

   Row (
      modifier = modifier ?: Modifier,
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(rowGap)
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
            tint = Color.Red,
            modifier = Modifier.size(20.dp)
         )
         Text(
            text = text,
            modifier = textModifiers ?: Modifier,
            fontSize = TextUnit(2.75f, TextUnitType.Em),
            color = Color.Red //todo fix color
         )
      }
   }
}