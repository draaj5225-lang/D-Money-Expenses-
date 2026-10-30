package com.example.model

/**
 * Domain model representing an expense category.
 */
data class Category(
  val id: String,
  val nameEn: String,
  val nameHi: String,
  val iconName: String,
  val colorHex: Long,
  val isCustom: Boolean = false
) {
  fun localizedName(isHindi: Boolean): String {
    return if (isHindi) nameHi else nameEn
  }

  companion object {
    // 5 Pre-defined categories requested in specification:
    // Grocery (अनाज / किराना)
    // Milk (दूध)
    // Petrol / Diesel (पेट्रोल / डीज़ल)
    // Bills - Phone, Electricity, Water (बिल्स - फोन, बिजली, पानी)
    // Other Expenses (अन्य खर्चे)
    val DEFAULT_CATEGORIES = listOf(
      Category(
        id = "grocery",
        nameEn = "Grocery (Kirana)",
        nameHi = "अनाज / किराना",
        iconName = "grocery",
        colorHex = 0xFFF97316L,
        isCustom = false
      ),
      Category(
        id = "milk",
        nameEn = "Milk",
        nameHi = "दूध",
        iconName = "milk",
        colorHex = 0xFF0EA5E9L,
        isCustom = false
      ),
      Category(
        id = "petrol",
        nameEn = "Petrol / Diesel",
        nameHi = "पेट्रोल / डीज़ल",
        iconName = "petrol",
        colorHex = 0xFFEF4444L,
        isCustom = false
      ),
      Category(
        id = "bills",
        nameEn = "Bills (Phone, Power, Water)",
        nameHi = "बिल्स (फोन, बिजली, पानी)",
        iconName = "bills",
        colorHex = 0xFF8B5CF6L,
        isCustom = false
      ),
      Category(
        id = "other",
        nameEn = "Other Expenses",
        nameHi = "अन्य खर्चे",
        iconName = "other",
        colorHex = 0xFF64748BL,
        isCustom = false
      )
    )

    // Selectable icon choices when user creates a custom category
    val ICON_OPTIONS = listOf(
      "food" to "Restaurant",
      "health" to "Medicine / Health",
      "shopping" to "Clothes / Shopping",
      "transport" to "Auto / Travel",
      "education" to "Education",
      "entertainment" to "Entertainment",
      "home" to "Home Repair",
      "gift" to "Gift / Donation",
      "grocery" to "Grocery",
      "milk" to "Milk / Dairy",
      "petrol" to "Fuel",
      "bills" to "Bills & Utilities"
    )

    // Palette of colors for custom categories
    val COLOR_OPTIONS = listOf(
      0xFFF97316L, // Orange
      0xFF0EA5E9L, // Sky Blue
      0xFFEF4444L, // Red
      0xFF8B5CF6L, // Purple
      0xFF10B981L, // Emerald
      0xFFF59E0BL, // Amber
      0xFFEC4899L, // Pink
      0xFF6366F1L, // Indigo
      0xFF14B8A6L, // Teal
      0xFF64748BL  // Slate
    )
  }
}
