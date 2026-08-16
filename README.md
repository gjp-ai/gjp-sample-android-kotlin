# GJPLab Android Feature Lab (`com.ganjianping.lab.ak`)

GJPLab is a small Kotlin Android app for learning modern Android fundamentals by reading, running, and extending working features. The UI is built with Jetpack Compose and Material 3; the app uses Activities for navigation and Koin for repository injection.

The repository also includes a structured [Android development tuition guide](ANDROID_TUITION.md), with lessons and exercises that build on the code in this project.

## What the app demonstrates

- A three-second Compose splash screen followed by a dashboard.
- Activity-based navigation with explicit Intents.
- Material 3 layouts, cards, grids, scrolling content, and system/dynamic colors.
- Device and Android OS information read through platform APIs.
- Native `HttpURLConnection` requests for `GET`, `POST`, `PUT`, and `DELETE`.
- Coroutine-based network I/O on `Dispatchers.IO`, 15-second connect/read timeouts, response headers, error bodies, and TLS error handling.
- JSON pretty-printing when a response contains a JSON object or array.
- Firebase Analytics, Remote Config, Crashlytics, and Performance Monitoring.
- An interactive Firebase feature screen for testing Analytics events, non-fatal Crashlytics reports, Remote Config, and custom Performance traces.
- Local unit-test and instrumented-test modules ready for extension.

## Requirements

- Android Studio with Android SDK 37 and a JDK 21 runtime (the repository's Gradle toolchain is configured for Java 21; source/target compatibility remains Java 11).
- An Android 11 (API 30) or newer emulator or physical device.
- Network access when using the HTTP feature or Firebase services.

The project includes the Gradle wrapper, so Gradle does not need to be installed separately.

## Run the app

Clone the repository, open it in Android Studio, allow the project to sync, and run the `app` configuration on an Android 11+ device or emulator.

From the project root, the same workflow can be started with:

```bash
./gradlew assembleDebug
```

The debug APK is written under `app/build/outputs/apk/debug/`.

## Explore the features

### OS & hardware

The device screen reads a snapshot from `DeviceInfoRepository` and displays:

- Android release, SDK level, and codename;
- screen dimensions;
- manufacturer, model, device, and hardware;
- available CPU cores, total memory, and supported ABIs.

### HttpURLConnection

Choose an HTTP method, edit the URL, and optionally provide a JSON payload for `POST` or `PUT`. The request repository:

1. opens a native `HttpURLConnection`;
2. sends the request on `Dispatchers.IO`;
3. reads the normal input stream for 2xx responses or the error stream otherwise;
4. formats JSON response bodies when possible;
5. passes the status code, body, and headers to the response screen;
6. always disconnects the connection.

The screen starts with a sample endpoint, but any endpoint must be reachable from the device and accept the selected method. The manifest grants Internet access, while the network security configuration disables cleartext traffic and adds a trusted certificate authority for `ganjianping.com`.

## Project structure

```text
.
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/ganjianping/lab/ak/
│       │   │   ├── common/theme/          # Compose theme, colors, typography
│       │   │   ├── di/                    # Koin module
│       │   │   ├── features/deviceinfo/   # Device information feature
│       │   │   ├── features/httpurlconnection/
│       │   │   ├── MainActivity.kt        # Dashboard host
│       │   │   └── SplashActivity.kt      # Launcher and splash flow
│       │   └── res/                       # Manifest, strings, theme, security, icons
│       ├── test/                          # Local JVM tests
│       └── androidTest/                   # On-device/instrumented tests
├── doc/FIREBASE_INTEGRATION.md            # Firebase setup and usage notes
├── ANDROID_TUITION.md                     # Lesson plan and exercises
└── gradle/libs.versions.toml              # Centralized dependency versions
```

The main event path is:

```text
SplashActivity → MainActivity → feature Activity
                              ├─ DeviceInfoActivity
                              └─ HttpURLConnectionActivity → HttpResponseActivity
```

Repositories are registered as Koin singletons in `di/AppModule.kt` and injected into their feature Activities. Screens receive repositories and callbacks as parameters, keeping the Compose UI separate from Activity navigation and data access.

## Firebase

`GJPLabApplication` starts the `integration/firebase/FirebaseIntegration` layer, which currently:

- logs an `app_started` Analytics event;
- stores the app version as a Crashlytics custom key;
- configures and fetches the `gjp_lab_maintenance_enabled` Remote Config flag;
- shows a maintenance page after launch when `gjp_lab_maintenance_enabled` is `true`;
- includes the Performance Monitoring SDK and Gradle plugin.

Firebase is configured with [`app/google-services.json`](app/google-services.json). This contains client-side project configuration, not server credentials. Restrict the associated API key to the Android package and signing certificates, and protect Firebase resources with Authentication, Security Rules, and App Check. Never commit service-account credentials, server keys, OAuth secrets, or App Check debug tokens.

See [`doc/FIREBASE_INTEGRATION.md`](doc/FIREBASE_INTEGRATION.md) for the complete integration map, verification steps, and examples.

## Verification

Run the local build and unit tests from the project root:

```bash
./gradlew assembleDebug
./gradlew test
```

With an emulator or connected device available, run the instrumented tests:

```bash
./gradlew connectedDebugAndroidTest
```

The checked-in tests are intentionally small starter tests: the JVM test verifies a basic assertion and the instrumented test verifies the application package name. Add deterministic feature and Compose tests as the app is extended; avoid making tests depend on the live sample endpoint.

## Learning path

Start with [`ANDROID_TUITION.md`](ANDROID_TUITION.md). It maps lessons to source files and covers project setup, Kotlin, Compose, Activities and Intents, UI state, coroutines, repositories, testing, and a capstone feature.

When using this project as a production starting point, the guide also identifies useful next refactors, including moving state into a `ViewModel`, introducing a navigation graph, modeling UI state explicitly, injecting replaceable test dependencies, and replacing `HttpURLConnection` with a maintained HTTP client.
