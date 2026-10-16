# Infotainment App (Kotlin + Jetpack Compose)

A vehicle-infotainment-style Android app with simulated data.

- **Dashboard**: speed, battery, estimated range, gear selector (safety rule: cannot shift to P/R/N while moving)
- **Media**: track list, play/pause, next/previous, progress
- **Climate**: A/C toggle, temperature (16-30 C), fan speed

## Architecture
`VehicleSimulator` (pure Kotlin logic, unit tested) -> `VehicleViewModel` (StateFlow, 500 ms tick) -> Compose screens (stateless, state hoisted).

## Run
Open the folder in Android Studio (Koala or newer), let Gradle sync, run on a landscape tablet / automotive emulator.
Unit tests: `./gradlew test` or right-click `VehicleSimulatorTest` > Run.
