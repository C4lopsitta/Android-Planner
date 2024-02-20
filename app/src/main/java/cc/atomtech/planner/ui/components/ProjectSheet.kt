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
import cc.atomtech.planner.R
import cc.atomtech.planner.dataEntities.Project

//TODO)) Implement better buttons
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSheet(context: Context?, project: Project, onDismiss: () -> Unit) {
   val showDeleteDialog = remember { mutableStateOf(false) }

   ModalBottomSheet (
      onDismissRequest = onDismiss,
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
         onClick = {  },
         modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
      ) {
         Icon(imageVector = Icons.Rounded.DeleteSweep, contentDescription = null)
         Text(text = context?.getString(R.string.project_sheet_empty) ?: "")
      }

      if(project.id != 1L)
         TextButton(
            onClick = { showDeleteDialog.value = true },
            modifier = Modifier
               .fillMaxWidth()
               .height(64.dp)
         ) {
            Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
            Text(text = context?.getString(R.string.project_sheet_delete) ?: "")
         }
   }

   if(showDeleteDialog.value)
      ProjectDeleteAlertDialog(
         projectName = project.name,
         projectReminders = project.reminderCount,
         context = context,
         onDismiss = { showDeleteDialog.value = false }
      ) {

      }
}

@Composable
fun ProjectDeleteAlertDialog(projectName: String, projectReminders: Int, context: Context?, onDismiss: () -> Unit, onConfirm: () -> Unit) {
   AlertDialog(
      onDismissRequest = { onDismiss() },
      confirmButton = { OutlinedButton(
         onClick = { onConfirm() },
         content = { Text(text = context?.getString(R.string.project_sheet_delete_confirm_confirm_button) ?: "") }
      ) },
      dismissButton = { Button(
         onClick = { onDismiss() },
         content = { Text(text = context?.getString(R.string.project_sheet_delete_confirm_dismiss_button) ?: "") }
      ) },
      title = { Text(text = context?.getString(R.string.project_sheet_delete_confirm_title) ?: "") },
      text = { Text(text = context?.getString(R.string.project_sheet_delete_confirm_text, projectName, projectReminders) ?: "") }
   )
}

