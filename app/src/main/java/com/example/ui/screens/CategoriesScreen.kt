package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.viewmodel.ExpenseViewModel

@Composable
fun CategoriesScreen(
  viewModel: ExpenseViewModel,
  modifier: Modifier = Modifier
) {
  val isHindi = viewModel.language.collectAsState().value == "hi"
  val categories by viewModel.allCategories.collectAsState()
  var showAddDialog by remember { mutableStateOf(false) }

  val defaultCats = categories.filter { !it.isCustom }
  val customCats = categories.filter { it.isCustom }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("categories_screen"),
      contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp)
    ) {
      // Header & Add Category Action
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = AppStrings.get("nav_categories", isHindi),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = if (isHindi) "अपने खर्चों के लिए नई श्रेणियां और प्रतीक जोड़ें" else "Organize spending with custom symbols & tags",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }

            Button(
              onClick = { showAddDialog = true },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              modifier = Modifier.testTag("btn_open_add_category")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(AppStrings.get("btn_add_expense", isHindi).take(6), fontSize = 12.sp)
            }
          }
        }
      }

      // Predefined Categories Section
      item {
        Text(
          text = if (isHindi) "मानक श्रेणियां (Predefined)" else "Predefined Categories",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
      }

      items(defaultCats, key = { it.id }) { cat ->
        CategoryRowItem(
          category = cat,
          isHindi = isHindi,
          canDelete = false,
          onDelete = {}
        )
      }

      // Custom Categories Section
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = if (isHindi) "कस्टम श्रेणियां (Custom)" else "Custom Categories",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
      }

      if (customCats.isEmpty()) {
        item {
          Text(
            text = if (isHindi) "कोई कस्टम श्रेणी नहीं जोड़ी गई है। नई श्रेणी जोड़ने के लिए ऊपर दिए बटन का उपयोग करें।"
            else "No custom categories added yet. Tap the button above to create one!",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }
      } else {
        items(customCats, key = { it.id }) { cat ->
          CategoryRowItem(
            category = cat,
            isHindi = isHindi,
            canDelete = true,
            onDelete = { viewModel.deleteCategory(cat.id) }
          )
        }
      }
    }
  }

  if (showAddDialog) {
    AddCategoryDialog(
      isHindi = isHindi,
      onDismiss = { showAddDialog = false },
      onAdd = { en, hi, icon, color ->
        viewModel.addCustomCategory(en, hi, icon, color)
      }
    )
  }
}

@Composable
private fun CategoryRowItem(
  category: Category,
  isHindi: Boolean,
  canDelete: Boolean,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        CategoryIconBadge(
          iconName = category.iconName,
          colorHex = category.colorHex,
          size = 42.dp,
          iconSize = 22.dp
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(
            text = category.localizedName(isHindi),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = if (isHindi) category.nameEn else category.nameHi,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          )
        }
      }

      if (canDelete) {
        IconButton(onClick = onDelete) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
          )
        }
      } else {
        Box(
          modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = "Default",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
