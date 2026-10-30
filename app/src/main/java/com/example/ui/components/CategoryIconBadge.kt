package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CategoryIconBadge(
  iconName: String,
  colorHex: Long,
  size: Dp = 44.dp,
  iconSize: Dp = 24.dp,
  modifier: Modifier = Modifier
) {
  val iconColor = Color(colorHex)
  val backgroundColor = iconColor.copy(alpha = 0.14f)
  val icon = getCategoryIconVector(iconName)

  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(backgroundColor),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = iconName,
      tint = iconColor,
      modifier = Modifier.size(iconSize)
    )
  }
}

fun getCategoryIconVector(iconName: String): ImageVector {
  return when (iconName.lowercase()) {
    "grocery", "shopping_basket" -> Icons.Default.ShoppingBasket
    "milk", "dairy", "local_drink" -> Icons.Default.LocalDrink
    "petrol", "diesel", "fuel", "gas", "local_gas_station" -> Icons.Default.LocalGasStation
    "bills", "receipt_long", "power", "water" -> Icons.Default.ReceiptLong
    "food", "restaurant", "dining" -> Icons.Default.Restaurant
    "health", "medical", "medicine" -> Icons.Default.MedicalServices
    "shopping", "clothes", "mall" -> Icons.Default.LocalMall
    "transport", "auto", "car", "travel" -> Icons.Default.DirectionsCar
    "education", "school", "books" -> Icons.Default.School
    "entertainment", "movies", "theaters" -> Icons.Default.Theaters
    "home", "repair", "build" -> Icons.Default.Build
    "gift", "card_giftcard" -> Icons.Default.CardGiftcard
    else -> Icons.Default.Category
  }
}
