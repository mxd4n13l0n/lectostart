package com.lectostart.app.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lectostart.app.core.ui.theme.LectoStartTheme

/** US-01 (docs/USER_STORIES.md). El botón "Comenzar" navega a Consentimiento informado (US-02). */
@Composable
fun WelcomeScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxSize().padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
  ) {
    Text(text = "LectoStart", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
    Text(text = "Empieza pequeño. Comprende más.", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
    Spacer(Modifier.height(8.dp))
    Button(onClick = onNext) { Text("Comenzar") }
  }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
  LectoStartTheme { WelcomeScreen(onNext = {}) }
}
