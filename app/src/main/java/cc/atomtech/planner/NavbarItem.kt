package cc.atomtech.planner

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bento
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.rounded.Bento
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.ui.graphics.vector.ImageVector

data class NavbarItem (
   val label: String,
   val icon: ImageVector,
   val unselectedIcon: ImageVector,
   val route: String
) {
   companion object {
      fun BuildList(context: Context): List<NavbarItem> {
         return listOf<NavbarItem> (
            NavbarItem(context.getString(R.string.nav_projects_label), Icons.Rounded.Bento, Icons.Outlined.Bento, "projects"),
            NavbarItem(context.getString(R.string.nav_dashboard_label), Icons.Rounded.Dashboard, Icons.Outlined.Dashboard, "home"),
            NavbarItem(context.getString(R.string.nav_labels_label), Icons.Rounded.Tag, Icons.Outlined.Tag, "labels")
         )
      }


      val list = listOf<NavbarItem> (
         NavbarItem("Dashboard", Icons.Rounded.Dashboard, Icons.Outlined.Dashboard, "home"),
         NavbarItem("Projects", Icons.Rounded.Bento, Icons.Outlined.Bento, "projects"),
         NavbarItem("Labels", Icons.Rounded.Tag, Icons.Outlined.Tag, "labels")
      )
   }
}