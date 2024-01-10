package cc.atomtech.planner

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
      val list = listOf<NavbarItem> (
         NavbarItem("Dashboard", Icons.Rounded.Dashboard, Icons.Outlined.Dashboard, "home"),
         NavbarItem("Projects", Icons.Rounded.Bento, Icons.Outlined.Bento, "projects"),
         NavbarItem("Labels", Icons.Rounded.Tag, Icons.Outlined.Tag, "labels")
      )
   }
}