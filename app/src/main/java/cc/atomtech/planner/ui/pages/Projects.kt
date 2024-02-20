package cc.atomtech.planner.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.dataEntities.Project
import cc.atomtech.planner.dataEntities.ProjectRow
import cc.atomtech.planner.ui.theme.PlannerTheme

@Composable
fun Projects(projects: MutableList<Project>) {
   Column (
      modifier = Modifier.fillMaxSize()
   ) {
//      Text(text = "Projects")
      LazyColumn(
         verticalArrangement = Arrangement.spacedBy(8.dp),
         contentPadding = PaddingValues(12.dp),
         content = {
            items(count = projects.size, key = null) { index ->
               val item = projects[index]
               if(item.color.length < 6) {
                  item.color = "ffffff"
               }

               item.loadCount()
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
