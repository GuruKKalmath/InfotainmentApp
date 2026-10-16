package com.example.infotainment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
    private val vm: VehicleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(Modifier.fillMaxSize()) {
                    val state by vm.state.collectAsStateWithLifecycle()
                    InfotainmentApp(state, vm)
                }
            }
        }
    }
}

@Composable
fun InfotainmentApp(state: VehicleState, vm: VehicleViewModel) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Dashboard", "Media", "Climate")
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { i, name ->
                    NavigationBarItem(
                        selected = tab == i, onClick = { tab = i },
                        icon = {}, label = { Text(name) }, alwaysShowLabel = true
                    )
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            when (tab) {
                0 -> DashboardScreen(state, vm::shift)
                1 -> MediaScreen(state, vm::prev, vm::togglePlay, vm::next)
                else -> ClimateScreen(state, vm::toggleAc, vm::changeTemp, vm::setFan)
            }
        }
    }
}
