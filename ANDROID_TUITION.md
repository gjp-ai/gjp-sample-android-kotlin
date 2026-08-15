# Android Development Tuition: GJPS Feature Lab

This repository is the teaching project for a practical Android development course. Students learn by reading, running, changing, and testing the existing feature lab.

## Course outcome

By the end of the course, a student should be able to:

- build and run a modern Kotlin Android app;
- create screens with Jetpack Compose and Material 3;
- move between screens with Activities and Intents;
- model UI state and handle loading, success, and error states;
- call Android platform APIs safely;
- perform network work off the main thread with coroutines;
- separate UI, feature, and data responsibilities;
- write unit and instrumented UI tests;
- extend the app with a complete feature independently.

## How to use the course

Each lesson has four passes: **observe** the running app, **explain** the event path, **modify** the project, and **prove** the result with checks. Recommended rhythm: one 60–90 minute lesson and one 30–60 minute practice task per week.

Use Android Studio, an emulator or Android 11+ device, and the Gradle wrapper included in this repository.

## Project map

| Area | Entry point | Teaching focus |
|---|---|---|
| App launch | `app/src/main/java/com/ganjianping/ak/sample/SplashActivity.kt` | Activity lifecycle and navigation start |
| Dashboard | `MainActivity.kt`, `MainScreen.kt` | Compose, lists, callbacks, Material 3 |
| Device feature | `features/deviceinfo/` | Platform APIs, repository boundary, rendering data |
| HTTP feature | `features/httpurlconnection/` | Forms, coroutines, network I/O, failures |
| Response feature | `HttpResponseActivity.kt`, `HttpResponseScreen.kt` | Intent extras and formatted output |
| Shared styling | `common/theme/` | Theme, colors, typography |
| Verification | `app/src/test/`, `app/src/androidTest/` | Unit and UI testing |

## Learning path

### Lesson 1 — Read and run an Android project

**Study:** `settings.gradle.kts`, root `build.gradle.kts`, `app/build.gradle.kts`, and `AndroidManifest.xml`.

**Learn:** modules, plugins, SDK levels, dependencies, permissions, exported Activities, and the launcher Activity.

**Exercise:** change the application label and add a short description to the dashboard. Locate the INTERNET permission and explain why it is required.

**Proof:** the app launches from the splash screen, reaches the dashboard, and both feature cards are visible.

### Lesson 2 — Kotlin needed for Android

**Study:** enums and data classes in `MainScreen.kt` and `HttpURLConnectionRepository.kt`; nullable values such as `String?`; `apply`, `let`, and `when`.

**Learn:** immutable values, functions, branching, collections, and exception handling.

**Exercise:** write a pure Kotlin function that maps an HTTP status code to `SUCCESS`, `CLIENT_ERROR`, or `SERVER_ERROR`.

**Proof:** the function has tests for 200, 404, and 500 without needing an Android device.

### Lesson 3 — Compose UI and Material 3

**Study:** `MainScreen.kt`, `DeviceInfoScreen.kt`, and `common/theme/`.

**Learn:** composable functions, `Modifier`, `Scaffold`, layout containers, lazy grids, and Material components.

**Exercise:** add a third disabled “Coming soon” card, then extract `FeatureCard` into a reusable component with a preview.

**Proof:** the screen remains readable on a small emulator and the card has a clear enabled/disabled appearance.

### Lesson 4 — Events, Activities, and Intents

**Study:** the `FeatureAction` enum, `MainActivity` callback, manifest declarations, and `HttpResponseActivity.start`.

**Learn:** one-way event flow, Activity boundaries, explicit Intents, extras, and finishing the current screen.

**Exercise:** add a “refresh device information” action. Pass data through an explicit Intent only when a new Activity is genuinely useful.

**Proof:** back navigation returns to the expected screen and no Activity is accidentally exported.

### Lesson 5 — State and user interaction

**Study:** `remember`, `mutableStateOf`, `isLoading`, `errorMessage`, and `HttpErrorBanner`.

**Learn:** state ownership, recomposition, input validation, disabled buttons, and transient errors.

**Exercise:** validate the URL before sending. Show a friendly message for a blank URL and for a URL without `http://` or `https://`.

**Proof:** sending is impossible while loading; the error is visible without crashing the app.

### Lesson 6 — Coroutines, repositories, and network work

**Study:** `HttpURLConnectionRepository.execute` and `withContext(Dispatchers.IO)`.

**Learn:** suspend functions, main-thread safety, timeouts, response streams, status codes, headers, cleanup, and TLS failures.

**Exercise:** add a request header field to the UI and pass it to the repository. Ensure the connection is always disconnected and the error stream is read for non-2xx responses.

**Proof:** a successful request shows status, body, and headers; a failed request shows a useful error and returns the UI to an actionable state.

### Lesson 7 — Test the behavior

**Study:** `ExampleUnitTest.kt` and `ExampleInstrumentedTest.kt`.

**Learn:** test pure parts first, then Compose semantics and Activity behavior. Avoid making tests depend on a live internet service.

**Exercise:** extract `prettyJson` or status classification into a testable pure component. Add tests for JSON objects, JSON arrays, plain text, malformed JSON, and empty input.

**Proof:** `./gradlew test` passes. Add one Compose test that finds the dashboard title and one feature card by text.

### Lesson 8 — Capstone: add a complete feature

Choose one: app settings, request history, device export with the Android Sharesheet, or a multi-endpoint health check.

The capstone must include a feature model, a Compose screen, an event path from the dashboard, clear loading/error/empty states where relevant, and at least two tests.

## Teaching checkpoints

After Lesson 3, the student should be able to build a static screen from a sketch. After Lesson 6, they should be able to explain why network work cannot run on the main thread. After Lesson 7, they should be able to test behavior without relying on a real server. After Lesson 8, they should be able to add a feature without copying an entire existing screen.

## Suggested next refactors

The sample intentionally keeps the code small. When the student is ready for production-scale patterns, refactor in this order:

1. move screen state and request execution into a `ViewModel`;
2. replace Activity-to-Activity navigation with a navigation graph;
3. represent UI state with a sealed class or data class;
4. inject repositories so network behavior can be replaced in tests;
5. replace `HttpURLConnection` with a maintained HTTP client when the goal is application development rather than learning the platform API;
6. add accessibility labels, retry actions, and configuration-change tests.

## Completion rubric

| Skill | Beginning | Ready |
|---|---|---|
| Compose | Can copy a layout | Can design a screen and lift reusable components |
| Kotlin | Can follow syntax | Can model data and nullability clearly |
| Android | Can launch an Activity | Can choose boundaries and pass data safely |
| Async work | Can make a request | Can handle loading, failure, cancellation, and cleanup |
| Testing | Has a smoke test | Tests behavior deterministically |
| Debugging | Reads the error message | Traces the event path and isolates the failing layer |

## Useful commands

```bash
./gradlew assembleDebug
./gradlew test
./gradlew connectedDebugAndroidTest
```

If a connected device is unavailable, use the emulator and run the first two commands as the baseline checks.
