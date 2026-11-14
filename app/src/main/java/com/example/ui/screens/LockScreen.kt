package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.local.LockType
import com.example.localization.LocalAppStrings
import com.example.ui.components.ProfileAvatar
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.LuminousGold
import com.example.ui.theme.RoyalIndigo

@Composable
fun LockScreen(
    lockType: String,
    userName: String,
    userPhotoPath: String,
    hasError: Boolean,
    onClearError: () -> Unit,
    onAttemptUnlock: (String) -> Unit
) {
    val s = LocalAppStrings.current
    var inputSecret by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(RoyalIndigo, DeepPlum, Color(0xFF14112B))
                )
            )
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (userPhotoPath.isNotBlank()) {
                ProfileAvatar(
                    photoPath = userPhotoPath,
                    contentDescription = userName,
                    size = 92.dp,
                    borderColor = LuminousGold
                )
            } else {
                Surface(
                    shape = CircleShape,
                    color = LuminousGold.copy(alpha = 0.16f),
                    modifier = Modifier.size(84.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = s.lockScreenTitle,
                            tint = LuminousGold,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }

            Text(
                text = if (userName.isNotBlank()) userName else s.appTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (lockType == LockType.PIN) s.enterPinPrompt else s.enterPasswordPrompt,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.78f),
                textAlign = TextAlign.Center
            )

            AnimatedVisibility(visible = hasError) {
                Surface(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = s.incorrectCredential,
                        color = Color(0xFFFFB4AB),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (lockType == LockType.PIN) {
                // PIN dots indicator
                val dotCount = maxOf(4, inputSecret.length)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    for (i in 0 until dotCount) {
                        val filled = i < inputSecret.length
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (filled) LuminousGold else Color.Transparent)
                                .border(2.dp, LuminousGold, CircleShape)
                        )
                    }
                }

                // Numeric Keypad
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("DEL", "0", "OK")
                )
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    rows.forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            row.forEach { key ->
                                when (key) {
                                    "DEL" -> PinKeyButton(
                                        onClick = {
                                            if (inputSecret.isNotEmpty()) {
                                                inputSecret = inputSecret.dropLast(1)
                                            }
                                            onClearError()
                                        },
                                        testTag = "pin_backspace_button"
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                                            contentDescription = s.delete,
                                            tint = Color.White
                                        )
                                    }
                                    "OK" -> PinKeyButton(
                                        containerColor = LuminousGold,
                                        onClick = { onAttemptUnlock(inputSecret) },
                                        testTag = "pin_unlock_button"
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = s.unlockButton,
                                            tint = RoyalIndigo
                                        )
                                    }
                                    else -> PinKeyButton(
                                        onClick = {
                                            if (inputSecret.length < 10) {
                                                inputSecret += key
                                                onClearError()
                                            }
                                        },
                                        testTag = "pin_digit_$key"
                                    ) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = inputSecret,
                    onValueChange = {
                        inputSecret = it
                        onClearError()
                    },
                    label = { Text(s.passwordOption) },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onAttemptUnlock(inputSecret) }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = LuminousGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.45f),
                        focusedLabelColor = LuminousGold,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_unlock_input")
                )

                Button(
                    onClick = { onAttemptUnlock(inputSecret) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LuminousGold,
                        contentColor = RoyalIndigo
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("password_unlock_button")
                ) {
                    Text(
                        text = s.unlockButton,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PinKeyButton(
    onClick: () -> Unit,
    testTag: String,
    containerColor: Color = Color.White.copy(alpha = 0.12f),
    content: @Composable () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = containerColor,
        modifier = Modifier
            .size(72.dp)
            .minimumInteractiveComponentSize()
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}
