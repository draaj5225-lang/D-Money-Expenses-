package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.CategorySummary
import java.util.Locale

@Composable
fun CategorySubtotalCard(
  summary: CategorySummary,
  isHindi: Boolean,
  onClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val cat = summary.category
  val catColor = Color(cat.colorHex)

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .width(180.dp)
      .clickable(onClick = onClick)
      .testTag("category_subtotal_card_${cat.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        CategoryIconBadge(
          iconName = cat.iconName,
          colorHex = cat.colorHex,
          size = 38.dp,
          iconSize = 20.dp
        )
        Text(
          text = "${(summary.percentageOfTotal * 100).toInt()}%",
          style = MaterialTheme.typography.labelMedium.copy(
            color = catColor,
            fontWeight = FontWeight.Bold
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = cat.localizedName(isHindi),
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.SemiBold
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = "${summary.count} ${AppStrings.get("total_transactions", isHindi).lowercase()}",
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "₹${String.format(Locale.getDefault(), "%,.0f", summary.totalAmount)}",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      LinearProgressIndicator(
        progress = { summary.percentageOfTotal.coerceIn(0.02f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(5.dp),
        color = catColor,
        trackColor = catColor.copy(alpha = 0.18f),
        strokeCap = StrokeCap.Round
      )
    }
  }
}
