package com.example.infotainment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleViewModel : ViewModel() {
    private val sim = VehicleSimulator()
    private val _state = MutableStateFlow(VehicleState())
    val state: StateFlow<VehicleState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                delay(VehicleSimulator.TICK_MS.toLong())
                _state.update(sim::step)
            }
        }
    }

    fun shift(gear: Gear) = _state.update { sim.shift(it, gear) }
    fun toggleAc() = _state.update(sim::toggleAc)
    fun changeTemp(delta: Int) = _state.update { sim.changeTemp(it, delta) }
    fun setFan(level: Int) = _state.update { sim.setFan(it, level) }
    fun togglePlay() = _state.update(sim::togglePlay)
    fun next() = _state.update(sim::nextTrack)
    fun prev() = _state.update(sim::prevTrack)
}
