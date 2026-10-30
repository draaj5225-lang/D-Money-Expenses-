package com.example.data

import com.example.model.Category
import com.example.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepository(
  private val dao: ExpenseDao
) {
  val allExpenses: Flow<List<Expense>> = dao.getAllExpenses().map { list ->
    list.map { it.toDomain() }
  }

  val allCategories: Flow<List<Category>> = dao.getAllCategories().map { list ->
    if (list.isEmpty()) {
      Category.DEFAULT_CATEGORIES
    } else {
      list.map { it.toDomain() }
    }
  }

  fun getExpensesBetweenDates(startTime: Long, endTime: Long): Flow<List<Expense>> {
    return dao.getExpensesBetweenDates(startTime, endTime).map { list ->
      list.map { it.toDomain() }
    }
  }

  suspend fun insertExpense(expense: Expense): Long {
    return dao.insertExpense(ExpenseEntity.fromDomain(expense))
  }

  suspend fun updateExpense(expense: Expense) {
    dao.updateExpense(ExpenseEntity.fromDomain(expense))
  }

  suspend fun deleteExpense(expense: Expense) {
    dao.deleteExpense(ExpenseEntity.fromDomain(expense))
  }

  suspend fun deleteExpenseById(id: Long) {
    dao.deleteExpenseById(id)
  }

  suspend fun insertCategory(category: Category) {
    dao.insertCategory(CategoryEntity.fromDomain(category))
  }

  suspend fun deleteCustomCategory(id: String) {
    dao.deleteCustomCategory(id)
  }

  suspend fun resetAllData() {
    dao.clearAllExpenses()
    dao.clearCategories()
    dao.insertCategories(Category.DEFAULT_CATEGORIES.map { CategoryEntity.fromDomain(it) })
  }
}
