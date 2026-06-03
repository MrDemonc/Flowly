package com.demonlab.flowly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.demonlab.flowly.core.util.CurrencySymbol
import com.demonlab.flowly.navigation.FlowlyNavGraph
import com.demonlab.flowly.presentation.onboarding.OnboardingScreen
import com.demonlab.flowly.ui.theme.FlowlyTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as FlowlyApp

        setContent {
            FlowlyTheme {
                val currency by app.settingsDataStore.currency.collectAsState(initial = "Gs")
                LaunchedEffect(currency) {
                    CurrencySymbol.current = currency
                }

                var isFirstRun by remember { mutableStateOf<Boolean?>(null) }
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    isFirstRun = app.settingsDataStore.isFirstRun.first()
                }

                when (isFirstRun) {
                    null -> {}
                    true -> OnboardingScreen(
                        onComplete = {
                            scope.launch {
                                app.settingsDataStore.setIsFirstRun(false)
                            }
                            isFirstRun = false
                        }
                    )
                    false -> Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        FlowlyNavGraph(app = app)
                    }
                }
            }
        }
    }
}
