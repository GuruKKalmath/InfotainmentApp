package com.example.infotainment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(s: VehicleState, onShift: (Gear) -> Unit) {
    Row(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${s.speedKmh}", fontSize = 96.sp, style = MaterialTheme.typography.displayLarge)
            Text("km/h", style = MaterialTheme.typography.titleMedium)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Battery ${s.batteryPct}%", style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(
                progress = { s.batteryPct / 100f },
                modifier = Modifier.fillMaxWidth().height(12.dp)
            )
            Text("Estimated range: ${s.rangeKm} km")
            if (s.batteryPct <= 15) {
                Text("Low battery", color = MaterialTheme.colorScheme.error)
            }
            Text("Gear", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Gear.values().forEach { g ->
                    FilterChip(selected = s.gear == g, onClick = { onShift(g) }, label = { Text(g.name) })
                }
            }
            s.message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
fun MediaScreen(s: VehicleState, onPrev: () -> Unit, onToggle: () -> Unit, onNext: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(s.track.title, style = MaterialTheme.typography.headlineMedium)
        Text(s.track.artist, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { s.positionMs / s.track.durationMs.toFloat() },
            modifier = Modifier.fillMaxWidth(0.6f)
        )
        Text("${fmt(s.positionMs)} / ${fmt(s.track.durationMs)}")
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onPrev) { Text("Prev") }
            Button(onClick = onToggle) { Text(if (s.playing) "Pause" else "Play") }
            Button(onClick = onNext) { Text("Next") }
        }
    }
}

@Composable
fun ClimateScreen(
    s: VehicleState, onToggleAc: () -> Unit, onTemp: (Int) -> Unit, onFan: (Int) -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("A/C", style = MaterialTheme.typography.titleMedium)
            Switch(checked = s.acOn, onCheckedChange = { onToggleAc() })
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { onTemp(-1) }) { Text("-") }
            Text("${s.tempC}°C", fontSize = 40.sp)
            OutlinedButton(onClick = { onTemp(+1) }) { Text("+") }
        }
        Text("Fan speed: ${s.fanLevel}")
        Slider(
            value = s.fanLevel.toFloat(),
            onValueChange = { onFan(it.toInt()) },
            valueRange = 1f..5f, steps = 3,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }
}

private fun fmt(ms: Int): String = "%d:%02d".format(ms / 60_000, (ms / 1000) % 60)
