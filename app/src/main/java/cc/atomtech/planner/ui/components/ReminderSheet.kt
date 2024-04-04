package cc.atomtech.planner.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DataObject
import androidx.compose.material.icons.rounded.Delete
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
import cc.atomtech.planner.activities.EditorActivity
import cc.atomtech.planner.R
import cc.atomtech.planner.dataEntities.Reminder

//TODO)) Implement better buttons
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSheet(context: Context?, reminder: Reminder, onDismiss: (reminder: Reminder?, action: ReminderSheet.DismissAction) -> Unit) {
   val isDialogOpen = remember { mutableStateOf(false) }

   ModalBottomSheet (
      onDismissRequest = { onDismiss(null, ReminderSheet.DismissAction.NO_ACTION) },
      sheetState = rememberModalBottomSheetState()
   ) {
      TextButton(
         onClick = {
            val intent = Intent(context, EditorActivity::class.java)
               .putExtra("isCreator", false)
               .putExtra("rowid", reminder.id)
            context?.startActivity(intent)
         },
         modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
      ) {
         Icon(imageVector = Icons.Rounded.Edit, contentDescription = null)
         Text(text = context?.getString(R.string.reminder_sheet_edit) ?: "")
      }

      TextButton(
         onClick = { onDismiss(reminder, ReminderSheet.DismissAction.SHARE) },
         modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
      ) {
         Icon(imageVector = Icons.Rounded.Share, contentDescription = null)
         Text(text = context?.getString(R.string.reminder_sheet_share) ?: "")
      }

      TextButton(
         onClick = { onDismiss(reminder, ReminderSheet.DismissAction.JSON_SHARE) },
         modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
      ) {
         Icon(imageVector = Icons.Rounded.DataObject, contentDescription = null)
         Text(text = context?.getString(R.string.reminder_sheet_json_share) ?: "")
      }

      TextButton(
         onClick = { isDialogOpen.value = true },
         modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
      ) {
         Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
         Text(text = context?.getString(R.string.reminder_sheet_delete) ?: "")
      }
   }

   if(isDialogOpen.value)
      ReminderSheet.GenericReminderAlertDialog(
         title = context?.getString(R.string.reminder_sheet_confirm_title) ?: "",
         body = context?.getString(R.string.reminder_sheet_confirm_body) ?: "",
         context = context,
         onDismiss = { onDismiss(null, ReminderSheet.DismissAction.NO_ACTION) }) {
         reminder.delete()
         onDismiss(reminder, ReminderSheet.DismissAction.DELETE)
      }
}

object ReminderSheet {
   @Composable
   fun GenericReminderAlertDialog(title: String,
                                  body: String,
                                  context: Context?,
                                  onDismiss: () -> Unit,
                                  onConfirm: () -> Unit) {
      AlertDialog(
         onDismissRequest = { onDismiss() },
         confirmButton = { OutlinedButton (
            onClick = { onConfirm() },
            content = { Text(text = context?.getString(R.string.reminder_sheet_confirm_confirm) ?: "") }
         ) },
         dismissButton = { Button(
            onClick = { onDismiss() },
            content = { Text(
               text = context?.getString(R.string.reminder_sheet_confirm_dismiss) ?: ""
            ) }
         ) },
         title = { Text(text = title) },
         text = { Text(text = body) }
      )
   }

   enum class DismissAction() {
      NO_ACTION,
      DELETE,
      EDIT,
      SHARE,
      COPY,
      JSON_SHARE
   }
}
