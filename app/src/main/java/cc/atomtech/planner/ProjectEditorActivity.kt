package cc.atomtech.planner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cc.atomtech.planner.dataEntities.ColorEntity
import cc.atomtech.planner.dataEntities.Project
import cc.atomtech.planner.ui.components.SpanningTextField
import cc.atomtech.planner.ui.theme.PlannerTheme
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProjectEditorActivity : ComponentActivity() {
   lateinit var isCreator: MutableState<Boolean>
   private lateinit var project: MutableState<Project>
   @OptIn(ExperimentalMaterial3Api::class)
   override fun onCreate(savedInstanceState: Bundle?) {
      val superIntent = intent
      var localProject: Project = Project()

      //if we are not creating a reminder, we're editing one, so let's read it from the DB
      if(!superIntent.getBooleanExtra("isCreator", true))
         GlobalScope.launch {
            localProject = DB.getProjectsDAO()
               ?.read(superIntent.getLongExtra("rowid", 0)) ?: Project()
         }

      super.onCreate(savedInstanceState)

      setContent {
         PlannerTheme {
            project = remember { mutableStateOf(localProject) }
            isCreator = remember { mutableStateOf(superIntent.getBooleanExtra("isCreator", true)) }
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

            Scaffold(
               modifier = Modifier.fillMaxSize(),
               topBar = {
                  CenterAlignedTopAppBar(
                     title = {
                        Text(
                           text = "${
                              if(isCreator.value) getString(R.string.activity_create)
                              else getString(R.string.activity_editor)
                           } ${getString(R.string.word_project)}"
                        )
                     },
                     scrollBehavior = scrollBehavior,
                     navigationIcon = {
                        IconButton(onClick = {
                           navigateUpTo(Intent(this@ProjectEditorActivity, MainActivity::class.java))
                        }) {
                           Icon(
                              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                              contentDescription = getString(R.string.btn_back_desc)
                           )
                        }
                     },
                     actions = {
                        if(!isCreator.value) {
                           IconButton(onClick = {
                              project.value.delete()
                              navigateUpTo(Intent(this@ProjectEditorActivity, MainActivity::class.java))
                           }) {
                              Icon(
                                 imageVector = Icons.Rounded.Delete,
                                 contentDescription = ""
                              )
                           }
                        }
                     }
                  )
               },
               floatingActionButton = {
                  ExtendedFloatingActionButton(onClick = {
                     if (isCreator.value)
                        project.value.store()
                     else
                        project.value.update()
                     Log.i("EDITOR", "User triggered store/update, returning")
                     navigateUpTo(Intent(this@ProjectEditorActivity, MainActivity::class.java))
                  },
                     icon = { Icon(
                        imageVector = if(isCreator.value) Icons.Rounded.Save else Icons.Rounded.Update,
                        contentDescription = getString(R.string.fab_save_label)
                     ) },
                     text = { Text(text = (if(isCreator.value) getString(R.string.fab_save_label) else getString(R.string.fab_update_label ))) }
                  )
               },
               content = {
                  EditorColumn(this@ProjectEditorActivity, it, project)
               }
            )
         }
      }
   }
}

@Composable
fun EditorColumn(context: Context?,
                 paddingValues: PaddingValues,
                 project: MutableState<Project>) {
   val title = remember { mutableStateOf(project.value.name) }
   val color = remember { mutableStateOf(project.value.color) }
   val colorEntity = remember { mutableStateOf(ColorEntity()) }
   colorEntity.value.buildByHex(color.value)
   val isDialogOpen = remember { mutableStateOf(false) }
   val colorControler = rememberColorPickerController()
   colorControler.setDebounceDuration(300L)
//   colorControler.setWheelColor()

   Column (
      modifier = Modifier
         .padding(paddingValues)
         .padding(horizontal = 12.dp)
         .fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
   ) {
      SpanningTextField(
         value = title,
         onValueChanged = {title.value = it; project.value.name = it},
         leadingIcon = { Icon(imageVector = Icons.Rounded.TextFields, contentDescription = null ) },
         singleLine = true,
         placeholder = context?.getString(R.string.project_title) ?: "",
         label = context?.getString(R.string.project_title) ?: ""
      )
      Row (
         modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
         horizontalArrangement = Arrangement.SpaceBetween,
         verticalAlignment = Alignment.CenterVertically
      ) {
         Text(text = context?.getString(R.string.word_color) ?: "Color")
         Icon(
            imageVector = Icons.Filled.Circle,
            contentDescription = "",
            tint = Color(colorEntity.value.red, colorEntity.value.green, colorEntity.value.blue)
         )
         IconButton(onClick = { isDialogOpen.value = true }) {
            Icon(imageVector = Icons.Rounded.Edit, contentDescription = context?.getString(R.string.word_edit))
         }
      }
      if(isDialogOpen.value)
         ColorDialog(
            controller = colorControler,
            onDismissRequest = { isDialogOpen.value = false },
            onConfirm = { hex ->
               color.value = hex
               colorEntity.value.buildByHex(hex)
               project.value.color = hex
               isDialogOpen.value = false
            }
         )
   }
}

@Composable
fun ColorDialog(controller: ColorPickerController, onDismissRequest: () -> Unit, onConfirm: (hex: String) -> Unit) {
   var color = "ffffff"

   Dialog(onDismissRequest = onDismissRequest) {
      Card (
         modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
         shape = RoundedCornerShape(16.dp)
      ) {
         HsvColorPicker(
            modifier = Modifier
               .fillMaxWidth()
               .padding(8.dp)
               .height(300.dp),
            controller = controller,
            onColorChanged = { color = it.hexCode.substring(2, 8) }
         )
         BrightnessSlider(
            modifier = Modifier.fillMaxWidth().padding(10.dp).height(35.dp),
            controller = controller
         )
         Row (
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
         ) {
            Button(onClick = onDismissRequest) {
               Text(text = "Cancel")
            }
            Button(onClick = { onConfirm(color) }) {
               Text(text = "Confirm")
            }
         }
      }
   }
}
