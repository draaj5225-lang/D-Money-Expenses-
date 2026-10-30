package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.ui.theme.Amber400
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Teal700
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  isHindi: Boolean,
  onFinish: () -> Unit
) {
  val scale = remember { Animatable(0.7f) }

  LaunchedEffect(Unit) {
    scale.animateTo(
      targetValue = 1f,
      animationSpec = tween(
        durationMillis = 900,
        easing = FastOutSlowInEasing
      )
    )
    delay(500)
    onFinish()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Emerald900,
            Emerald700,
            Teal700
          )
        )
      )
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.scale(scale.value)
    ) {
      // Elegant circular logo with Indian Rupee
      Box(
        modifier = Modifier
          .size(110.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(86.dp)
            .clip(CircleShape)
            .background(Amber400),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "₹",
            style = MaterialTheme.typography.displayMedium.copy(
              color = Emerald900,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 50.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = AppStrings.get("app_name", isHindi),
        style = MaterialTheme.typography.headlineMedium.copy(
          color = Color.White,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = AppStrings.get("tagline", isHindi),
        style = MaterialTheme.typography.bodyMedium.copy(
          color = Color.White.copy(alpha = 0.85f),
          fontSize = 14.sp
        )
      )
    }
  }
}
