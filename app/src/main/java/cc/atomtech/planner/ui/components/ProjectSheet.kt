package cc.atomtech.planner.ui.components

import android.content.Context
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.DB
import cc.atomtech.planner.R
import cc.atomtech.planner.dataEntities.Project
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//TODO)) Implement better buttons
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSheet(context: Context?, project: Project, onDismiss: (deletedProject: Project?) -> Unit) {
   val selectedDialog = remember { mutableStateOf(ProjectSheetSelectedDialog.NONE) }

   ModalBottomSheet (
      onDismissRequest = { onDismiss(null) },
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
      modifier = Modifier.padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
   ) {
      TextButton(
         onClick = { /*TODO*/ },
         modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
      ) {
         Icon(imageVector = Icons.Rounded.Edit, contentDescription = null)
         Text(text = context?.getString(R.string.project_sheet_edit) ?: "")
      }

      TextButton(
         onClick = { /*TODO*/ },
         modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
      ) {
         Icon(imageVector = Icons.Rounded.Share, contentDescription = null)
         Text(text = context?.getString(R.string.project_sheet_share) ?: "")
      }

      TextButton(
         onClick = { selectedDialog.value = ProjectSheetSelectedDialog.REMINDERS },
         modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
      ) {
         Icon(imageVector = Icons.Rounded.DeleteSweep, contentDescription = null)
         Text(text = context?.getString(R.string.project_sheet_empty) ?: "")
      }

      if(project.id != 1L)
         TextButton(
            onClick = { selectedDialog.value = ProjectSheetSelectedDialog.PROJECT },
            modifier = Modifier
               .fillMaxWidth()
               .height(64.dp)
         ) {
            Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
            Text(text = context?.getString(R.string.project_sheet_delete) ?: "")
         }
   }

   if(selectedDialog.value != ProjectSheetSelectedDialog.NONE)
      if(selectedDialog.value == ProjectSheetSelectedDialog.PROJECT)
         ProjectDeleteAlertDialog(
            projectName = project.name,
            projectReminders = project.reminderCount,
            context = context,
            onDismiss = { selectedDialog.value = ProjectSheetSelectedDialog.NONE }
         ) {
            GlobalScope.launch {
               DB.getRemindersDAO()?.deleteInProject(project.id)
               DB.getProjectsDAO()?.delete(project)
            }
            onDismiss(project)
         }
      else
         ProjectDeleteRemindersAlertDialog(
            projectName = project.name,
            projectReminders = project.reminderCount,
            context = context,
            onDismiss = { selectedDialog.value = ProjectSheetSelectedDialog.NONE }
         ) {
            GlobalScope.launch {
               DB.getRemindersDAO()?.deleteInProject(project.id)
            }
            onDismiss(null)
         }
}

@Composable
fun ProjectDeleteRemindersAlertDialog(projectName: String, projectReminders: Int, context: Context?, onDismiss: () -> Unit, onConfirm: () -> Unit) {
   GenericProjectDeleteAlertDialog(
      projectName = projectName,
      projectReminders = projectReminders,
      context = context,
      confirmItem = context?.getString(R.string.word_reminders) ?: "",
      textContent = context?.getString(R.string.project_sheet_delete_reminders_confirm_text, projectReminders, projectName) ?: "",
      onDismiss = onDismiss
   ) {
      onConfirm()
   }
}

@Composable
fun ProjectDeleteAlertDialog(projectName: String, projectReminders: Int, context: Context?, onDismiss: () -> Unit, onConfirm: () -> Unit) {
   GenericProjectDeleteAlertDialog(
      projectName = projectName,
      projectReminders = projectReminders,
      context = context,
      confirmItem = context?.getString(R.string.word_project) ?: "",
      textContent = context?.getString(R.string.project_sheet_delete_confirm_text, projectName, projectReminders) ?: "",
      onDismiss = onDismiss
   ) {
      onConfirm()
   }
}

@Composable
fun GenericProjectDeleteAlertDialog(projectName: String, projectReminders: Int, confirmItem: String, textContent: String, context: Context?, onDismiss: () -> Unit, onConfirm: () -> Unit) {
   AlertDialog(
      onDismissRequest = { onDismiss() },
      confirmButton = { OutlinedButton(
         onClick = { onConfirm() },
         content = { Text(text = context?.getString(R.string.project_sheet_delete_confirm_confirm_button, confirmItem) ?: "") }
      ) },
      dismissButton = { Button(
         onClick = { onDismiss() },
         content = { Text(text = context?.getString(R.string.project_sheet_delete_confirm_dismiss_button) ?: "") }
      ) },
      title = { Text(text = context?.getString(R.string.project_sheet_delete_confirm_title) ?: "") },
      text = { Text(text = textContent) }
   )
}

enum class ProjectSheetSelectedDialog {
   NONE,
   PROJECT,
   REMINDERS
}
