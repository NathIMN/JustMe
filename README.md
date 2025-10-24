# JustMe — Android Wellness & Habit Tracker

A native Android application built with Kotlin to help users track daily habits, monitor moods, and quickly log activities through a clean, modern interface.

This repository contains the Android app module `app/` implemented with Android Jetpack components and a single-activity-style UI backed by RecyclerViews and adapters.

## Key features

- Create, edit and delete habits with weekly schedules.
- Track habit completion history and view statistics (weekly and per-habit breakdowns).
- Mood tracking with a calendar view and quick mood logging.
- Quick log screen for fast check-ins.
- Home screen App Widget for at-a-glance habit completion and quick actions.
- Settings and profile screens.
- Splash screen and polished navigation flows.

## Observed project structure

- `app/src/main/java/com/example/justme/ui/` — UI layer with fragments and activities, organized by feature (habits, mood, profile, quicklog, settings, splash).
- `app/src/main/res/layout/` — XML layouts for activities, fragments, list items and widgets (e.g., `widget_habit_item.xml`).
- `app/build.gradle.kts`, project-level Gradle files.

## Technologies

- Kotlin
- Android Jetpack (Fragments, Navigation, ViewModel patterns visible via package structure)
- Material Design (XML components)
- RecyclerView-based lists and multiple adapters
- App Widgets (home screen widget layouts present)

Assumptions: The project likely uses local persistence (Room or SharedPreferences) and coroutines/Flow for async operations. If you rely on external libraries (Room, Hilt, MPAndroidChart), ensure they are declared in the Gradle files.

## Architecture (recommended / inferred)

- Pattern: MVVM-style separation (UI folders and adapters suggest ViewModel usage).
- Layers: UI (fragments/activities + adapters), Data (local persistence), Domain (business logic).

## Getting started

Prerequisites
- Android Studio (Narwhal recommended)
- JDK 21 or higher

Quick start
1. Clone the repository and open it in Android Studio.
2. Let Gradle sync and download dependencies.
3. Run the app on an emulator or a connected device.

Command-line building

```bash
# From project root
./gradlew assembleDebug
./gradlew installDebug
# Run unit tests
./gradlew testDebugUnitTest
# Run instrumentation tests (device or emulator required)
./gradlew connectedDebugAndroidTest
```


