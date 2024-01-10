package cc.atomtech.planner

import androidx.compose.ui.graphics.vector.ImageVector

data class ButtonData (
   val label: String,
   val icon: ImageVector?,
   val onClick: () -> Unit
)