package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.Category
import com.example.model.CategorySummary
import com.example.model.DashboardFilter
import com.example.model.Expense
import com.example.ui.components.CategorySubtotalCard
import com.example.ui.components.ExpenseItemCard
import com.example.ui.theme.Amber400
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Teal700
import com.example.viewmodel.ExpenseViewModel
import java.util.Locale

@Composable
fun DashboardScreen(
  viewModel: ExpenseViewModel,
  onAddExpenseClick: () -> Unit,
  onEditExpense: (Expense) -> Unit,
  onNavigateToAnalytics: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isHindi = viewModel.language.collectAsState().value == "hi"
  val currentFilter by viewModel.dashboardFilter.collectAsState()
  val expenses by viewModel.dashboardExpenses.collectAsState()
  val summaries by viewModel.dashboardCategorySummaries.collectAsState()
  val categories by viewModel.allCategories.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val userName by viewModel.userName.collectAsState()

  val totalAmount = expenses.sumOf { it.amount }
  val totalCount = expenses.size
  val dailyAvg = if (totalCount > 0) totalAmount / (when (currentFilter) {
    DashboardFilter.TODAY -> 1
    DashboardFilter.THIS_WEEK -> 7
    DashboardFilter.THIS_MONTH -> 30
    DashboardFilter.ALL_TIME -> 30
  }) else 0.0

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("dashboard_screen"),
      contentPadding = PaddingValues(bottom = 100.dp)
    ) {
      // 1. Hero Card: Cumulative Total Expenses & Quick Stats
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("dashboard_hero_card")
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.linearGradient(
                  colors = listOf(Emerald900, Emerald700, Teal700)
                )
              )
              .padding(20.dp)
          ) {
            Column(modifier = Modifier.fillMaxWidth()) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "${AppStrings.get("welcome_back", isHindi)}, $userName",
                    style = MaterialTheme.typography.bodyMedium.copy(
                      color = Color.White.copy(alpha = 0.85f)
                    )
                  )
                  Text(
                    text = AppStrings.get("total_expenses", isHindi),
                    style = MaterialTheme.typography.titleMedium.copy(
                      color = Color.White.copy(alpha = 0.95f),
                      fontWeight = FontWeight.SemiBold
                    )
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = Amber400,
                    modifier = Modifier.size(24.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Big Rupee Cumulative Total
              Text(
                text = "₹${String.format(Locale.getDefault(), "%,.0f", totalAmount)}",
                style = MaterialTheme.typography.displaySmall.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = Color.White,
                  letterSpacing = 0.5.sp
                )
              )

              Spacer(modifier = Modifier.height(18.dp))

              // Sub-metrics (Daily Average & Total Entries)
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color.Black.copy(alpha = 0.18f))
                  .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(
                    text = AppStrings.get("daily_average", isHindi),
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color.White.copy(alpha = 0.75f),
                      fontSize = 11.sp
                    )
                  )
                  Text(
                    text = "₹${String.format(Locale.getDefault(), "%,.0f", dailyAvg)}/day",
                    style = MaterialTheme.typography.titleSmall.copy(
                      color = Amber400,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = AppStrings.get("total_transactions", isHindi),
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color.White.copy(alpha = 0.75f),
                      fontSize = 11.sp
                    )
                  )
                  Text(
                    text = "$totalCount entries",
                    style = MaterialTheme.typography.titleSmall.copy(
                      color = Color.White,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }
              }
            }
          }
        }
      }

      // 2. Dashboard Time Filter Chips (Today, This Week, This Month, All Time)
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val filterList = listOf(
            DashboardFilter.TODAY to "filter_today",
            DashboardFilter.THIS_WEEK to "filter_week",
            DashboardFilter.THIS_MONTH to "filter_month",
            DashboardFilter.ALL_TIME to "filter_all"
          )

          filterList.forEach { (filterType, stringKey) ->
            val isSelected = currentFilter == filterType
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.setDashboardFilter(filterType) },
              label = {
                Text(
                  text = AppStrings.get(stringKey, isHindi),
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              shape = RoundedCornerShape(20.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
              modifier = Modifier.testTag("dashboard_filter_${filterType.name}")
            )
          }
        }
      }

      // 3. Category Sub-totals Section (Dedicated cards for each category)
      item {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = AppStrings.get("category_subtotals", isHindi),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold
            )
          )

          TextButton(
            onClick = onNavigateToAnalytics,
            modifier = Modifier.testTag("dashboard_view_analytics_btn")
          ) {
            Text(AppStrings.get("nav_analytics", isHindi), fontSize = 13.sp)
            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal scrolling category subtotal cards
        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(summaries, key = { it.category.id }) { summary ->
            CategorySubtotalCard(
              summary = summary,
              isHindi = isHindi,
              onClick = onNavigateToAnalytics
            )
          }
        }
      }

      // 4. Search Bar
      item {
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          placeholder = { Text(if (isHindi) "खर्चा, श्रेणी या माध्यम खोजें..." else "Search expenses, notes, UPI...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { viewModel.setSearchQuery("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_search_input")
        )
      }

      // 5. Recent Transactions Header
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = AppStrings.get("recent_transactions", isHindi),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold
            )
          )
          Text(
            text = "${expenses.size} ${AppStrings.get("total_transactions", isHindi).lowercase()}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Empty state
      if (expenses.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Outlined.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = AppStrings.get("no_expenses_title", isHindi),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = AppStrings.get("no_expenses_desc", isHindi),
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 13.sp
                )
              )
            }
          }
        }
      } else {
        items(expenses, key = { it.id }) { expense ->
          ExpenseItemCard(
            expense = expense,
            categories = categories,
            isHindi = isHindi,
            onEdit = { onEditExpense(expense) },
            onDelete = { viewModel.deleteExpense(expense) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
          )
        }
      }
    }

    // Prominent Floating Action Button (+ Add Expense)
    FloatingActionButton(
      onClick = onAddExpenseClick,
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = Color.White,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 24.dp, end = 20.dp)
        .testTag("dashboard_add_expense_fab")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Expense")
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = AppStrings.get("btn_add_expense", isHindi),
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
      }
    }
  }
}
