# Daily Routine — Android App

A native Android app (Kotlin + Jetpack Compose, Material 3) implementing the Daily Routine
design: Home (habits/tasks by time-of-day section), Calendar (week view with completion dots),
Stats (streaks, weekly bar chart), Settings, and routine Detail/Add/Edit screens.

Backend: **Firebase** — Firebase Authentication (email/password) for accounts, and Cloud
Firestore for storing each user's routines with realtime sync across devices.

## Project structure

```
DailyRoutineApp/
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json             ← your Firebase config (gitignored, not committed)
│   └── src/main/java/com/dailyroutine/app/
│       ├── DailyRoutineApp.kt          (Application, Firebase init)
│       ├── MainActivity.kt
│       ├── data/
│       │   ├── model/RoutineItem.kt
│       │   └── repository/AuthRepository.kt, RoutineRepository.kt
│       └── ui/
│           ├── theme/                  (colors ported from the design's oklch palette)
│           ├── navigation/RoutineNavGraph.kt
│           ├── components/             (RoutineCard, ProgressRing, BottomNavBar)
│           └── screens/                (auth, home, calendar, stats, settings, detail, form)
├── firestore.rules                     ← per-user data isolation rules
├── build.gradle.kts / settings.gradle.kts
```

## 1. Create the Firebase backend

1. Go to the [Firebase console](https://console.firebase.google.com/) and create a new project.
2. Add an **Android app** to it with package name `com.dailyroutine.app`.
3. Download the generated `google-services.json` and place it at `app/google-services.json`.
4. In the Firebase console, enable:
   - **Authentication → Sign-in method → Email/Password**
   - **Firestore Database → Create database** (start in production mode)
5. Deploy the included security rules so users can only read/write their own routines:
   ```
   firebase deploy --only firestore:rules
   ```
   (or paste the contents of `firestore.rules` into the Firestore console's Rules tab).

Data model in Firestore: `users/{uid}/routines/{routineId}`, each routine document holding
`title, category, type, time, color, streak, bestStreak, history (map of date → bool), createdAt`.

> Note: this project doesn't ship a `gradlew` wrapper binary (it's a jar and can't be authored
> as text). Opening the folder directly in Android Studio works fine — it syncs with its bundled
> Gradle. If you want a `./gradlew` for CLI/CI use, run `gradle wrapper` once from this directory
> with any local Gradle 8.x install.

## 2. Open and run in Android Studio

1. Open the `DailyRoutineApp/` folder in Android Studio (Koala or newer recommended).
2. Let Gradle sync (it will download the Compose BOM, Firebase BOM, Navigation, etc.).
3. Run on an emulator or device (min SDK 26 / Android 8.0+).
4. On first launch you'll see the sign-in screen — tap "No account yet? Sign up" to create
   an account; it's backed by Firebase Auth, so any device/emulator with the same Firebase
   project can sign in to the same account and see the same routines.

## Features implemented

- **Auth**: email/password sign up & sign in via Firebase Auth, session persists across restarts.
- **Home**: today's routines grouped by Morning/Afternoon/Evening/Anytime, a completion ring,
  tap-to-toggle checkboxes, habit streak badges, tap a card to open its detail screen.
- **Calendar**: week strip with a completion-rate dot per day, tap a day to see that day's agenda.
- **Stats**: weekly completion ring, best streak / active habits / perfect days tiles, a 7-day
  bar chart, and a full habit-streak leaderboard.
- **Settings**: profile card (from Firebase Auth), notification/sound toggles, sign out.
- **Add / Edit**: create or edit a routine — title, habit vs task, category, optional time,
  and a teal/orange color tag — persisted straight to Firestore.
- **Detail**: full routine view with a 7-day history strip, streak stats, mark-complete button,
  edit and delete.

All data is realtime: `RoutineRepository.observeRoutines()` uses a Firestore snapshot listener,
so edits made on one device (or directly in the Firestore console) appear instantly everywhere
the account is signed in.

## Notes / next steps if you continue this

- Notification/sound toggles in Settings are currently local UI state only (not wired to
  actual push notifications) — hook them up to `WorkManager` + Firebase Cloud Messaging if you
  want real daily reminders.
- The month-view calendar and per-day agenda editing from the original design were trimmed to
  the week view to keep the first version focused; the data layer already supports adding it.
- App icon is a simple placeholder vector; swap `app/src/main/res/drawable/ic_launcher_*.xml`
  for real branding art.
