# Overview

BurstLog is an Android workout timer and logger for interval training such as Tabata and HIIT. The app lets users set custom work and rest intervals, run a countdown timer with audio coaching, and automatically saves each finished session to a local database. It supports both dark and light themes.

To use the app, open it and tap "New Workout" on the Home screen. On the Setup screen, enter a workout name and choose work seconds, rest seconds, and the number of rounds, then tap Start. The Timer screen shows a large countdown number with the current phase (WORK or REST) and round counter. Trainer voice cues call out the start of each interval and count down the final seconds. When the session ends, the record is saved automatically and appears on the Home screen along with the user's weekly stats.

My purpose for creating this app was to learn Kotlin, Jetpack Compose, and the modern Android architecture as my second deep-dive module in the CSE 310 course. I wanted to ship something I would actually use myself, since I do interval training a few times a week and most free apps either have too many ads or do not let me customize the countdown cues the way I want.

# Development Environment

I developed BurstLog using Android Studio Ladybug on a Windows laptop and tested on a real Samsung Galaxy S22 connected over USB. The build system is Gradle 8.10 with the Android Gradle Plugin 8.7.

The app is written in Kotlin 2.0 and uses Jetpack Compose with Material 3 for the entire UI. Other libraries include Room for the local SQLite database, Jetpack DataStore (Preferences) for user settings, Navigation Compose for screen routing, Kotlin coroutines and Flow for asynchronous work and the timer loop, and Android's SoundPool for low-latency audio playback. The trainer voice clips were generated with ElevenLabs and the whistle effect is a royalty-free sound from Pixabay.

# Useful Websites

* [Android Basics with Compose (official Google course)](https://developer.android.com/courses/android-basics-compose/course)
* [Jetpack Compose Pathway](https://developer.android.com/courses/pathways/compose)
* [Room database documentation](https://developer.android.com/training/data-storage/room)
* [DataStore documentation](https://developer.android.com/topic/libraries/architecture/datastore)
* [Material 3 components for Compose](https://developer.android.com/jetpack/compose/designsystems/material3)
* [Kotlin Coroutines guide](https://kotlinlang.org/docs/coroutines-guide.html)
* [ElevenLabs Text-to-Speech](https://elevenlabs.io/)

# Future Work

* Add weekly and monthly stat charts on the Home screen using a charting library
* Support custom audio cues uploaded by the user
* Add a "rest preset" mode so users can mix different work and rest times in a single session
* Add Wear OS support so the timer can run from a smartwatch
* Add a quick-start widget for the home screen launcher
