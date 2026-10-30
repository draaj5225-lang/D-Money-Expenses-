package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
  @Query("SELECT * FROM expenses ORDER BY dateMillis DESC, id DESC")
  fun getAllExpenses(): Flow<List<ExpenseEntity>>

  @Query("SELECT * FROM expenses WHERE dateMillis >= :startTime AND dateMillis <= :endTime ORDER BY dateMillis DESC, id DESC")
  fun getExpensesBetweenDates(startTime: Long, endTime: Long): Flow<List<ExpenseEntity>>

  @Query("SELECT * FROM expenses WHERE categoryId = :catId ORDER BY dateMillis DESC")
  fun getExpensesByCategory(catId: String): Flow<List<ExpenseEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpense(expense: ExpenseEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpenses(expenses: List<ExpenseEntity>)

  @Update
  suspend fun updateExpense(expense: ExpenseEntity)

  @Delete
  suspend fun deleteExpense(expense: ExpenseEntity)

  @Query("DELETE FROM expenses WHERE id = :id")
  suspend fun deleteExpenseById(id: Long)

  @Query("DELETE FROM expenses")
  suspend fun clearAllExpenses()

  // Categories
  @Query("SELECT * FROM categories ORDER BY isCustom ASC, id ASC")
  fun getAllCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<CategoryEntity>)

  @Query("DELETE FROM categories WHERE id = :id AND isCustom = 1")
  suspend fun deleteCustomCategory(id: String)

  @Query("DELETE FROM categories")
  suspend fun clearCategories()
}
