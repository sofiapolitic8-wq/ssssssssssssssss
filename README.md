# DAVID.IO Android source

This is a source-level rebrand and repair workspace for an Android snake-arena game. The Android namespace and application ID are `io.david.mobile`; the launcher label is `DAVID.IO`.

## Current status

- Native JNI exported names were aligned with `io.david.mobile.DavidActivity`.
- Team creation/roster/chat relay code and `server/server.js` are included. The server's default public URL is `https://lwkmod-team-server.onrender.com`; service uptime is external and must be verified.
- Core gameplay, skin editing, arena selection, controls, and performance settings are represented in the source tree.
- This archive has **not** been compiled into a verified APK or tested on physical devices in this environment. Do not describe it as fully secure or release-ready yet.
- Some historical build tools and vendored components may retain upstream identifiers for provenance or generated asset compatibility. Required copyright, GPL, and third-party notices are preserved.

## Build prerequisites

Use JDK 17, Android SDK 36, Android NDK `28.2.13676358`, CMake `3.22.1`, Vulkan/Slang shader compiler tooling, and network access for Gradle dependencies. Generate shaders with `python build.py` where required, then build a debug APK from `android/` using the included Gradle wrapper. Review `HANDOFF.txt` and `SECURITY_RELEASE_AUDIT.md` before attempting a release.

## Legal and assets

Read `LICENSE`, `NOTICE`, and component-level licenses. GPL-covered distribution requires compliance with its applicable source and notice obligations. Some textures/artwork originate from third-party material; verify redistribution rights before public release.

## Team server

Deploy `server/` with Node.js 16 or newer. Configure the app and every teammate to use the same HTTPS server URL. Do not treat a successful local simulation as proof that two physical phones can connect.

---

## Architecture

```text
Jetpack Compose interface (Kotlin)       Android activity, updater, backup (Java)
                 │                                   │
                 └──────── JNI mailboxes and callbacks ────────┘
                                   │
                    C engine: game, protocol, bot, UI overlay
                                   │
                  SDL3 (window, input) · Thermite · Vulkan
```

The app is portrait; the lobby and the arena are landscape. Compose never
touches engine state directly: requests go through mutex-guarded mailboxes that
are drained on the engine thread.

## Repository layout

| Path | Contents |
|---|---|
| `app/src/` | C engine: gameplay, slither protocol, renderer glue, touch controls, JNI bridges (`platform/`) |
| `app/res/` | Fonts, textures (including the arrow atlas) and shader sources, packaged as assets |
| `android/` | The Gradle project; Kotlin/Java under `android/app/src/main/java/io/david/mobile/` |
| `android-sdl/` | Vendored SDL3 |
| `thermite/` | The Vulkan rendering framework |
| `glfw/` | The desktop window/input dependency, kept for upstream compatibility |
| `tools/`, `scripts/`, `tests/` | Asset tools, the build script, the release script and protocol tests |
| `update/` | The signed update manifests read by installed apps |

## Building

Requirements:
- JDK 17 (the Android Studio JBR)
- Android SDK 36
- NDK `28.2.13676358`
- CMake `3.22.1`
- an ARM64 Android 8.0+ device with Vulkan

1. Generate the SPIR-V shaders with `python build.py` (it needs `slangc`). The
   compiled `.spv` files are not committed.
2. Add your own Firebase `android/app/google-services.json`. It is not published.
3. Build:

   ```powershell
   cd android
   .\gradlew.bat :app:assembleDebug "-PDAVID_VERSION_CODE=628" "-PDAVID_VERSION_NAME=6.2.8"
   ```

Optional Gradle properties: `DAVID_API_URL`, `GOOGLE_WEB_CLIENT_ID`,
`DAVID_VERSION_CODE`, `DAVID_VERSION_NAME`. The version reaches the C engine
through a generated header (`android/app/.cxx-version/david_version.h`), so a
new version does not rebuild the whole engine.

The first native build compiles the whole engine and takes several minutes.

Release signing reads `release-signing/keystore.properties`, which is private
and not in this repository. Debug builds cannot replace official builds.

## Security

For security issues, do not include passwords, private keys, invite authentication keys, or personal data in public reports. Use a private channel controlled by the project owner once one has been verified.

No signing keys, keystores, passwords, service credentials or player data are
stored here. The only key file is the updater's public verification key. Team
credentials and sessions live in the Android Keystore on the device.

## License

GNU GPL v3; see `LICENSE` and `NOTICE`. Third-party components keep their own
licenses in their folders. Slither.io names, artwork and trademarks belong to
their owners. David is independent and not affiliated with the game's developer.
