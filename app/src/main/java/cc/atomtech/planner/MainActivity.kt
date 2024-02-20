package cc.atomtech.planner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bento
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cc.atomtech.planner.activities.EditorActivity
import cc.atomtech.planner.activities.ProjectEditorActivity
import cc.atomtech.planner.activities.SettingsActivity
import cc.atomtech.planner.dataEntities.NavbarItem
import cc.atomtech.planner.dataEntities.Project
import cc.atomtech.planner.dataEntities.Reminder
import cc.atomtech.planner.ui.pages.Dashboard
import cc.atomtech.planner.ui.pages.Labels
import cc.atomtech.planner.ui.pages.Projects
import cc.atomtech.planner.ui.theme.PlannerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

class MainActivity : ComponentActivity() {

   @OptIn(ExperimentalMaterial3Api::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      DB.Connect(context = this@MainActivity, allowDestructiveMigration = true)

      CoroutineScope(Dispatchers.Default).launch {
         val isntFirstLaunch = AppPreferences.readBoolean(this@MainActivity, "isntFirstLaunch")

         if(isntFirstLaunch) return@launch

         AppPreferences.writeBoolean(this@MainActivity, "isntFirstLaunch", true)
         Log.i("MAIN_ACTIVITY", "App has been launched for the first time, running setup")

         // Create default project (id 1)
         DB.getProjectsDAO()?.create(Project(name = "Default project", color = "ffffff", isImportant = false))

         val projects = DB.getProjectsDAO()?.readAll()

         //Todo)) Add some default reminders

         //TODO)) Launch first installation experience
      }

      var reminders: MutableList<Reminder>? = null
      var projects: MutableList<Project> = mutableListOf()

      GlobalScope.launch {
         reminders = (DB.getRemindersDAO()?.readAll())?.toMutableList()
         DB.getProjectsDAO()?.fixColors()
         projects = (DB.getProjectsDAO()?.readAll())?.toMutableList()!!
      }

      super.onCreate(savedInstanceState)

      setContent {
         PlannerTheme {
            val navController = rememberNavController()
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

            val mutableReminders = remember { reminders }

            val useSearchTopBar = remember { mutableStateOf(false) }

            val isInProjects = remember {
               mutableStateOf(navController.currentBackStackEntry?.destination?.route == NavbarItem.list[1].route)
            }

            LaunchedEffect(Unit) {
               useSearchTopBar.value =
                  AppPreferences.readBoolean(this@MainActivity, "useSearchTopBar")
            }

            // A surface container using the 'background' color from the theme
            Scaffold(
               topBar = {
                  if (useSearchTopBar.value)
                     SearchBar(context = this@MainActivity)
                  else
                     CenterAlignedTopAppBar(
                        title = {
                           Text(
                              text = getString(R.string.app_name),
                              maxLines = 1,
                              overflow = TextOverflow.Ellipsis
                           )
                        },
                        actions = {
                           IconButton(onClick = {
                              startActivity(
                                 Intent(
                                    this@MainActivity,
                                    SettingsActivity::class.java
                                 )
                              )
                           }) {
                              Icon(
                                 imageVector = Icons.Rounded.Settings,
                                 contentDescription = getString(R.string.btn_settings_desc)
                              )
                           }
                        },
                        scrollBehavior = scrollBehavior
                     )
               },
               bottomBar = {
                  Navbar(
                     navController = navController,
                     navItems = NavbarItem.BuildList(this)
                  )
               },
               floatingActionButton = {
                  ExtendedFloatingActionButton(
                     onClick = { fabOnClick(isInProjects) },
                     text = {
                        if(isInProjects.value)
                           Text(text = getString(R.string.fab_add_project_label))
                        else
                           Text(text = getString(R.string.fab_add_label))
                     },
                     icon = {
                        if(isInProjects.value)
                           Icon(Icons.Rounded.Bento, getString(R.string.fab_add_project_label))
                        else
                           Icon(Icons.Rounded.Add, getString(R.string.fab_add_label))
                     },
                     shape = FloatingActionButtonDefaults.extendedFabShape
                  )
               },
               floatingActionButtonPosition = FabPosition.End,
               content = {
                  ContentController(
                     navController = navController,
                     paddingValues = it,
                     context = this,
                     reminders = reminders,
                     mutableReminders = mutableReminders,
                     isInProject = isInProjects,
                     mutableProjcets = projects
                  )
                  if(projects.size == 0)
                     AlertDialog(
                        onDismissRequest = {},
                        confirmButton = { Button(onClick = {
                           exitProcess(0)
                        }) {
                           Text(text = getString(R.string.wrong_install_close))
                        } },
                        dismissButton = {
                           Button(
                              onClick = {
                                 val uri = Uri.parse("https://github.com/c4lopsitta/Android-Planner/issues")
                                 val intent = Intent(Intent.ACTION_VIEW, uri)
                                 startActivity(intent)
                              },
                              colors = ButtonDefaults.outlinedButtonColors()
                        ) {
                           Text(text = getString(R.string.wrong_install_make_issue))
                        }},
                        title = { Text(text = getString(R.string.wrong_install_title)) },
                        text = { Text(text = getString(R.string.wrong_install_body)) }
                     )
                  }
               )
            }
         }
   }

   private fun fabOnClick(isInProject: MutableState<Boolean>) {
      if(isInProject.value)
         startActivity(
            Intent(this@MainActivity, ProjectEditorActivity::class.java).putExtra("isCreator", true)
         )
      else
         startActivity(
            Intent(this@MainActivity, EditorActivity::class.java).putExtra("isCreator", true)
         )
   }
}

@Composable
fun Navbar(navController: NavHostController, navItems: List<NavbarItem>) {
   NavigationBar() {
      val currentNavEntry: String = navController.currentBackStackEntry?.destination?.route ?: "home"
      navItems.forEach { item ->
         NavigationBarItem(
            selected = (currentNavEntry == item.route),
            label = { Text(text = item.label) },
            alwaysShowLabel = false,
            onClick = {
               navController.navigate(item.route) {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
               }
            },
            icon = {
               if (currentNavEntry == item.route)
                  Icon(item.icon, item.label)
               else
                  Icon(item.unselectedIcon, item.label)
            },
         )
      }
   }
}

@Composable
fun ContentController(navController: NavHostController,
                      reminders: MutableList<Reminder>?,
                      mutableReminders: MutableList<Reminder>?,
                      mutableProjcets: MutableList<Project>,
                      isInProject: MutableState<Boolean>,
                      paddingValues: PaddingValues,
                      context: Context?) {
   NavHost(
      navController = navController,
      startDestination = "home",
      modifier = Modifier.padding(paddingValues = paddingValues),
      builder = {
         composable(route = "home") {
            isInProject.value = false
            Dashboard(context = context, reminders = mutableReminders)
         }
         composable(route = "labels") {
            isInProject.value = false
            Labels()
         }
         composable(route = "projects") {
            isInProject.value = true
            Projects(projects = mutableProjcets, context = context)
         }
      }
   )
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun SearchBar(context: Context) {
   val searchQuery = remember { mutableStateOf("") }
   val isSearchExpanded = remember { mutableStateOf(false) }

   Box(
      modifier = Modifier
         .padding(top = 12.dp)
         .padding(horizontal = 12.dp)
         .fillMaxWidth(),
      contentAlignment = Alignment.Center,
   ) {
      DockedSearchBar(
         query = searchQuery.value,
         placeholder = {
            Text(
               text = context.getString(R.string.app_name),
               fontSize = TextUnit(4F, TextUnitType.Em)
            )
         },
         onQueryChange = {
            searchQuery.value = it
         },
         onSearch = {

         },
         active = isSearchExpanded.value,
         onActiveChange = {
            isSearchExpanded.value = it
         },
         modifier = Modifier.fillMaxWidth(),
         leadingIcon = {
            IconButton(onClick = { /*TODO*/ }) {
               Icon(
                  imageVector = Icons.Rounded.Search,
                  contentDescription = context.getString(R.string.btn_search_desc)
               )
            }
         },
         trailingIcon = {
            IconButton(onClick = {
               context.startActivity(Intent(context, SettingsActivity::class.java))
            }) {
               Icon(
                  imageVector = Icons.Rounded.Settings,
                  contentDescription = context.getString(R.string.btn_settings_desc)
               )
            }
         },
         content = {
            Text(text = "Not much to see here for now")
         },
         tonalElevation = 6.dp
      )
   }
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
      content = { ContentController(
         navController = navController,
         paddingValues = it,
         reminders = null,
         mutableReminders = null,
         context = null,
         isInProject = mutableStateOf(false),
         mutableProjcets = mutableListOf()
      ) }
   )
}
