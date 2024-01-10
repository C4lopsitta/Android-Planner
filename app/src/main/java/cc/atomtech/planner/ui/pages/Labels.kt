package cc.atomtech.planner.ui.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cc.atomtech.planner.ui.theme.PlannerTheme

@Composable
fun Labels() {
   Text(text = "Labels UI")
}

@Preview(showBackground = true)
@Composable
fun LabelsPreview() {
   PlannerTheme {
      Labels()
   }
}
