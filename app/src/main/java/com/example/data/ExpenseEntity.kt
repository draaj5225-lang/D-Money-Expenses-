package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Expense

@Entity(tableName = "expenses")
data class ExpenseEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val amount: Double,
  val categoryId: String,
  val categoryNameEn: String,
  val categoryNameHi: String,
  val iconName: String,
  val dateMillis: Long,
  val dayOfWeek: String,
  val monthYear: String,
  val notes: String,
  val paymentMode: String
) {
  fun toDomain(): Expense = Expense(
    id = id,
    amount = amount,
    categoryId = categoryId,
    categoryNameEn = categoryNameEn,
    categoryNameHi = categoryNameHi,
    iconName = iconName,
    dateMillis = dateMillis,
    dayOfWeek = dayOfWeek,
    monthYear = monthYear,
    notes = notes,
    paymentMode = paymentMode
  )

  companion object {
    fun fromDomain(expense: Expense): ExpenseEntity = ExpenseEntity(
      id = expense.id,
      amount = expense.amount,
      categoryId = expense.categoryId,
      categoryNameEn = expense.categoryNameEn,
      categoryNameHi = expense.categoryNameHi,
      iconName = expense.iconName,
      dateMillis = expense.dateMillis,
      dayOfWeek = expense.dayOfWeek,
      monthYear = expense.monthYear,
      notes = expense.notes,
      paymentMode = expense.paymentMode
    )
  }
}
