package com.example

import com.example.localization.AppStrings
import com.example.model.Category
import com.example.model.Expense
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testDefaultCategoriesPresence() {
    val defaultCats = Category.DEFAULT_CATEGORIES
    assertEquals(5, defaultCats.size)
    val ids = defaultCats.map { it.id }
    assertTrue(ids.contains("grocery"))
    assertTrue(ids.contains("milk"))
    assertTrue(ids.contains("petrol"))
    assertTrue(ids.contains("bills"))
    assertTrue(ids.contains("other"))
  }

  @Test
  fun testBilingualStringLookups() {
    // English
    val enTitle = AppStrings.get("app_name", false)
    assertEquals("Kharcha Tracker", enTitle)
    val enCatMilk = AppStrings.get("cat_milk", false)
    assertEquals("Milk", enCatMilk)

    // Hindi
    val hiTitle = AppStrings.get("app_name", true)
    assertEquals("खर्चा ट्रैकर", hiTitle)
    val hiCatMilk = AppStrings.get("cat_milk", true)
    assertEquals("दूध", hiCatMilk)
    val hiCatPetrol = AppStrings.get("cat_petrol", true)
    assertEquals("पेट्रोल / डीज़ल", hiCatPetrol)
  }

  @Test
  fun testCategorySubtotalCalculations() {
    val expenses = listOf(
      Expense(id = 1, amount = 100.0, categoryId = "milk", categoryNameEn = "Milk", categoryNameHi = "दूध", iconName = "milk", dateMillis = 1000L, dayOfWeek = "Monday", monthYear = "October 2026"),
      Expense(id = 2, amount = 300.0, categoryId = "milk", categoryNameEn = "Milk", categoryNameHi = "दूध", iconName = "milk", dateMillis = 2000L, dayOfWeek = "Monday", monthYear = "October 2026"),
      Expense(id = 3, amount = 600.0, categoryId = "petrol", categoryNameEn = "Petrol", categoryNameHi = "पेट्रोल", iconName = "petrol", dateMillis = 3000L, dayOfWeek = "Tuesday", monthYear = "October 2026")
    )

    val total = expenses.sumOf { it.amount }
    assertEquals(1000.0, total, 0.001)

    val grouped = expenses.groupBy { it.categoryId }
    val milkTotal = grouped["milk"]?.sumOf { it.amount } ?: 0.0
    val petrolTotal = grouped["petrol"]?.sumOf { it.amount } ?: 0.0

    assertEquals(400.0, milkTotal, 0.001)
    assertEquals(600.0, petrolTotal, 0.001)
    assertEquals(0.4f, (milkTotal / total).toFloat(), 0.001f)
    assertEquals(0.6f, (petrolTotal / total).toFloat(), 0.001f)
  }
}
