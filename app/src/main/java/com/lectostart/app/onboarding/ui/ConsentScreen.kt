package com.lectostart.app.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lectostart.app.core.ui.theme.LectoStartTheme

/**
 * US-02 (docs/USER_STORIES.md). El texto de consentimiento es un borrador de producto — el
 * texto legal/formato final depende del comité de ética (docs/PRD.md §10).
 */
@Composable
fun ConsentScreen(onAccepted: (userId: String) -> Unit, modifier: Modifier = Modifier, viewModel: ConsentViewModel = hiltViewModel()) {
  val status by viewModel.status.collectAsStateWithLifecycle()

  LaunchedEffect(status) {
    val current = status
    if (current is ConsentStatus.Accepted) onAccepted(current.userId)
  }

  when (status) {
    ConsentStatus.Declined -> ConsentDeclinedContent(onReturn = viewModel::onReturnToConsent, modifier = modifier)
    else -> ConsentPendingContent(onAccept = viewModel::onAccept, onDecline = viewModel::onDecline, modifier = modifier)
  }
}

@Composable
private fun ConsentPendingContent(onAccept: () -> Unit, onDecline: () -> Unit, modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxSize()) {
    Text(text = "Consentimiento informado", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(16.dp))
    Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
      Text(text = CONSENT_TEXT, style = MaterialTheme.typography.bodyMedium)
    }
    Spacer(Modifier.height(16.dp))
    Button(onClick = onAccept, modifier = Modifier.fillMaxWidth()) { Text("Aceptar y continuar") }
    Spacer(Modifier.height(8.dp))
    OutlinedButton(onClick = onDecline, modifier = Modifier.fillMaxWidth()) { Text("No aceptar") }
  }
}

@Composable
private fun ConsentDeclinedContent(onReturn: () -> Unit, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(text = "Entendido", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text(
      text = "Gracias por tu tiempo. No es necesario que continúes — puedes cerrar la aplicación cuando quieras. Tu participación siempre es voluntaria.",
      style = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.Center,
    )
    OutlinedButton(onClick = onReturn) { Text("Volver al consentimiento") }
  }
}

private val CONSENT_TEXT =
  """
  Esta aplicación, LectoStart, forma parte de un trabajo de investigación de maestría sobre procrastinación académica y comprensión lectora.

  ¿En qué consiste tu participación?
  Si aceptas, usarás la app para realizar sesiones breves de lectura y responder preguntas de comprensión. La app registrará información como: tus respuestas a un cuestionario inicial sobre hábitos de lectura, el tiempo que dedicas a cada sesión, y tus resultados en las preguntas de comprensión.

  ¿Cómo se usan tus datos?
  Los datos se almacenan de forma anónima (no se asocian a tu nombre real) y se usan únicamente con fines de análisis para esta investigación. No se comparten con terceros.

  ¿Es obligatorio participar?
  No. Tu participación es completamente voluntaria. Puedes dejar de usar la app en cualquier momento, sin ninguna consecuencia negativa para ti.

  Al tocar "Aceptar y continuar" confirmas que has leído esta información y aceptas participar de forma voluntaria.

  (Este texto es un borrador de producto, pendiente de revisión y validación formal por el comité de ética correspondiente antes de usarse con participantes reales.)
  """
    .trimIndent()

@Preview(showBackground = true)
@Composable
private fun ConsentScreenPendingPreview() {
  LectoStartTheme { ConsentPendingContent(onAccept = {}, onDecline = {}, modifier = Modifier.fillMaxSize()) }
}

@Preview(showBackground = true)
@Composable
private fun ConsentScreenDeclinedPreview() {
  LectoStartTheme { ConsentDeclinedContent(onReturn = {}, modifier = Modifier.fillMaxSize()) }
}
