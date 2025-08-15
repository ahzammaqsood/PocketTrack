# PocketTrack (Android)

An offline expense & income tracker in Kotlin (XML Views) using Room + MVVM + LiveData, with MPAndroidChart (pie & bar), search & filter, CSV/PDF export, theme toggle, and AdMob (test IDs).

## Open & Build
- Open the `PocketTrack` folder in Android Studio.
- Let Gradle sync. If wrapper prompt appears, accept to generate wrapper.
- Run on a device/emulator (Ads require network; app data works offline).

## Features
- Add/Edit/Delete transactions with categories
- Monthly summary with charts
- Search and filter by date/category
- Export to CSV or PDF via Storage Access Framework
- Light/Dark theme toggle (persisted)
- AdMob banner and interstitial (test IDs)

## Tests
- Unit: `TransactionsViewModelTest`
- UI: `MainActivityTest`

## AdMob IDs
- App ID (test): ca-app-pub-3940256099942544~3347511713
- Banner (test): ca-app-pub-3940256099942544/6300978111
- Interstitial (test): ca-app-pub-3940256099942544/1033173712

Replace with your real IDs before publishing.