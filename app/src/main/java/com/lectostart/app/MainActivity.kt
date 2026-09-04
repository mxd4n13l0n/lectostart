package com.lectostart.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.lectostart.app.core.data.seed.SeedLoader
import com.lectostart.app.core.navigation.LectoStartNavigation
import com.lectostart.app.core.ui.theme.LectoStartTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject lateinit var seedLoader: SeedLoader

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycleScope.launch { seedLoader.seedIfNeeded() }

    enableEdgeToEdge()
    setContent {
      LectoStartTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { LectoStartNavigation() }
      }
    }
  }
}
