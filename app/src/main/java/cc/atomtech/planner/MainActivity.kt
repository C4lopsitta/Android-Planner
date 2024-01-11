package cc.atomtech.planner

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import cc.atomtech.planner.ui.theme.PlannerTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cc.atomtech.planner.ui.pages.Dashboard
import cc.atomtech.planner.ui.pages.Labels
import cc.atomtech.planner.ui.pages.Projects

class MainActivity : ComponentActivity() {
   @OptIn(ExperimentalMaterial3Api::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
         PlannerTheme {
            val navController = rememberNavController()
            // A surface container using the 'background' color from the theme
            Scaffold (
               bottomBar = { Navbar(
                  navController = navController,
                  navItems = NavbarItem.BuildList(this)
               )},
               floatingActionButton = { ExtendedFloatingActionButton(
                  onClick = { fabOnClick() },
                  text = { Text(text = getString(R.string.fab_add_label)) },
                  icon = { Icon(Icons.Rounded.Add, getString(R.string.fab_add_label)) },
                  shape = FloatingActionButtonDefaults.extendedFabShape
               )},
               floatingActionButtonPosition = FabPosition.End,
               content = { ContentController(
                  navController = navController,
                  paddingValues = it,
                  context = this
               )}
            )
         }
      }
   }


   private fun fabOnClick() {
      Toast.makeText(this, "Im as useless as my creator!", Toast.LENGTH_SHORT).show()
   }
}

@Composable
fun Navbar(navController: NavHostController, navItems: List<NavbarItem>) {
   NavigationBar() {
      val currentNavEntry: String = navController.currentBackStackEntry?.destination?.route ?: "home"
      navItems.forEach { item ->
         NavigationBarItem(
            selected = ( currentNavEntry.equals(item.route) ),
            label = { Text(text = item.label) },
            alwaysShowLabel = false,
            onClick = {
               navController.navigate(item.route) {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
                  Log.i("Navigator", "I've  been selected! $currentNavEntry -> ${item.route}")
               }
            },
            icon = {
               if (currentNavEntry.equals(item.route))
                  Icon(item.icon, item.label)
               else
                  Icon(item.unselectedIcon, item.label)
            },
         )
      }
   }
}

@Composable
fun ContentController(navController: NavHostController, paddingValues: PaddingValues, context: Context?) {
   NavHost(
      navController = navController,
      startDestination = "home",
      modifier = Modifier.padding(paddingValues = paddingValues),
      builder = {
         composable(route = "home") {
            Dashboard(context = context)
         }
         composable(route = "labels") {
            Labels()
         }
         composable(route = "projects") {
            Projects()
         }
      }
   )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF000000)
@Composable
fun AppPreview() {
   val navController = rememberNavController()
   // A surface container using the 'background' color from the theme
   Scaffold (
      bottomBar = { Navbar(navController = navController, navItems = NavbarItem.list) },
      floatingActionButton = { ExtendedFloatingActionButton(
         onClick = {  },
         text = { Text(text = "Add") },
         icon = { Icon(Icons.Rounded.Add, "Add") },
         shape = FloatingActionButtonDefaults.extendedFabShape
      ) },
      floatingActionButtonPosition = FabPosition.End,
      content = { ContentController(navController = navController, paddingValues = it, null) }
   )
}
