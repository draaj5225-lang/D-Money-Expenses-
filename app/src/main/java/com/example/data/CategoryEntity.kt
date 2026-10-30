package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey
  val id: String,
  val nameEn: String,
  val nameHi: String,
  val iconName: String,
  val colorHex: Long,
  val isCustom: Boolean
) {
  fun toDomain(): Category = Category(
    id = id,
    nameEn = nameEn,
    nameHi = nameHi,
    iconName = iconName,
    colorHex = colorHex,
    isCustom = isCustom
  )

  companion object {
    fun fromDomain(cat: Category): CategoryEntity = CategoryEntity(
      id = cat.id,
      nameEn = cat.nameEn,
      nameHi = cat.nameHi,
      iconName = cat.iconName,
      colorHex = cat.colorHex,
      isCustom = cat.isCustom
    )
  }
}
