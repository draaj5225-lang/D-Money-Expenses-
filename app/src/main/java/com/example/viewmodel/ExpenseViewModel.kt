package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ExpenseRepository
import com.example.data.UserPreferences
import com.example.model.AnalyticsTimeFilter
import com.example.model.Category
import com.example.model.CategorySummary
import com.example.model.DashboardFilter
import com.example.model.Expense
import com.example.model.Quarter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class AnalyticsFilterParams(
  val timeFilter: AnalyticsTimeFilter = AnalyticsTimeFilter.MONTH_WISE,
  val year: Int = Calendar.getInstance().get(Calendar.YEAR),
  val month: Int = Calendar.getInstance().get(Calendar.MONTH), // 0..11
  val quarter: Quarter = Quarter.Q4,
  val fromDate: Long = 0L,
  val toDate: Long = System.currentTimeMillis(),
  val categoryId: String? = null
)

class ExpenseViewModel(
  private val repository: ExpenseRepository,
  private val preferences: UserPreferences
) : ViewModel() {

  // User & Localization state
  val language: StateFlow<String> = preferences.language
  val isAuthenticated: StateFlow<Boolean> = preferences.isAuthenticated
  val userName: StateFlow<String> = preferences.userName
  val userPhone: StateFlow<String> = preferences.userPhone
  val isOnboardingDone: StateFlow<Boolean> = preferences.isOnboardingDone

  // Raw DB streams
  val allCategories: StateFlow<List<Category>> = repository.allCategories
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Category.DEFAULT_CATEGORIES)

  val allExpenses: StateFlow<List<Expense>> = repository.allExpenses
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dashboard state
  private val _dashboardFilter = MutableStateFlow(DashboardFilter.THIS_MONTH)
  val dashboardFilter: StateFlow<DashboardFilter> = _dashboardFilter.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  // Analytics params
  private val _analyticsParams = MutableStateFlow(
    AnalyticsFilterParams(
      fromDate = getStartOfMonthMillis(),
      toDate = System.currentTimeMillis()
    )
  )
  val analyticsParams: StateFlow<AnalyticsFilterParams> = _analyticsParams.asStateFlow()

  // Computed Dashboard Expenses
  val dashboardExpenses: StateFlow<List<Expense>> = combine(
    allExpenses,
    _dashboardFilter,
    _searchQuery
  ) { list: List<Expense>, filter: DashboardFilter, query: String ->
    val timeFiltered = filterExpensesByDashboardTime(list, filter)
    if (query.isBlank()) {
      timeFiltered
    } else {
      timeFiltered.filter {
        it.notes.contains(query, ignoreCase = true) ||
        it.categoryNameEn.contains(query, ignoreCase = true) ||
        it.categoryNameHi.contains(query, ignoreCase = true) ||
        it.paymentMode.contains(query, ignoreCase = true)
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Computed Dashboard Category Summaries
  val dashboardCategorySummaries: StateFlow<List<CategorySummary>> = combine(
    dashboardExpenses,
    allCategories
  ) { expenses: List<Expense>, categories: List<Category> ->
    calculateSummaries(expenses, categories)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Computed Analytics Expenses
  val analyticsExpenses: StateFlow<List<Expense>> = combine(
    allExpenses,
    _analyticsParams
  ) { list: List<Expense>, params: AnalyticsFilterParams ->
    val filtered = when (params.timeFilter) {
      AnalyticsTimeFilter.MONTH_WISE -> {
        val calStart = Calendar.getInstance().apply {
          set(Calendar.YEAR, params.year)
          set(Calendar.MONTH, params.month)
          set(Calendar.DAY_OF_MONTH, 1)
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
        }
        val calEnd = Calendar.getInstance().apply {
          set(Calendar.YEAR, params.year)
          set(Calendar.MONTH, params.month)
          set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
          set(Calendar.HOUR_OF_DAY, 23)
          set(Calendar.MINUTE, 59)
          set(Calendar.SECOND, 59)
          set(Calendar.MILLISECOND, 999)
        }
        list.filter { it.dateMillis in calStart.timeInMillis..calEnd.timeInMillis }
      }
      AnalyticsTimeFilter.QUARTER_WISE -> {
        val calStart = Calendar.getInstance().apply {
          set(Calendar.YEAR, params.year)
          set(Calendar.MONTH, params.quarter.months.first() - 1)
          set(Calendar.DAY_OF_MONTH, 1)
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
        }
        val calEnd = Calendar.getInstance().apply {
          set(Calendar.YEAR, params.year)
          set(Calendar.MONTH, params.quarter.months.last() - 1)
          set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
          set(Calendar.HOUR_OF_DAY, 23)
          set(Calendar.MINUTE, 59)
          set(Calendar.SECOND, 59)
        }
        list.filter { it.dateMillis in calStart.timeInMillis..calEnd.timeInMillis }
      }
      AnalyticsTimeFilter.YEAR_WISE -> {
        val calStart = Calendar.getInstance().apply {
          set(Calendar.YEAR, params.year)
          set(Calendar.MONTH, Calendar.JANUARY)
          set(Calendar.DAY_OF_MONTH, 1)
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
        }
        val calEnd = Calendar.getInstance().apply {
          set(Calendar.YEAR, params.year)
          set(Calendar.MONTH, Calendar.DECEMBER)
          set(Calendar.DAY_OF_MONTH, 31)
          set(Calendar.HOUR_OF_DAY, 23)
          set(Calendar.MINUTE, 59)
          set(Calendar.SECOND, 59)
        }
        list.filter { it.dateMillis in calStart.timeInMillis..calEnd.timeInMillis }
      }
      AnalyticsTimeFilter.CUSTOM_RANGE -> {
        list.filter { it.dateMillis in params.fromDate..params.toDate }
      }
    }

    if (params.categoryId != null) {
      filtered.filter { it.categoryId == params.categoryId }
    } else {
      filtered
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val analyticsCategorySummaries: StateFlow<List<CategorySummary>> = combine(
    analyticsExpenses,
    allCategories
  ) { expenses: List<Expense>, categories: List<Category> ->
    calculateSummaries(expenses, categories)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dashboard helper methods
  fun setDashboardFilter(filter: DashboardFilter) {
    _dashboardFilter.value = filter
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  // Analytics helper methods
  fun setAnalyticsFilter(filter: AnalyticsTimeFilter) {
    _analyticsParams.value = _analyticsParams.value.copy(timeFilter = filter)
  }

  fun setSelectedYear(year: Int) {
    _analyticsParams.value = _analyticsParams.value.copy(year = year)
  }

  fun setSelectedMonth(month: Int) {
    _analyticsParams.value = _analyticsParams.value.copy(month = month)
  }

  fun setSelectedQuarter(quarter: Quarter) {
    _analyticsParams.value = _analyticsParams.value.copy(quarter = quarter)
  }

  fun setCustomDateRange(from: Long, to: Long) {
    _analyticsParams.value = _analyticsParams.value.copy(fromDate = from, toDate = to)
  }

  fun setCategoryFilter(catId: String?) {
    _analyticsParams.value = _analyticsParams.value.copy(categoryId = catId)
  }

  // Preferences / Language / Auth
  fun toggleLanguage() {
    preferences.toggleLanguage()
  }

  fun setLanguage(lang: String) {
    preferences.setLanguage(lang)
  }

  fun completeOnboarding() {
    preferences.setOnboardingDone(true)
  }

  fun loginUser(name: String, phone: String) {
    preferences.setAuthenticated(name, phone)
  }

  fun logout() {
    preferences.logout()
  }

  fun resetAllData() {
    viewModelScope.launch {
      repository.resetAllData()
      preferences.resetAll()
    }
  }

  // Expense CRUD
  fun addExpense(
    amount: Double,
    category: Category,
    dateMillis: Long,
    notes: String,
    paymentMode: String
  ) {
    viewModelScope.launch {
      val (dayOfWeek, monthYear) = computeDateAttributes(dateMillis)
      val newExpense = Expense(
        amount = amount,
        categoryId = category.id,
        categoryNameEn = category.nameEn,
        categoryNameHi = category.nameHi,
        iconName = category.iconName,
        dateMillis = dateMillis,
        dayOfWeek = dayOfWeek,
        monthYear = monthYear,
        notes = notes.trim(),
        paymentMode = paymentMode
      )
      repository.insertExpense(newExpense)
    }
  }

  fun updateExpense(
    id: Long,
    amount: Double,
    category: Category,
    dateMillis: Long,
    notes: String,
    paymentMode: String
  ) {
    viewModelScope.launch {
      val (dayOfWeek, monthYear) = computeDateAttributes(dateMillis)
      val updated = Expense(
        id = id,
        amount = amount,
        categoryId = category.id,
        categoryNameEn = category.nameEn,
        categoryNameHi = category.nameHi,
        iconName = category.iconName,
        dateMillis = dateMillis,
        dayOfWeek = dayOfWeek,
        monthYear = monthYear,
        notes = notes.trim(),
        paymentMode = paymentMode
      )
      repository.updateExpense(updated)
    }
  }

  fun deleteExpense(expense: Expense) {
    viewModelScope.launch {
      repository.deleteExpense(expense)
    }
  }

  // Categories CRUD
  fun addCustomCategory(
    nameEn: String,
    nameHi: String,
    iconName: String,
    colorHex: Long
  ) {
    viewModelScope.launch {
      val id = "custom_" + System.currentTimeMillis()
      val cat = Category(
        id = id,
        nameEn = nameEn.trim(),
        nameHi = if (nameHi.isNotBlank()) nameHi.trim() else nameEn.trim(),
        iconName = iconName,
        colorHex = colorHex,
        isCustom = true
      )
      repository.insertCategory(cat)
    }
  }

  fun deleteCategory(id: String) {
    viewModelScope.launch {
      repository.deleteCustomCategory(id)
    }
  }

  // Internal helpers
  private fun calculateSummaries(expenses: List<Expense>, categories: List<Category>): List<CategorySummary> {
    val totalAll = expenses.sumOf { it.amount }
    val grouped = expenses.groupBy { it.categoryId }

    val summaries = categories.map { cat ->
      val list = grouped[cat.id] ?: emptyList()
      val sum = list.sumOf { it.amount }
      val pct = if (totalAll > 0) (sum / totalAll).toFloat() else 0f
      CategorySummary(
        category = cat,
        totalAmount = sum,
        count = list.size,
        percentageOfTotal = pct
      )
    }

    return summaries.sortedByDescending { it.totalAmount }
  }

  private fun filterExpensesByDashboardTime(list: List<Expense>, filter: DashboardFilter): List<Expense> {
    return when (filter) {
      DashboardFilter.TODAY -> {
        val startOfDay = Calendar.getInstance().apply {
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        list.filter { it.dateMillis >= startOfDay }
      }
      DashboardFilter.THIS_WEEK -> {
        val startOfWeek = Calendar.getInstance().apply {
          set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        list.filter { it.dateMillis >= startOfWeek }
      }
      DashboardFilter.THIS_MONTH -> {
        val startOfMonth = Calendar.getInstance().apply {
          set(Calendar.DAY_OF_MONTH, 1)
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        list.filter { it.dateMillis >= startOfMonth }
      }
      DashboardFilter.ALL_TIME -> list
    }
  }

  private fun computeDateAttributes(millis: Long): Pair<String, String> {
    val date = Date(millis)
    val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    return Pair(dayFormat.format(date), monthFormat.format(date))
  }

  private fun getStartOfMonthMillis(): Long {
    return Calendar.getInstance().apply {
      set(Calendar.DAY_OF_MONTH, 1)
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }.timeInMillis
  }
}
