package com.example.model

/**
 * Domain model representing an expense transaction.
 */
data class Expense(
  val id: Long = 0,
  val amount: Double,
  val categoryId: String,
  val categoryNameEn: String,
  val categoryNameHi: String,
  val iconName: String,
  val dateMillis: Long,
  val dayOfWeek: String, // e.g. "Monday", "सोमवार"
  val monthYear: String, // e.g. "October 2026", "अक्टूबर 2026"
  val notes: String = "",
  val paymentMode: String = "UPI" // "UPI", "Cash", "Card", "NetBanking"
)
