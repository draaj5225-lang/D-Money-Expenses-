package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.Category
import com.example.ui.components.CategoryIconBadge

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddCategoryDialog(
  isHindi: Boolean,
  onDismiss: () -> Unit,
  onAdd: (nameEn: String, nameHi: String, iconName: String, colorHex: Long) -> Unit
) {
  var nameEn by remember { mutableStateOf("") }
  var nameHi by remember { mutableStateOf("") }
  var selectedIcon by remember { mutableStateOf(Category.ICON_OPTIONS.first().first) }
  var selectedColor by remember { mutableLongStateOf(Category.COLOR_OPTIONS.first()) }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Text(
        text = AppStrings.get("add_custom_category", isHindi),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Preview badge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          CategoryIconBadge(
            iconName = selectedIcon,
            colorHex = selectedColor,
            size = 56.dp,
            iconSize = 30.dp
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // English Name Field
        OutlinedTextField(
          value = nameEn,
          onValueChange = {
            nameEn = it
            errorText = null
          },
          label = { Text(AppStrings.get("cat_name_en", isHindi)) },
          placeholder = { Text("e.g. Cinema / Movie") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("custom_cat_name_en")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Hindi Name Field
        OutlinedTextField(
          value = nameHi,
          onValueChange = { nameHi = it },
          label = { Text(AppStrings.get("cat_name_hi", isHindi)) },
          placeholder = { Text("उदा. सिनेमा / फिल्म") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("custom_cat_name_hi")
        )

        if (errorText != null) {
          Text(
            text = errorText ?: "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 6.dp, start = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Icon Picker
        Text(
          text = AppStrings.get("choose_icon", isHindi),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Category.ICON_OPTIONS.forEach { (iconKey, _) ->
            val isSelected = selectedIcon == iconKey
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                  if (isSelected) MaterialTheme.colorScheme.primaryContainer
                  else MaterialTheme.colorScheme.surfaceVariant
                )
                .border(
                  width = if (isSelected) 2.dp else 0.dp,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                  shape = CircleShape
                )
                .clickable { selectedIcon = iconKey },
              contentAlignment = Alignment.Center
            ) {
              CategoryIconBadge(
                iconName = iconKey,
                colorHex = if (isSelected) selectedColor else 0xFF64748BL,
                size = 36.dp,
                iconSize = 20.dp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Color Picker
        Text(
          text = AppStrings.get("choose_color", isHindi),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Category.COLOR_OPTIONS.forEach { hex ->
            val isSelected = selectedColor == hex
            val c = Color(hex)
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(c)
                .border(
                  width = if (isSelected) 3.dp else 1.dp,
                  color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.White,
                  shape = CircleShape
                )
                .clickable { selectedColor = hex }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (nameEn.isBlank()) {
            errorText = "Please enter category name"
          } else {
            onAdd(nameEn, nameHi, selectedIcon, selectedColor)
            onDismiss()
          }
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.testTag("btn_confirm_add_category")
      ) {
        Text(AppStrings.get("btn_create_category", isHindi))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(AppStrings.get("cancel", isHindi))
      }
    }
  )
}
