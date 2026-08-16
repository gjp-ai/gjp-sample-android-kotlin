# Firebase integration

This Android app is connected to the `gjp-lab` Firebase project for Analytics, Remote Config, Crashlytics, Performance Monitoring, and Cloud Messaging.

The Android application ID is `com.ganjianping.lab.ak`.

The current application-level integration performs these actions:

- logs an Analytics `app_started` event;
- records the app version in Crashlytics as `app_version`;
- configures Remote Config with a safe `gjp_lab_maintenance_enabled = false` default;
- fetches the maintenance flag after the splash flow reaches `MainActivity`;
- shows a maintenance page after launch when `gjp_lab_maintenance_enabled` is `true`;
- retrieves FCM registration tokens and can subscribe to the `gjp_lab_demo` topic.

The Firebase feature screen also provides safe demonstrations for a non-fatal Crashlytics exception and a custom Performance trace.

## Common Firebase setup

Firebase uses the configuration file at `app/google-services.json`. The file contains the Firebase project and Android app identifiers for this package.

The Google Services Gradle plugin reads that file during the build and generates the Firebase resources required by the SDKs.

Firebase library versions are managed with the Firebase Android BoM. Individual Firebase dependencies therefore do not declare their own versions.

The versions currently used by this project are:

| Component | Version |
| --- | --- |
| Firebase Android BoM | `34.16.0` |
| Google Services Gradle plugin | `4.5.0` |

### Gradle files

The root `build.gradle.kts` declares the project-level plugins:

```kotlin
plugins {
    alias(libs.plugins.google.services) apply false
}
```

The app module applies the Google Services plugin and imports the BoM:

```kotlin
plugins {
    alias(libs.plugins.google.services)
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:<version>"))
}
```

The actual plugin and library versions are centralized in `gradle/libs.versions.toml`.

The version-catalog aliases are equivalent to declaring the Google Services plugin inline:

```kotlin
id("com.google.gms.google-services") version "4.5.0" apply false
```

This is a Gradle plugin declaration, not an `implementation` dependency. The plugin processes `app/google-services.json`; the Firebase SDKs are added separately through the BoM and the library aliases.

Firebase SDK objects and `FirebaseIntegration` are registered as Koin singletons in `integration/firebase/FirebaseModule.kt`. The application starts Koin before requesting the `FirebaseIntegration` instance.

## Firebase Analytics

Firebase Analytics collects app usage data and supports custom events with parameters. Crashlytics can also use Analytics breadcrumbs to provide more context around crashes.

### Dependency

In `app/build.gradle.kts`:

```kotlin
implementation(libs.firebase.analytics)
```

The catalog maps this alias to `com.google.firebase:firebase-analytics`.

### Initialization and startup event

Firebase SDK initialization is provided automatically by the Firebase startup provider. `GJPLabApplication.kt` starts Koin and initializes the injected `FirebaseIntegration` instance, which records the custom `app_started` event:

```kotlin
get<FirebaseIntegration>().initialize()
```

The integration class receives `FirebaseAnalytics`, `FirebaseCrashlytics`, and `FirebaseRemoteConfig` through its constructor. This keeps Firebase SDK access in the `integration/firebase` package and makes the integration replaceable in tests.

### Custom event sample

Use a small, stable event name and pass values as parameters:

```kotlin
val analytics = FirebaseAnalytics.getInstance(context)
val params = Bundle().apply {
    putString("feature", "device_info")
}
analytics.logEvent("feature_opened", params)
```

Imports:

```kotlin
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
```

Avoid putting personally identifiable information into event names or parameters. Event names and parameter names should follow the Firebase Analytics naming rules.

## Firebase Remote Config

Remote Config lets the app change behavior or values from the Firebase console without requiring a new APK release. This project uses it to control the maintenance page shown after launch.

### Dependency

In `app/build.gradle.kts`:

```kotlin
implementation(libs.firebase.config)
```

The catalog maps this alias to `com.google.firebase:firebase-config`.

### Defaults and fetch

`FirebaseIntegration.initialize()` configures a local fallback and sets the fetch interval:

```kotlin
remoteConfig.setConfigSettingsAsync(
    FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else 3600)
        .build()
)
remoteConfig.setDefaultsAsync(
    mapOf("gjp_lab_maintenance_enabled" to false)
)
```

`FirebaseIntegration.fetchMaintenanceMode()` fetches and reads the value after activation completes:

```kotlin
firebaseIntegration.fetchMaintenanceMode { enabled ->
    // Show the maintenance page when enabled.
}
```

`MainActivity` calls `fetchAndActivate()` after the splash flow reaches the main screen. It reads `gjp_lab_maintenance_enabled` after the fetch completes; while the result is loading, the branded splash screen remains visible. If the value is `true`, the app shows `MaintenanceScreen`; otherwise it shows the normal feature dashboard. The default is `false`, so a fetch failure fails open to the dashboard. Configure the matching `gjp_lab_maintenance_enabled` parameter in the Firebase Console before enabling maintenance mode.

## Firebase Crashlytics

Crashlytics reports crashes, non-fatal exceptions, and ANRs in the Firebase Console. The Crashlytics Gradle plugin also handles mapping-file processing for obfuscated release builds.

This project uses Crashlytics Gradle plugin version `3.0.7`.

### Plugin and dependency

Declare the Crashlytics Gradle plugin at the project level and apply it in the app module:

Root `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.firebase.crashlytics) apply false
}
```

App `app/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.firebase.crashlytics)
}
```

Add the Crashlytics SDK through the Firebase BoM:

```kotlin
implementation(libs.firebase.crashlytics)
```

The app records its version as Crashlytics metadata in `FirebaseIntegration.kt`, using the injected Crashlytics instance:

```kotlin
crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
```

`buildConfig = true` is enabled in `app/build.gradle.kts` so `BuildConfig.VERSION_NAME` is available.

The application sets the `app_version` custom key. The Firebase feature screen can also record a non-fatal demo exception and a non-sensitive demo log.

### Non-fatal exception sample

```kotlin
try {
    loadImportantData()
} catch (exception: Exception) {
    FirebaseCrashlytics.getInstance().recordException(exception)
}
```

Use custom keys and non-sensitive logs to add debugging context:

```kotlin
val crashlytics = FirebaseCrashlytics.getInstance()
crashlytics.setCustomKey("feature", "http_url_connection")
crashlytics.log("Request started")
```

Do not add passwords, tokens, raw user identifiers, or other sensitive data to Crashlytics keys, logs, or exception messages.

### Verification

Use a test-only crash to validate the setup, then remove or guard it before production:

```kotlin
FirebaseCrashlytics.getInstance().log("Crashlytics test")
error("Crashlytics test crash")
```

Crash reports can take time to appear after the app is restarted and network connectivity is available.

## Firebase Performance Monitoring

Performance Monitoring automatically collects app-start and screen-rendering metrics. Its Gradle plugin instruments supported network requests and enables custom trace instrumentation.

This project uses Performance Monitoring Gradle plugin version `2.0.2`.

### Plugin and dependency

Declare the Performance Monitoring Gradle plugin at the project level and apply it in the app module:

Root `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.firebase.performance) apply false
}
```

App `app/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.firebase.performance)
}
```

Add the Performance Monitoring SDK through the Firebase BoM:

```kotlin
implementation(libs.firebase.perf)
```

No manual initialization is required for the automatic metrics.

The Firebase feature screen runs a short `firebase_demo_trace` custom trace. The existing `HttpURLConnection` feature remains eligible for supported automatic network monitoring when the Performance plugin is active.

### Custom trace sample

```kotlin
val trace = FirebasePerformance.getInstance()
    .newTrace("load_device_info")
trace.start()
try {
    loadDeviceInfo()
} finally {
    trace.stop()
}
```

Import:

```kotlin
import com.google.firebase.perf.FirebasePerformance
```

The existing HTTP URL connection code can be monitored automatically by the Performance Monitoring plugin. Review the Performance dashboard after deploying a build to a test device.

## Firebase Cloud Messaging

Firebase Cloud Messaging (FCM) delivers notification and data messages to the app. This project registers `GJPLabFirebaseMessagingService` under `integration/firebase` to receive token refreshes and foreground messages.

### Dependency

In `app/build.gradle.kts`:

```kotlin
implementation(libs.firebase.messaging)
```

The dependency is versioned by the Firebase Android BoM.

### Token and topic demo

The Firebase feature screen uses the injected `FirebaseIntegration` class to:

- retrieve the current FCM registration token;
- subscribe to the `gjp_lab_demo` topic;
- display a shortened token or operation status.

The app requests `POST_NOTIFICATIONS` permission when the Firebase feature screen opens on Android 13 and newer. The token can still be retrieved when notification permission is denied, but notifications will not be shown.

### Message handling

`GJPLabFirebaseMessagingService` creates the notification channel `gjp_lab_firebase_messages` and displays foreground messages using the notification title/body or equivalent `title`/`body` data fields. Tapping the notification opens `MainActivity`.

For testing, send a notification or data message from the Firebase Console to the app token or the `gjp_lab_demo` topic. Server-side message sending should use Firebase Admin SDK or FCM HTTP v1 with credentials kept outside the mobile app.

## Complete file change map

These are all project files changed for the Firebase integration:

| File | Purpose |
| --- | --- |
| `app/google-services.json` | Firebase project and Android app configuration for `com.ganjianping.lab.ak`. |
| `build.gradle.kts` | Declares the Google Services, Crashlytics, and Performance project-level plugins. |
| `app/build.gradle.kts` | Applies Firebase plugins, enables BuildConfig generation, imports the Firebase BoM, and adds Analytics, Remote Config, Crashlytics, Performance, and Messaging dependencies. |
| `gradle/libs.versions.toml` | Centralizes Firebase BoM, Gradle plugin, library, and version-catalog aliases. |
| `app/src/main/java/com/ganjianping/lab/ak/di/AppModule.kt` | Includes the Firebase Koin module with the app’s other dependency modules. |
| `app/src/main/java/com/ganjianping/lab/ak/GJPLabApplication.kt` | Starts the app-level Firebase integration during application startup. |
| `app/src/main/java/com/ganjianping/lab/ak/integration/firebase/FirebaseIntegration.kt` | Encapsulates Firebase Analytics, Crashlytics, Remote Config setup, and maintenance-flag fetching. |
| `app/src/main/java/com/ganjianping/lab/ak/integration/firebase/FirebaseModule.kt` | Registers Firebase SDK instances and `FirebaseIntegration` as Koin singletons. |
| `app/src/main/java/com/ganjianping/lab/ak/integration/firebase/FirebaseConstants.kt` | Centralizes Firebase event names, Crashlytics keys, and Remote Config keys. |
| `app/src/main/java/com/ganjianping/lab/ak/integration/firebase/GJPLabFirebaseMessagingService.kt` | Receives FCM token updates and messages, and displays foreground notifications. |
| `app/src/main/java/com/ganjianping/lab/ak/MainActivity.kt` | Fetches the maintenance flag after launch and selects the maintenance or dashboard screen. |
| `app/src/main/java/com/ganjianping/lab/ak/MaintenanceScreen.kt` | Displays the maintenance message and retry action. |
| `app/src/main/java/com/ganjianping/lab/ak/features/firebase/FirebaseFeatureActivity.kt` | Hosts the Firebase integration demonstration screen. |
| `app/src/main/java/com/ganjianping/lab/ak/features/firebase/FirebaseFeatureScreen.kt` | Provides actions for Analytics, Crashlytics, Remote Config, Performance Monitoring, and Cloud Messaging. |
| `doc/FIREBASE_INTEGRATION.md` | Documents the integration and usage examples. |

## Verification commands

Process the Firebase configuration:

```bash
./gradlew :app:processDebugGoogleServices
```

Build the debug APK with all Firebase plugins enabled:

```bash
./gradlew :app:assembleDebug
```

Run the local unit tests as well:

```bash
./gradlew :app:test
```

The generated build should include Firebase initialization components in the merged manifest, Crashlytics processing tasks, Performance instrumentation, and generated resources such as `google_app_id`. The Google Services task also validates that the JSON client package matches `com.ganjianping.lab.ak`.

## Maintaining the integration

If the Firebase Android app or project changes, download the new `google-services.json` from the Firebase Console and replace `app/google-services.json`. Confirm that its `package_name` remains `com.ganjianping.lab.ak`.

The client configuration contains app identifiers and an API key intended for client applications. It is not a replacement for Firebase Authentication, Firestore, Storage, Remote Config, or server-side security rules.

Useful official documentation:

- [Firebase Android setup](https://firebase.google.com/docs/android/setup)
- [Firebase Analytics for Android](https://firebase.google.com/docs/analytics/get-started)
- [Firebase Remote Config for Android](https://firebase.google.com/docs/remote-config/get-started)
- [Firebase Crashlytics for Android](https://firebase.google.com/docs/crashlytics/android/get-started)
- [Firebase Performance Monitoring for Android](https://firebase.google.com/docs/perf-mon/get-started-android)
