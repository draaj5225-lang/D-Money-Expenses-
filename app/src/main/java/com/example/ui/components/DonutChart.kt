package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.CategorySummary
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DonutChart(
  data: List<CategorySummary>,
  totalAmount: Double,
  isHindi: Boolean,
  chartSize: Dp = 200.dp,
  strokeWidth: Dp = 26.dp,
  modifier: Modifier = Modifier
) {
  var selectedIndex by remember { mutableStateOf<Int?>(null) }
  val animationProgress = remember { Animatable(0f) }

  LaunchedEffect(data) {
    animationProgress.snapTo(0f)
    animationProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 800)
    )
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier.size(chartSize),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(chartSize)) {
        val sweepAngleTotal = 360f * animationProgress.value
        var startAngle = -90f

        val strokePx = strokeWidth.toPx()
        val diameter = size.minDimension - strokePx
        val topLeft = Offset(
          (size.width - diameter) / 2f,
          (size.height - diameter) / 2f
        )
        val arcSize = Size(diameter, diameter)

        if (data.isEmpty() || totalAmount <= 0.0) {
          // Empty placeholder ring
          drawArc(
            color = Color.LightGray.copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
          )
        } else {
          data.forEachIndexed { index, item ->
            val sweep = (item.percentageOfTotal * sweepAngleTotal)
            val isSelected = selectedIndex == index
            val effectiveStroke = if (isSelected) strokePx + 6.dp.toPx() else strokePx

            drawArc(
              color = Color(item.category.colorHex),
              startAngle = startAngle,
              sweepAngle = sweep.coerceAtLeast(1f),
              useCenter = false,
              topLeft = topLeft,
              size = arcSize,
              style = Stroke(width = effectiveStroke, cap = StrokeCap.Butt)
            )
            startAngle += sweep
          }
        }
      }

      // Center summary text
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
      ) {
        val activeItem = selectedIndex?.let { data.getOrNull(it) }

        if (activeItem != null) {
          Text(
            text = activeItem.category.localizedName(isHindi),
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color(activeItem.category.colorHex),
              fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            maxLines = 1
          )
          Text(
            text = "₹${String.format(Locale.getDefault(), "%,.0f", activeItem.totalAmount)}",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.Center
          )
          Text(
            text = "${(activeItem.percentageOfTotal * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          )
        } else {
          Text(
            text = AppStrings.get("total_expenses", isHindi),
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            ),
            textAlign = TextAlign.Center
          )
          Text(
            text = "₹${String.format(Locale.getDefault(), "%,.0f", totalAmount)}",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Interactive Legend Pills
    FlowRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.Center,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      data.forEachIndexed { index, item ->
        val isSelected = selectedIndex == index
        val itemColor = Color(item.category.colorHex)

        Row(
          modifier = Modifier
            .clip(CircleShape)
            .clickable {
              selectedIndex = if (selectedIndex == index) null else index
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
          ) {
            Canvas(modifier = Modifier.matchParentSize()) {
              drawCircle(color = itemColor)
            }
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${item.category.localizedName(isHindi)} ${(item.percentageOfTotal * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) itemColor else MaterialTheme.colorScheme.onSurface,
              fontSize = 12.sp
            )
          )
        }
      }
    }
  }
}
