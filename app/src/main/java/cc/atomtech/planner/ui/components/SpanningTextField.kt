package cc.atomtech.planner.ui.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import cc.atomtech.planner.R

@Composable
fun SpanningTextField(value: MutableState<String>,
                      onValueChanged: (String) -> Unit,
                      singleLine: Boolean = false,
                      placeholder: String,
                      label: String,
                      leadingIcon: @Composable () -> Unit) {
   OutlinedTextField(
      value = value.value,
      onValueChange = onValueChanged,
      placeholder = { Text(text = placeholder) },
      label = { Text(text = label) },
      leadingIcon = { leadingIcon },
      modifier = Modifier
         .fillMaxWidth(),
      singleLine = singleLine
   )
}