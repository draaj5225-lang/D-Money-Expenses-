package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Teal700

data class OnboardingPageData(
  val titleKey: String,
  val descKey: String,
  val icon: ImageVector,
  val iconColor: Color,
  val iconBg: Color
)

@Composable
fun OnboardingScreen(
  isHindi: Boolean,
  onComplete: () -> Unit
) {
  var currentPage by remember { mutableIntStateOf(0) }

  val pages = listOf(
    OnboardingPageData(
      titleKey = "onboarding_title_1",
      descKey = "onboarding_desc_1",
      icon = Icons.Default.AccountBalanceWallet,
      iconColor = Emerald700,
      iconBg = Emerald700.copy(alpha = 0.12f)
    ),
    OnboardingPageData(
      titleKey = "onboarding_title_2",
      descKey = "onboarding_desc_2",
      icon = Icons.Default.PieChart,
      iconColor = Amber500,
      iconBg = Amber500.copy(alpha = 0.15f)
    ),
    OnboardingPageData(
      titleKey = "onboarding_title_3",
      descKey = "onboarding_desc_3",
      icon = Icons.Default.Translate,
      iconColor = Teal700,
      iconBg = Teal700.copy(alpha = 0.14f)
    )
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .padding(24.dp)
      .testTag("onboarding_screen"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top Bar with Skip
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      if (currentPage < pages.size - 1) {
        TextButton(
          onClick = onComplete,
          modifier = Modifier.testTag("onboarding_skip_button")
        ) {
          Text(
            text = AppStrings.get("btn_skip", isHindi),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      } else {
        Spacer(modifier = Modifier.height(48.dp))
      }
    }

    Spacer(modifier = Modifier.weight(0.6f))

    // Page Content with Smooth Transition
    AnimatedContent(
      targetState = currentPage,
      label = "onboarding_content"
    ) { pageIndex ->
      val page = pages[pageIndex]
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .size(130.dp)
            .clip(CircleShape)
            .background(page.iconBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = page.icon,
            contentDescription = null,
            tint = page.iconColor,
            modifier = Modifier.size(64.dp)
          )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
          text = AppStrings.get(page.titleKey, isHindi),
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          ),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = AppStrings.get(page.descKey, isHindi),
          style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
          ),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 16.dp)
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Dots Indicator
    Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 24.dp)
    ) {
      pages.indices.forEach { index ->
        val isSelected = index == currentPage
        Box(
          modifier = Modifier
            .padding(horizontal = 4.dp)
            .height(8.dp)
            .width(if (isSelected) 24.dp else 8.dp)
            .clip(CircleShape)
            .background(
              if (isSelected) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )
      }
    }

    // Bottom Action Button
    Button(
      onClick = {
        if (currentPage < pages.size - 1) {
          currentPage++
        } else {
          onComplete()
        }
      },
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
      ),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("onboarding_next_button")
    ) {
      Text(
        text = if (currentPage == pages.size - 1) {
          AppStrings.get("btn_get_started", isHindi)
        } else {
          AppStrings.get("btn_next", isHindi)
        },
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.SemiBold
        )
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
