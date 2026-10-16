package com.example.infotainment

enum class Gear { P, R, N, D }

data class Track(val title: String, val artist: String, val durationMs: Int)

val TRACKS = listOf(
    Track("Highway Lights", "The Roadies", 204_000),
    Track("Night Drive", "Neon Avenue", 187_000),
    Track("Open Roads", "Kavya & Co", 231_000)
)

data class VehicleState(
    val speedKmh: Int = 0,
    val batteryPct: Int = 80,
    val gear: Gear = Gear.P,
    val acOn: Boolean = false,
    val tempC: Int = 22,
    val fanLevel: Int = 2,
    val trackIndex: Int = 0,
    val playing: Boolean = false,
    val positionMs: Int = 0,
    val ticks: Int = 0,
    val message: String? = null
) {
    val rangeKm: Int get() = estimateRangeKm(batteryPct)
    val track: Track get() = TRACKS[trackIndex]
}

fun estimateRangeKm(batteryPct: Int, kmPerPct: Float = 4.2f): Int =
    (batteryPct.coerceIn(0, 100) * kmPerPct).toInt()

/** Pure, testable simulation logic. The ViewModel just calls these on a timer / on user input. */
class VehicleSimulator {

    fun step(s: VehicleState): VehicleState {
        val target = if (s.gear == Gear.D && s.batteryPct > 0) TARGET_SPEED else 0
        val speed = when {
            s.speedKmh < target -> minOf(s.speedKmh + ACCEL, target)
            s.speedKmh > target -> maxOf(s.speedKmh - BRAKE, target)
            else -> s.speedKmh
        }
        val ticks = s.ticks + 1
        var battery = s.batteryPct
        if (speed > 0 && ticks % DRIVE_DRAIN_EVERY == 0) battery -= 1
        if (s.acOn && ticks % AC_DRAIN_EVERY == 0) battery -= 1
        battery = battery.coerceAtLeast(0)

        var index = s.trackIndex
        var position = s.positionMs
        if (s.playing) {
            position += TICK_MS
            if (position >= TRACKS[index].durationMs) {
                index = (index + 1) % TRACKS.size
                position = 0
            }
        }
        return s.copy(
            speedKmh = speed, batteryPct = battery, ticks = ticks,
            trackIndex = index, positionMs = position
        )
    }

    fun shift(s: VehicleState, gear: Gear): VehicleState =
        if (gear != Gear.D && s.speedKmh > 0)
            s.copy(message = "Slow to a stop before selecting ${gear.name}")
        else s.copy(gear = gear, message = null)

    fun toggleAc(s: VehicleState) = s.copy(acOn = !s.acOn)
    fun changeTemp(s: VehicleState, delta: Int) = s.copy(tempC = (s.tempC + delta).coerceIn(16, 30))
    fun setFan(s: VehicleState, level: Int) = s.copy(fanLevel = level.coerceIn(1, 5))

    fun togglePlay(s: VehicleState) = s.copy(playing = !s.playing)
    fun nextTrack(s: VehicleState) = s.copy(trackIndex = (s.trackIndex + 1) % TRACKS.size, positionMs = 0)
    fun prevTrack(s: VehicleState) =
        if (s.positionMs > 3_000) s.copy(positionMs = 0)
        else s.copy(trackIndex = (s.trackIndex - 1 + TRACKS.size) % TRACKS.size, positionMs = 0)

    companion object {
        const val TICK_MS = 500
        const val TARGET_SPEED = 60
        const val ACCEL = 5
        const val BRAKE = 8
        const val DRIVE_DRAIN_EVERY = 20
        const val AC_DRAIN_EVERY = 60
    }
}
