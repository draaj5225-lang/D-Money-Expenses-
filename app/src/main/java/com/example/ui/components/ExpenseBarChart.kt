package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

data class BarChartItem(
  val label: String,
  val amount: Double
)

@Composable
fun ExpenseBarChart(
  items: List<BarChartItem>,
  primaryColor: Color = MaterialTheme.colorScheme.primary,
  modifier: Modifier = Modifier
) {
  val animationProgress = remember { Animatable(0f) }

  LaunchedEffect(items) {
    animationProgress.snapTo(0f)
    animationProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 700)
    )
  }

  val maxVal = (items.maxOfOrNull { it.amount } ?: 1.0).coerceAtLeast(100.0)

  Column(modifier = modifier.fillMaxWidth()) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .padding(horizontal = 8.dp)
    ) {
      Canvas(modifier = Modifier.matchParentSize()) {
        val count = items.size
        if (count == 0) return@Canvas

        val availableWidth = size.width
        val barSlotWidth = availableWidth / count
        val barWidth = (barSlotWidth * 0.55f).coerceIn(12f, 40f)

        // Draw horizontal grid lines
        val gridLines = 3
        for (i in 0..gridLines) {
          val y = size.height * (i.toFloat() / gridLines)
          drawLine(
            color = Color.LightGray.copy(alpha = 0.25f),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx()
          )
        }

        // Draw bars
        items.forEachIndexed { index, item ->
          val fraction = ((item.amount / maxVal) * animationProgress.value).toFloat().coerceIn(0.02f, 1f)
          val barHeight = size.height * fraction
          val x = (index * barSlotWidth) + (barSlotWidth - barWidth) / 2f
          val y = size.height - barHeight

          drawRoundRect(
            color = primaryColor,
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Labels row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      items.forEach { item ->
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = item.label,
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            maxLines = 1,
            textAlign = TextAlign.Center
          )
          if (item.amount > 0) {
            Text(
              text = "₹${formatCompact(item.amount)}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
              ),
              maxLines = 1
            )
          }
        }
      }
    }
  }
}

private fun formatCompact(value: Double): String {
  return when {
    value >= 100000 -> String.format(Locale.getDefault(), "%.1fL", value / 100000)
    value >= 1000 -> String.format(Locale.getDefault(), "%.1fk", value / 1000)
    else -> String.format(Locale.getDefault(), "%.0f", value)
  }
}
