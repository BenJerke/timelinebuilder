# Project Overview
The goal of this application is to log events and draw connections between them. These events and connections will be presented to the user on an adjustable timeline interface.

## Potential Use Cases
* Tracking current events as they develop over time
* Understanding historic events
* Creating fictional histories
* Personal journaling


This is a Kotlin Multiplatform project targeting Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
      folder is the appropriate location.

## Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…


## Library Documentation
1. [Koin](https://insert-koin.io/)
2. [Compose Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform.html?_cl=MTsxOzE7VFB5UkFaYlNFall1NEx2V0dQYUphTFRhdE1wTXRzNWxLeDFMVkRYcTltUHEzd0o0YjdCdkxBUjFxNjlyWUFnVzs%3D) 
3. [SQLDelight](https://sqldelight.github.io/sqldelight/2.1.0/)
4. [Material UI](https://developer.android.com/jetpack/androidx/releases/compose-material3?_gl=1*2mik*_ga*MTgzODg4ODg0NS4xNzU3MTU5OTI0*_ga_QPQ2NRV856*czE3NjAyODU0NDgkbzQkZzAkdDE3NjAyODU0NDgkajYwJGwwJGgw)
