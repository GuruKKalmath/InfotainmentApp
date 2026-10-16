package com.example.infotainment

import org.junit.Assert.*
import org.junit.Test

class VehicleSimulatorTest {
    private val sim = VehicleSimulator()

    @Test fun cannotSelectReverseWhileMoving() {
        val moving = VehicleState(gear = Gear.D, speedKmh = 40)
        val result = sim.shift(moving, Gear.R)
        assertEquals(Gear.D, result.gear)
        assertNotNull(result.message)
    }

    @Test fun canSelectParkWhenStopped() {
        assertEquals(Gear.P, sim.shift(VehicleState(gear = Gear.D), Gear.P).gear)
    }

    @Test fun acceleratesInDriveUpToTarget() {
        var s = VehicleState(gear = Gear.D)
        repeat(30) { s = sim.step(s) }
        assertEquals(VehicleSimulator.TARGET_SPEED, s.speedKmh)
    }

    @Test fun batteryNeverGoesBelowZero() {
        var s = VehicleState(gear = Gear.D, batteryPct = 0, speedKmh = 30)
        repeat(100) { s = sim.step(s) }
        assertEquals(0, s.batteryPct)
        assertEquals(0, s.speedKmh)
    }

    @Test fun temperatureIsClamped() {
        assertEquals(30, sim.changeTemp(VehicleState(tempC = 30), +5).tempC)
        assertEquals(16, sim.changeTemp(VehicleState(tempC = 16), -5).tempC)
    }

    @Test fun trackWrapsAround() {
        val last = VehicleState(trackIndex = TRACKS.lastIndex)
        assertEquals(0, sim.nextTrack(last).trackIndex)
        assertEquals(TRACKS.lastIndex, sim.prevTrack(VehicleState(trackIndex = 0)).trackIndex)
    }

    @Test fun rangeEstimate() {
        assertEquals(336, estimateRangeKm(80))
    }
}
