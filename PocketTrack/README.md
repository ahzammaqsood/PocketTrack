# PocketTrack (Android)

An offline expense & income tracker built with Kotlin, XML Views, Room, MVVM, and MPAndroidChart. Includes AdMob (test IDs), CSV/PDF export, light/dark theme toggle, and tests (JUnit/Espresso).

## Requirements
- Android 8.0+ (minSdk 26)
- Kotlin 2.2
- Android Gradle Plugin 8.11

## Build
1. Open this folder in Android Studio (Giraffe+).
2. Let Gradle sync. If prompted, use JDK 17 or higher.
3. Run on device/emulator. Ads use Google test IDs by default.

## Generate APK
- Build &gt; Build Bundle(s) / APK(s) &gt; Build APK(s)

## Test
- Unit tests: `./gradlew testDebugUnitTest`
- Instrumented (UI) tests: `./gradlew connectedDebugAndroidTest`

## Notes
- 100% offline for data; Ads require network.
- Exports use Android SAF to let you choose a destination.