# WAE Python IDE for Android

**Project:** WE ARE ENGINEERS (WAE) Python IDE  
**Current source version:** 0.2.0  
**Package:** `com.weareengineers.waeide`

This is an Android Studio source project for a Python IDE starter application. It includes an embedded Python runtime through Chaquopy, a WebView-based editor UI, app-private Python file storage, basic autocomplete, source-line error navigation, and an optional Termux command bridge.

## Features in this source

- Dark editor interface with a red **RUN** button.
- Executes Python code inside the Android app through the bundled Python runtime.
- Captures standard output and tracebacks; the runner reports the source line for user-code errors.
- Basic Python keyword/builtin suggestions and Tab indentation.
- Save, list, open and delete `.py` files in the app's private `projects` folder.
- Library Manager screen with a command handoff to Termux.
- Optional Termux `RUN_COMMAND` bridge.
- GitHub Actions workflow to build a debug APK in the cloud.

## Build an APK on GitHub

1. Create a GitHub repository and upload the contents of this folder (the `settings.gradle.kts` file must be at repository root).
2. Open **Actions** and select **Build WAE Python IDE APK**.
3. Run the workflow. It downloads Gradle 8.13 and runs `gradle assembleDebug`.
4. If successful, download the `wae-python-ide-debug-apk` artifact from the workflow run.

The first build can fail if an upstream Android/Gradle/Chaquopy dependency changes or the environment needs an SDK component. Read the build log and fix the first actual error; do not assume a generated APK exists until the workflow succeeds.

## Termux setup

Termux must be installed separately. Enable its external-app command permission and configure `allow-external-apps=true` if required by your Termux build. The command is sent to Termux; this starter does not capture the terminal's stdout/stderr back into the IDE.

## Current limitations (important)

- This is a starter source project, not a verified release APK. It has not been built and tested on a physical Android device in this workspace.
- The editor is a `textarea`-based editor, so it does not yet have full IDE-grade syntax-colored tokens, multi-file tabs, breakpoints, or a step debugger.
- Autocomplete is a simple built-in word list, not an intelligent language server.
- Embedded-runtime package installation is not implemented. The Library Manager sends a pip command to Termux; that installs into Termux's Python environment, not automatically into the embedded Python runtime.
- Files are stored in app-private storage; importing/exporting files through Android's document picker is a future task.
- The Termux bridge requires user-side Termux configuration and may not work on every Termux distribution without adjustments.
- Only `arm64-v8a` is currently configured.

## Tech stack

- Kotlin + Android SDK
- WebView for the editor interface
- Chaquopy embedded Python runtime (Python 3.13 configured)
- GitHub Actions + Gradle for cloud build

## Safety note

Running code executes arbitrary Python inside the app's Python process. Do not run code from untrusted sources. This is not a security sandbox.
