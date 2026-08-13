package com.demonlab.flowly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.demonlab.flowly.navigation.FlowlyNavGraph
import com.demonlab.flowly.ui.theme.FlowlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as FlowlyApp

        setContent {
            val themeMode by app.settingsDataStore.themeMode.collectAsState(initial = "system")

            FlowlyTheme(themeMode = themeMode, dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlowlyNavGraph(app = app)
                }
            }
        }
    }
}
