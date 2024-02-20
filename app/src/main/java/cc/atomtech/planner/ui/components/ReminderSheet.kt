package cc.atomtech.planner.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cc.atomtech.planner.activities.EditorActivity
import cc.atomtech.planner.R
import cc.atomtech.planner.dataEntities.Reminder

//TODO)) Implement better buttons
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSheet(context: Context?, reminder: Reminder, onDismiss: () -> Unit) {
   ModalBottomSheet (
      onDismissRequest = onDismiss,
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
         onClick = { /*TODO*/ },
         modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
      ) {
         Icon(imageVector = Icons.Rounded.Share, contentDescription = null)
         Text(text = context?.getString(R.string.reminder_sheet_share) ?: "")
      }

      TextButton(
         onClick = { /*TODO*/ },
         modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
      ) {
         Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
         Text(text = context?.getString(R.string.reminder_sheet_delete) ?: "")
      }
   }
}