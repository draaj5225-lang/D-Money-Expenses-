package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.ExpenseRepository
import com.example.data.UserPreferences
import com.example.localization.AppStrings
import com.example.model.Expense
import com.example.ui.screens.AddEditExpenseSheet
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.Emerald700
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ExpenseViewModel

enum class MainTab {
  DASHBOARD,
  ANALYTICS,
  CATEGORIES,
  PROFILE
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        KharchaAppRoot()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KharchaAppRoot() {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  // Initialize DB, Prefs, Repo, and ViewModel
  val database = remember { AppDatabase.getDatabase(context, scope) }
  val preferences = remember { UserPreferences(context) }
  val repository = remember { ExpenseRepository(database.expenseDao()) }
  val viewModel: ExpenseViewModel = viewModel {
    ExpenseViewModel(repository, preferences)
  }

  // App navigation & presentation state
  val language by viewModel.language.collectAsState()
  val isHindi = language == "hi"

  val isAuthenticated by viewModel.isAuthenticated.collectAsState()
  val isOnboardingDone by viewModel.isOnboardingDone.collectAsState()
  val categories by viewModel.allCategories.collectAsState()

  var showSplash by remember { mutableStateOf(true) }
  var currentTab by remember { mutableStateOf(MainTab.DASHBOARD) }

  // Add / Edit expense sheet state
  var showAddExpenseSheet by remember { mutableStateOf(false) }
  var expenseToEdit by remember { mutableStateOf<Expense?>(null) }

  // BackHandler for custom tab navigation
  if (currentTab != MainTab.DASHBOARD) {
    BackHandler {
      currentTab = MainTab.DASHBOARD
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    when {
      showSplash -> {
        SplashScreen(
          isHindi = isHindi,
          onFinish = { showSplash = false }
        )
      }

      !isOnboardingDone -> {
        OnboardingScreen(
          isHindi = isHindi,
          onComplete = {
            viewModel.completeOnboarding()
          }
        )
      }

      !isAuthenticated -> {
        AuthScreen(
          isHindi = isHindi,
          onLoginSuccess = { name, phone ->
            viewModel.loginUser(name, phone)
          },
          onLanguageToggle = {
            viewModel.toggleLanguage()
          }
        )
      }

      else -> {
        // Main Authenticated Experience
        Scaffold(
          contentWindowInsets = WindowInsets.safeDrawing,
          topBar = {
            CenterAlignedTopAppBar(
              title = {
                Text(
                  text = when (currentTab) {
                    MainTab.DASHBOARD -> AppStrings.get("app_name", isHindi)
                    MainTab.ANALYTICS -> AppStrings.get("nav_analytics", isHindi)
                    MainTab.CATEGORIES -> AppStrings.get("nav_categories", isHindi)
                    MainTab.PROFILE -> AppStrings.get("nav_profile", isHindi)
                  },
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                )
              },
              actions = {
                // Persistent quick Language Switcher chip
                FilterChip(
                  selected = true,
                  onClick = { viewModel.toggleLanguage() },
                  label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Switch language",
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = if (isHindi) "हिंदी" else "EN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                      )
                    }
                  },
                  shape = RoundedCornerShape(20.dp),
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                  ),
                  modifier = Modifier
                    .padding(end = 12.dp)
                    .testTag("top_bar_language_toggle")
                )
              },
              colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
              )
            )
          },
          bottomBar = {
            NavigationBar(
              containerColor = MaterialTheme.colorScheme.surface,
              tonalElevation = 8.dp,
              modifier = Modifier.testTag("bottom_nav_bar")
            ) {
              // 1. Dashboard Tab
              NavigationBarItem(
                selected = currentTab == MainTab.DASHBOARD,
                onClick = { currentTab = MainTab.DASHBOARD },
                icon = {
                  Icon(
                    imageVector = if (currentTab == MainTab.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                    contentDescription = AppStrings.get("nav_dashboard", isHindi)
                  )
                },
                label = { Text(AppStrings.get("nav_dashboard", isHindi)) },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedIconColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("nav_tab_dashboard")
              )

              // 2. Analytics Tab
              NavigationBarItem(
                selected = currentTab == MainTab.ANALYTICS,
                onClick = { currentTab = MainTab.ANALYTICS },
                icon = {
                  Icon(
                    imageVector = if (currentTab == MainTab.ANALYTICS) Icons.Filled.PieChart else Icons.Outlined.PieChart,
                    contentDescription = AppStrings.get("nav_analytics", isHindi)
                  )
                },
                label = { Text(AppStrings.get("nav_analytics", isHindi)) },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedIconColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("nav_tab_analytics")
              )

              // 3. Categories Tab
              NavigationBarItem(
                selected = currentTab == MainTab.CATEGORIES,
                onClick = { currentTab = MainTab.CATEGORIES },
                icon = {
                  Icon(
                    imageVector = if (currentTab == MainTab.CATEGORIES) Icons.Filled.Category else Icons.Outlined.Category,
                    contentDescription = AppStrings.get("nav_categories", isHindi)
                  )
                },
                label = { Text(AppStrings.get("nav_categories", isHindi)) },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedIconColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("nav_tab_categories")
              )

              // 4. Profile Tab
              NavigationBarItem(
                selected = currentTab == MainTab.PROFILE,
                onClick = { currentTab = MainTab.PROFILE },
                icon = {
                  Icon(
                    imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = AppStrings.get("nav_profile", isHindi)
                  )
                },
                label = { Text(AppStrings.get("nav_profile", isHindi)) },
                colors = NavigationBarItemDefaults.colors(
                  indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedIconColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("nav_tab_profile")
              )
            }
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (currentTab) {
              MainTab.DASHBOARD -> {
                DashboardScreen(
                  viewModel = viewModel,
                  onAddExpenseClick = {
                    expenseToEdit = null
                    showAddExpenseSheet = true
                  },
                  onEditExpense = { exp ->
                    expenseToEdit = exp
                    showAddExpenseSheet = true
                  },
                  onNavigateToAnalytics = {
                    currentTab = MainTab.ANALYTICS
                  }
                )
              }

              MainTab.ANALYTICS -> {
                AnalyticsScreen(
                  viewModel = viewModel
                )
              }

              MainTab.CATEGORIES -> {
                CategoriesScreen(
                  viewModel = viewModel
                )
              }

              MainTab.PROFILE -> {
                ProfileScreen(
                  viewModel = viewModel,
                  onLogout = {
                    currentTab = MainTab.DASHBOARD
                  }
                )
              }
            }
          }
        }
      }
    }

    // Modal Add / Edit Expense Sheet
    if (showAddExpenseSheet) {
      AddEditExpenseSheet(
        initialExpense = expenseToEdit,
        categories = categories,
        isHindi = isHindi,
        onDismiss = {
          showAddExpenseSheet = false
          expenseToEdit = null
        },
        onSave = { amount, category, dateMillis, notes, mode ->
          if (expenseToEdit != null) {
            viewModel.updateExpense(
              id = expenseToEdit!!.id,
              amount = amount,
              category = category,
              dateMillis = dateMillis,
              notes = notes,
              paymentMode = mode
            )
          } else {
            viewModel.addExpense(
              amount = amount,
              category = category,
              dateMillis = dateMillis,
              notes = notes,
              paymentMode = mode
            )
          }
          showAddExpenseSheet = false
          expenseToEdit = null
        },
        onDelete = expenseToEdit?.let { exp ->
          {
            viewModel.deleteExpense(exp)
            showAddExpenseSheet = false
            expenseToEdit = null
          }
        }
      )
    }
  }
}
