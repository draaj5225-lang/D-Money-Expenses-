package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.AnalyticsTimeFilter
import com.example.model.Category
import com.example.model.CategorySummary
import com.example.model.Quarter
import com.example.ui.components.BarChartItem
import com.example.ui.components.CategoryIconBadge
import com.example.ui.components.DonutChart
import com.example.ui.components.ExpenseBarChart
import com.example.ui.theme.Amber500
import com.example.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
  viewModel: ExpenseViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isHindi = viewModel.language.collectAsState().value == "hi"

  val params by viewModel.analyticsParams.collectAsState()
  val expenses by viewModel.analyticsExpenses.collectAsState()
  val summaries by viewModel.analyticsCategorySummaries.collectAsState()
  val allCategories by viewModel.allCategories.collectAsState()

  val totalAmount = expenses.sumOf { it.amount }

  val monthNamesEn = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
  val monthNamesHi = listOf("जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून", "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर")

  val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("analytics_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // 1. Time Filter Tabs (Month-wise, Quarter-wise, Year-wise, Custom Range)
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = AppStrings.get("time_filter", isHindi),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Filter Segmented Row
          val tabs = listOf(
            AnalyticsTimeFilter.MONTH_WISE to "tab_month",
            AnalyticsTimeFilter.QUARTER_WISE to "tab_quarter",
            AnalyticsTimeFilter.YEAR_WISE to "tab_year",
            AnalyticsTimeFilter.CUSTOM_RANGE to "tab_custom"
          )

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            tabs.forEach { (filterOption, labelKey) ->
              val isSelected = params.timeFilter == filterOption
              FilterChip(
                selected = isSelected,
                onClick = { viewModel.setAnalyticsFilter(filterOption) },
                label = { Text(AppStrings.get(labelKey, isHindi)) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primary,
                  selectedLabelColor = Color.White
                ),
                modifier = Modifier.testTag("analytics_filter_${filterOption.name}")
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Dynamic secondary selectors based on tab
          when (params.timeFilter) {
            AnalyticsTimeFilter.MONTH_WISE -> {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                for (m in 0..11) {
                  val isSelected = params.month == m
                  val mLabel = if (isHindi) monthNamesHi[m] else monthNamesEn[m]
                  FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSelectedMonth(m) },
                    label = { Text(mLabel, fontSize = 12.sp) },
                    shape = RoundedCornerShape(16.dp)
                  )
                }
              }
            }

            AnalyticsTimeFilter.QUARTER_WISE -> {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Quarter.values().forEach { q ->
                  val isSelected = params.quarter == q
                  val qLabel = when (q) {
                    Quarter.Q1 -> AppStrings.get("q1", isHindi)
                    Quarter.Q2 -> AppStrings.get("q2", isHindi)
                    Quarter.Q3 -> AppStrings.get("q3", isHindi)
                    Quarter.Q4 -> AppStrings.get("q4", isHindi)
                  }
                  FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSelectedQuarter(q) },
                    label = { Text(qLabel.take(6), fontSize = 12.sp) },
                    modifier = Modifier.weight(1f)
                  )
                }
              }
            }

            AnalyticsTimeFilter.YEAR_WISE -> {
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                listOf(currentYear, currentYear - 1, currentYear - 2).forEach { yr ->
                  FilterChip(
                    selected = params.year == yr,
                    onClick = { viewModel.setSelectedYear(yr) },
                    label = { Text(yr.toString()) }
                  )
                }
              }
            }

            AnalyticsTimeFilter.CUSTOM_RANGE -> {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedButton(
                  onClick = {
                    val cal = Calendar.getInstance().apply { timeInMillis = params.fromDate }
                    DatePickerDialog(
                      context,
                      { _, y, m, d ->
                        val newCal = Calendar.getInstance().apply {
                          set(Calendar.YEAR, y)
                          set(Calendar.MONTH, m)
                          set(Calendar.DAY_OF_MONTH, d)
                          set(Calendar.HOUR_OF_DAY, 0)
                          set(Calendar.MINUTE, 0)
                          set(Calendar.SECOND, 0)
                        }
                        viewModel.setCustomDateRange(newCal.timeInMillis, params.toDate)
                      },
                      cal.get(Calendar.YEAR),
                      cal.get(Calendar.MONTH),
                      cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                  },
                  modifier = Modifier
                    .weight(1f)
                    .testTag("analytics_from_date_btn")
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(AppStrings.get("from_date", isHindi), fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    Text(dateFormat.format(Date(params.fromDate)), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }

                OutlinedButton(
                  onClick = {
                    val cal = Calendar.getInstance().apply { timeInMillis = params.toDate }
                    DatePickerDialog(
                      context,
                      { _, y, m, d ->
                        val newCal = Calendar.getInstance().apply {
                          set(Calendar.YEAR, y)
                          set(Calendar.MONTH, m)
                          set(Calendar.DAY_OF_MONTH, d)
                          set(Calendar.HOUR_OF_DAY, 23)
                          set(Calendar.MINUTE, 59)
                          set(Calendar.SECOND, 59)
                        }
                        viewModel.setCustomDateRange(params.fromDate, newCal.timeInMillis)
                      },
                      cal.get(Calendar.YEAR),
                      cal.get(Calendar.MONTH),
                      cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                  },
                  modifier = Modifier
                    .weight(1f)
                    .testTag("analytics_to_date_btn")
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(AppStrings.get("to_date", isHindi), fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    Text(dateFormat.format(Date(params.toDate)), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }
    }

    // 2. Category Filter Chips (All vs specific category)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = params.categoryId == null,
          onClick = { viewModel.setCategoryFilter(null) },
          label = { Text(if (isHindi) "सभी श्रेणियां" else "All Categories") },
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier.testTag("analytics_cat_all")
        )

        allCategories.forEach { cat ->
          val isSelected = params.categoryId == cat.id
          FilterChip(
            selected = isSelected,
            onClick = {
              viewModel.setCategoryFilter(if (isSelected) null else cat.id)
            },
            label = { Text(cat.localizedName(isHindi)) },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("analytics_cat_${cat.id}")
          )
        }
      }
    }

    // 3. Interactive Donut Chart Card
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = AppStrings.get("chart_category_share", isHindi),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
              text = "₹${String.format(Locale.getDefault(), "%,.0f", totalAmount)}",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          DonutChart(
            data = summaries.filter { it.totalAmount > 0 },
            totalAmount = totalAmount,
            isHindi = isHindi
          )
        }
      }
    }

    // 4. Bar Chart: Spending Distribution
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = AppStrings.get("chart_spending_distribution", isHindi),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(16.dp))

          val barItems = summaries.map {
            BarChartItem(
              label = it.category.localizedName(isHindi).take(6),
              amount = it.totalAmount
            )
          }

          ExpenseBarChart(items = barItems)
        }
      }
    }

    // 5. Category Breakdown Detailed List
    item {
      Spacer(modifier = Modifier.height(20.dp))
      Text(
        text = AppStrings.get("category_subtotals", isHindi),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))
    }

    if (summaries.all { it.totalAmount == 0.0 }) {
      item {
        Text(
          text = AppStrings.get("no_analytics_data", isHindi),
          style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          ),
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
      }
    } else {
      items(summaries.filter { it.totalAmount > 0 }, key = { it.category.id }) { summary ->
        val cat = summary.category
        val catColor = Color(cat.colorHex)

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryIconBadge(
                  iconName = cat.iconName,
                  colorHex = cat.colorHex,
                  size = 40.dp,
                  iconSize = 22.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = cat.localizedName(isHindi),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                  )
                  Text(
                    text = "${summary.count} ${AppStrings.get("total_transactions", isHindi).lowercase()}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.outline,
                      fontSize = 11.sp
                    )
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "₹${String.format(Locale.getDefault(), "%,.0f", summary.totalAmount)}",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                )
                Text(
                  text = "${(summary.percentageOfTotal * 100).toInt()}% ${AppStrings.get("percentage_of_total", isHindi)}",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = catColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
              progress = { summary.percentageOfTotal.coerceIn(0.02f, 1f) },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
              color = catColor,
              trackColor = catColor.copy(alpha = 0.15f),
              strokeCap = StrokeCap.Round
            )
          }
        }
      }
    }
  }
}
