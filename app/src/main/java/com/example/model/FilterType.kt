package com.example.model

/**
 * Filter intervals for advanced analytics and dashboard.
 */
enum class DashboardFilter {
  TODAY,
  THIS_WEEK,
  THIS_MONTH,
  ALL_TIME
}

enum class AnalyticsTimeFilter {
  MONTH_WISE,
  QUARTER_WISE,
  YEAR_WISE,
  CUSTOM_RANGE
}

enum class Quarter(val qNumber: Int, val months: List<Int>) {
  Q1(1, listOf(1, 2, 3)),
  Q2(2, listOf(4, 5, 6)),
  Q3(3, listOf(7, 8, 9)),
  Q4(4, listOf(10, 11, 12))
}

data class CategorySummary(
  val category: Category,
  val totalAmount: Double,
  val count: Int,
  val percentageOfTotal: Float
)
