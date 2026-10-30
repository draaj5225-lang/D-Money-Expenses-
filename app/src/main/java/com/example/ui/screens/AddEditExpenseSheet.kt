package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.Category
import com.example.model.Expense
import com.example.ui.components.CategoryIconBadge
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditExpenseSheet(
  initialExpense: Expense?,
  categories: List<Category>,
  isHindi: Boolean,
  onDismiss: () -> Unit,
  onSave: (amount: Double, category: Category, dateMillis: Long, notes: String, mode: String) -> Unit,
  onDelete: (() -> Unit)? = null
) {
  val context = LocalContext.current
  val isEditing = initialExpense != null

  var amountText by remember {
    mutableStateOf(if (isEditing) String.format(Locale.getDefault(), "%.0f", initialExpense!!.amount) else "")
  }
  var selectedCategory by remember {
    mutableStateOf(
      categories.find { it.id == initialExpense?.categoryId } ?: categories.firstOrNull() ?: Category.DEFAULT_CATEGORIES.first()
    )
  }
  var dateMillis by remember {
    mutableLongStateOf(initialExpense?.dateMillis ?: System.currentTimeMillis())
  }
  var notes by remember {
    mutableStateOf(initialExpense?.notes ?: "")
  }
  var paymentMode by remember {
    mutableStateOf(initialExpense?.paymentMode ?: "UPI")
  }
  var showDeleteDialog by remember { mutableStateOf(false) }
  var validationError by remember { mutableStateOf<String?>(null) }

  // Date and Day auto-detection strings
  val calendar = remember(dateMillis) {
    Calendar.getInstance().apply { timeInMillis = dateMillis }
  }
  val dayFormat = remember { SimpleDateFormat("EEEE", Locale.ENGLISH) }
  val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH) }

  val formattedDay = dayFormat.format(calendar.time)
  val formattedDate = dateFormat.format(calendar.time)

  // Quick Amount chips
  val quickAmounts = listOf(50, 100, 200, 500, 1000, 2000)
  val paymentModes = listOf("UPI", "Cash", "Card", "NetBanking")

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("add_edit_expense_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .imePadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      // Header Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isEditing) AppStrings.get("title_edit_expense", isHindi) else AppStrings.get("title_add_expense", isHindi),
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (isEditing && onDelete != null) {
            IconButton(
              onClick = { showDeleteDialog = true },
              modifier = Modifier.testTag("delete_expense_button")
            ) {
              Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 1. Amount Field with ₹ symbol
      OutlinedTextField(
        value = amountText,
        onValueChange = { input ->
          if (input.all { it.isDigit() || it == '.' }) {
            amountText = input
            validationError = null
          }
        },
        label = { Text(AppStrings.get("amount_label", isHindi)) },
        placeholder = { Text("0") },
        leadingIcon = {
          Text(
            text = "₹",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(start = 12.dp)
          )
        },
        textStyle = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("expense_amount_input")
      )

      if (validationError != null) {
        Text(
          text = validationError ?: "",
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Quick Amount Chips
      Text(
        text = AppStrings.get("quick_amounts", isHindi),
        style = MaterialTheme.typography.labelSmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        quickAmounts.forEach { amt ->
          FilterChip(
            selected = amountText == amt.toString(),
            onClick = {
              amountText = amt.toString()
              validationError = null
            },
            label = { Text("+₹$amt") },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 2. Date & Day Auto-detection with Manual Override Option
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CalendarToday,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "$formattedDay, $formattedDate",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.SemiBold
                )
              )
              Text(
                text = AppStrings.get("auto_detected", isHindi),
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MaterialTheme.colorScheme.outline,
                  fontSize = 11.sp
                )
              )
            }
          }

          // Manual Date Picker Override Button
          OutlinedButton(
            onClick = {
              val c = Calendar.getInstance().apply { timeInMillis = dateMillis }
              DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                  val newCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                  }
                  dateMillis = newCal.timeInMillis
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
              ).show()
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("change_date_button")
          ) {
            Text(AppStrings.get("change_date", isHindi), fontSize = 12.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 3. Category Selection with Visual Symbols & Bilingual Names
      Text(
        text = AppStrings.get("select_category", isHindi),
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold
        )
      )
      Spacer(modifier = Modifier.height(8.dp))

      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categories.forEach { cat ->
          val isSelected = cat.id == selectedCategory.id
          val catColor = Color(cat.colorHex)

          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(
                if (isSelected) catColor.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant
              )
              .clickable { selectedCategory = cat }
              .padding(horizontal = 10.dp, vertical = 8.dp)
              .testTag("category_chip_${cat.id}"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CategoryIconBadge(
              iconName = cat.iconName,
              colorHex = cat.colorHex,
              size = 28.dp,
              iconSize = 16.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = cat.localizedName(isHindi),
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) catColor else MaterialTheme.colorScheme.onSurface
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 4. Custom Text / Notes Field (user can type anything they want for custom description)
      OutlinedTextField(
        value = notes,
        onValueChange = { notes = it },
        label = { Text(AppStrings.get("notes_label", isHindi)) },
        placeholder = { Text(AppStrings.get("notes_placeholder", isHindi)) },
        shape = RoundedCornerShape(12.dp),
        minLines = 2,
        maxLines = 4,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("expense_notes_input")
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 5. Payment Mode Selection
      Text(
        text = AppStrings.get("payment_mode", isHindi),
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold
        )
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        paymentModes.forEach { mode ->
          FilterChip(
            selected = paymentMode == mode,
            onClick = { paymentMode = mode },
            label = { Text(mode) },
            shape = RoundedCornerShape(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Submit Button
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull()
          if (amt == null || amt <= 0.0) {
            validationError = "Please enter a valid amount greater than 0"
          } else {
            onSave(amt, selectedCategory, dateMillis, notes, paymentMode)
            onDismiss()
          }
        },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("save_expense_button")
      ) {
        Text(
          text = if (isEditing) AppStrings.get("btn_update_expense", isHindi) else AppStrings.get("btn_save_expense", isHindi),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  // Confirm Delete Dialog
  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text(AppStrings.get("confirm_delete_title", isHindi)) },
      text = { Text(AppStrings.get("confirm_delete_msg", isHindi)) },
      confirmButton = {
        Button(
          onClick = {
            showDeleteDialog = false
            onDelete?.invoke()
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
          modifier = Modifier.testTag("confirm_delete_button")
        ) {
          Text(AppStrings.get("btn_delete", isHindi))
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text(AppStrings.get("cancel", isHindi))
        }
      }
    )
  }
}
