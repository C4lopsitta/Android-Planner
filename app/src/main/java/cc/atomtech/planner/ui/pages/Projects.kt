package cc.atomtech.planner.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cc.atomtech.planner.dataEntities.Project
import cc.atomtech.planner.dataEntities.ProjectRow
import cc.atomtech.planner.ui.theme.PlannerTheme

@Composable
fun Projects(projects: MutableList<Project>) {
   Column (
      modifier = Modifier.fillMaxSize()
   ) {
      Text(text = "Projects")
      LazyColumn(
         content = {
            items(count = projects.size, key = null) { index ->
               val item = projects[index]
               ProjectRow(project = item)
            }
      })
   }
}

@Preview(showBackground = true)
@Composable
fun ProjectsPreview() {
   PlannerTheme {
      Projects(mutableListOf())
   }
}
