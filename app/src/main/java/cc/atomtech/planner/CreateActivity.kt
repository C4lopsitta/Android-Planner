package cc.atomtech.planner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.dataEntities.Reminder
import cc.atomtech.planner.ui.theme.PlannerTheme

class CreateActivity : ComponentActivity() {
   val reminder = Reminder()
   @OptIn(ExperimentalMaterial3Api::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      setContent {
         PlannerTheme {
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

            Scaffold(
               modifier = Modifier.fillMaxSize(),
               topBar = {
                  CenterAlignedTopAppBar(
                     title = {
                        Text(
                           text = "${getString(R.string.activity_create)} ${getString(R.string.word_reminder)}"
                        )
                     },
                     scrollBehavior = scrollBehavior,
                     navigationIcon = {
                        IconButton(onClick = {
                           navigateUpTo(Intent(this@CreateActivity, MainActivity::class.java))
                        }) {
                           Icon(
                              imageVector = Icons.Rounded.ArrowBack,
                              contentDescription = getString(R.string.btn_back_desc)
                           )
                        }
                     }
                  )
               },
               floatingActionButton = {
                  ExtendedFloatingActionButton(onClick = {
                        // TODO: make async
                        reminder.store()
                     },
                     icon = { Icon(
                        imageVector = Icons.Rounded.Save,
                        contentDescription = getString(R.string.fab_save_label)
                     ) },
                     text = { Text(text = getString(R.string.fab_save_label)) }
                  )
               },
               content = {
                  CreateColumn(this@CreateActivity, it, reminder)
               }
            )
         }
      }
   }
}

@Composable
fun CreateColumn(context: Context, paddingValues: PaddingValues, reminder: Reminder) {
   val title = remember { mutableStateOf("") }

   Column (
      modifier = Modifier
         .padding(paddingValues)
         .fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
   ) {
      OutlinedTextField(
         value = title.value,
         onValueChange = {
            title.value = it
            reminder.title = it
         },
         placeholder = { Text(text = context.getString(R.string.txt_reminder_title)) },
         label = { Text(text = context.getString(R.string.txt_reminder_title)) },
         leadingIcon = { Icon(imageVector = Icons.Rounded.Title, contentDescription = context.getString(R.string.txt_reminder_title))},
         modifier = Modifier
            .fillMaxWidth(0.925f)

      )


   }
}
