# Firebase integration

This Android app is connected to the `gjp-sample` Firebase project for Analytics, Remote Config, Crashlytics, and Performance Monitoring.

The Android application ID is `com.ganjianping.ak.sample`.

## Common Firebase setup

Firebase uses the configuration file at `app/google-services.json`. The file contains the Firebase project and Android app identifiers for this package.

The Google Services Gradle plugin reads that file during the build and generates the Firebase resources required by the SDKs.

Firebase library versions are managed with the Firebase Android BoM. Individual Firebase dependencies therefore do not declare their own versions.

### Gradle files

The root `build.gradle.kts` declares the project-level plugins:

```kotlin
plugins {
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.performance) apply false
}
```

The app module applies the plugins and imports the BoM:

```kotlin
plugins {
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.performance)
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:<version>"))
}
```

The actual plugin and library versions are centralized in `gradle/libs.versions.toml`.

## Firebase Analytics

Firebase Analytics collects app usage data and supports custom events with parameters. Crashlytics can also use Analytics breadcrumbs to provide more context around crashes.

### Dependency

In `app/build.gradle.kts`:

```kotlin
implementation(libs.firebase.analytics)
```

The catalog maps this alias to `com.google.firebase:firebase-analytics`.

### Initialization and startup event

Firebase SDK initialization is provided automatically by the Firebase startup provider. This app records a custom `app_started` event in `GJPSApplication.kt`:

```kotlin
FirebaseAnalytics.getInstance(this)
    .logEvent("app_started", Bundle())
```

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

Remote Config lets the app change behavior or values from the Firebase console without requiring a new APK release. In this project, “Firebase Events Config” is represented by Firebase Remote Config plus Analytics events.

### Dependency

In `app/build.gradle.kts`:

```kotlin
implementation(libs.firebase.config)
```

The catalog maps this alias to `com.google.firebase:firebase-config`.

### Defaults and fetch

`GJPSApplication.kt` configures a local fallback, sets the fetch interval, and starts a fetch:

```kotlin
val remoteConfig = FirebaseRemoteConfig.getInstance()
remoteConfig.setConfigSettingsAsync(
    FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else 3600)
        .build()
)
remoteConfig.setDefaultsAsync(
    mapOf("sample_feature_enabled" to false)
)
remoteConfig.fetchAndActivate()
```

Read a value after fetch and activation completes:

```kotlin
FirebaseRemoteConfig.getInstance()
    .fetchAndActivate()
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val enabled = FirebaseRemoteConfig.getInstance()
                .getBoolean("sample_feature_enabled")
            // Apply the remote value to the UI or feature behavior.
        }
    }
```

Always provide a safe local default. Configure the matching `sample_feature_enabled` parameter in the Firebase Console before relying on a production value.

## Firebase Crashlytics

Crashlytics reports crashes, non-fatal exceptions, and ANRs in the Firebase Console. The Crashlytics Gradle plugin also handles mapping-file processing for obfuscated release builds.

### Plugin and dependency

The project-level plugin is declared in the root `build.gradle.kts`, applied in `app/build.gradle.kts`, and the SDK is added through the BoM:

```kotlin
implementation(libs.firebase.crashlytics)
```

The app records its version as Crashlytics metadata in `GJPSApplication.kt`:

```kotlin
FirebaseCrashlytics.getInstance()
    .setCustomKey("app_version", BuildConfig.VERSION_NAME)
```

`buildConfig = true` is enabled in `app/build.gradle.kts` so `BuildConfig.VERSION_NAME` is available.

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

### Plugin and dependency

The project-level plugin is declared in the root `build.gradle.kts`, applied in `app/build.gradle.kts`, and the SDK is added through the BoM:

```kotlin
implementation(libs.firebase.perf)
```

No manual initialization is required for the automatic metrics.

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

## Complete file change map

These are all project files changed for the Firebase integration:

| File | Purpose |
| --- | --- |
| `app/google-services.json` | Firebase project and Android app configuration for `com.ganjianping.ak.sample`. |
| `build.gradle.kts` | Declares the Google Services, Crashlytics, and Performance project-level plugins. |
| `app/build.gradle.kts` | Applies Firebase plugins, enables BuildConfig generation, imports the Firebase BoM, and adds Analytics, Remote Config, Crashlytics, and Performance dependencies. |
| `gradle/libs.versions.toml` | Centralizes Firebase BoM, Gradle plugin, library, and version-catalog aliases. |
| `app/src/main/java/com/ganjianping/ak/sample/GJPSApplication.kt` | Logs the startup Analytics event, adds the Crashlytics app-version key, and configures Remote Config defaults and fetching. |
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

The generated build should include Firebase initialization components in the merged manifest, Crashlytics processing tasks, Performance instrumentation, and generated resources such as `google_app_id`.

## Maintaining the integration

If the Firebase Android app or project changes, download the new `google-services.json` from the Firebase Console and replace `app/google-services.json`. Confirm that its `package_name` remains `com.ganjianping.ak.sample`.

The client configuration contains app identifiers and an API key intended for client applications. It is not a replacement for Firebase Authentication, Firestore, Storage, Remote Config, or server-side security rules.

Useful official documentation:

- [Firebase Android setup](https://firebase.google.com/docs/android/setup)
- [Firebase Analytics for Android](https://firebase.google.com/docs/analytics/get-started)
- [Firebase Remote Config for Android](https://firebase.google.com/docs/remote-config/get-started)
- [Firebase Crashlytics for Android](https://firebase.google.com/docs/crashlytics/android/get-started)
- [Firebase Performance Monitoring for Android](https://firebase.google.com/docs/perf-mon/get-started-android)
