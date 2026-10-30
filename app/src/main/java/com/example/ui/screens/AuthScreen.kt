package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald700
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
  isHindi: Boolean,
  onLoginSuccess: (name: String, phone: String) -> Unit,
  onLanguageToggle: () -> Unit
) {
  var step by remember { mutableIntStateOf(1) } // 1 = Phone & Name, 2 = OTP
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var otp by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var timerSeconds by remember { mutableIntStateOf(30) }

  // Countdown timer for OTP
  LaunchedEffect(step, timerSeconds) {
    if (step == 2 && timerSeconds > 0) {
      delay(1000)
      timerSeconds--
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding()
      .verticalScroll(rememberScrollState())
      .padding(24.dp)
      .testTag("auth_screen"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top Bar with Language Toggle & Back
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (step == 2) {
        IconButton(
          onClick = {
            step = 1
            errorMessage = null
          },
          modifier = Modifier.testTag("auth_back_button")
        ) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
      } else {
        Spacer(modifier = Modifier.size(48.dp))
      }

      // Language Switcher Chip
      OutlinedButton(
        onClick = onLanguageToggle,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("auth_language_toggle")
      ) {
        Text(
          text = if (isHindi) "English" else "हिंदी",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Icon Header
    Box(
      modifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .background(Emerald700.copy(alpha = 0.12f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (step == 1) Icons.Default.PhoneAndroid else Icons.Default.Security,
        contentDescription = null,
        tint = Emerald700,
        modifier = Modifier.size(40.dp)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = if (step == 1) AppStrings.get("auth_title", isHindi) else AppStrings.get("otp_title", isHindi),
      style = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      ),
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = if (step == 1) {
        AppStrings.get("auth_subtitle", isHindi)
      } else {
        "${AppStrings.get("otp_subtitle", isHindi)} +91 $phone"
      },
      style = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
      ),
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(32.dp))

    if (step == 1) {
      // Step 1: Name and Phone Input
      OutlinedTextField(
        value = name,
        onValueChange = {
          name = it
          errorMessage = null
        },
        label = { Text(AppStrings.get("name_label", isHindi)) },
        placeholder = { Text(AppStrings.get("name_placeholder", isHindi)) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_name_input")
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = phone,
        onValueChange = { input ->
          if (input.length <= 10 && input.all { it.isDigit() }) {
            phone = input
            errorMessage = null
          }
        },
        label = { Text(AppStrings.get("phone_label", isHindi)) },
        placeholder = { Text(AppStrings.get("phone_placeholder", isHindi)) },
        leadingIcon = {
          Text(
            text = "+91 ",
            style = MaterialTheme.typography.bodyLarge.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(start = 12.dp)
          )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_phone_input")
      )

      AnimatedVisibility(visible = errorMessage != null) {
        Text(
          text = errorMessage ?: "",
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 4.dp)
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      Button(
        onClick = {
          if (name.isBlank()) {
            errorMessage = AppStrings.get("error_fill_name", isHindi)
          } else if (phone.length < 10) {
            errorMessage = AppStrings.get("invalid_phone", isHindi)
          } else {
            errorMessage = null
            timerSeconds = 30
            step = 2
          }
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("auth_get_otp_button")
      ) {
        Text(
          text = AppStrings.get("btn_send_otp", isHindi),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Instant Skip/Demo login button for ultimate frictionless UX
      TextButton(
        onClick = {
          onLoginSuccess("Kharcha User", "9876543210")
        },
        modifier = Modifier.testTag("auth_guest_button")
      ) {
        Text(
          text = if (isHindi) "डेमो खाते से तुरंत जारी रखें" else "Quick Demo Login (Skip OTP)",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
          )
        )
      }

    } else {
      // Step 2: OTP Entry
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
          containerColor = Amber500.copy(alpha = 0.12f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 20.dp)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Amber500,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = AppStrings.get("otp_helper", isHindi),
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurface
            )
          )
        }
      }

      OutlinedTextField(
        value = otp,
        onValueChange = { input ->
          if (input.length <= 6 && input.all { it.isDigit() }) {
            otp = input
            errorMessage = null
          }
        },
        label = { Text("6-Digit OTP") },
        placeholder = { Text("123456") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_otp_input")
      )

      // Auto-fill Demo Chip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = { otp = "123456" },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("auth_autofill_otp_button")
        ) {
          Text(AppStrings.get("btn_quick_fill_otp", isHindi), fontSize = 12.sp)
        }

        Text(
          text = if (timerSeconds > 0) "${timerSeconds}s" else "",
          style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
        )
      }

      AnimatedVisibility(visible = errorMessage != null) {
        Text(
          text = errorMessage ?: "",
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 4.dp)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Button(
        onClick = {
          if (otp == "123456" || otp.length == 6) {
            onLoginSuccess(name.ifBlank { "User" }, phone.ifBlank { "9876543210" })
          } else {
            errorMessage = AppStrings.get("invalid_otp", isHindi)
          }
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("auth_verify_button")
      ) {
        Text(
          text = AppStrings.get("btn_verify", isHindi),
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      TextButton(
        onClick = {
          timerSeconds = 30
          otp = ""
        },
        enabled = timerSeconds == 0,
        modifier = Modifier.testTag("auth_resend_button")
      ) {
        Text(
          text = AppStrings.get("btn_resend_otp", isHindi),
          color = if (timerSeconds == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
      }
    }
  }
}
