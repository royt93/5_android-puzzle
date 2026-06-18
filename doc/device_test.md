# Pixel 7 Pro Device Test - 2026-06-18

## Device

- Model: Pixel 7 Pro (`cheetah`)
- Device id: `2B051FDH3006MU`
- Package: `com.helpmepls.slidepuzzle`
- Build tested: debug APK from `./gradlew assembleDebug`

## Commands / verification

- `./gradlew assembleDebug`: PASS
- `./gradlew lintDebug`: PASS
- `./gradlew testDebugUnitTest`: PASS
- `./gradlew connectedDebugAndroidTest`: PASS, 2 tests on Pixel 7 Pro
- `adb install -r app/build/outputs/apk/debug/app-debug.apk`: PASS
- Launch: `adb shell am start -n com.helpmepls.slidepuzzle/.act.SplashActivity`: PASS
- Foreground after launch: `BoardOptionsAct`
- Smoke flow:
  - Select first image from grid: PASS, navigated to `GameAct`
  - Tap board several times: PASS, no crash
  - Tap Shuffle and confirm dialog: PASS, no crash
  - Tap Undo: PASS, no crash
  - Tap Reset and confirm dialog: PASS, no crash
- Foreground after smoke test: `GameAct`
- App PID after smoke test: `28516`

## Font scale regression test

- Device system font scale during test: `1.8`
- Fix tested: all app activities now inherit `BaseActivity`, which wraps the base context with `Configuration.fontScale = 1.0f`.
- Reinstalled debug APK and relaunched app while device remained at `font_scale = 1.8`.
- `BoardOptionsAct` UI dump showed text bounds no longer inflated by the device font scale:
  - `Quick Puzzle`: `[61,210][541,322]`
  - `4 x 4`: `[1129,225][1257,307]`
- Navigated to `GameAct`: PASS, foreground changed to `GameAct`.
- PID-only app log after navigation: no crash/runtime exception.
- Note: UIAutomator still returns `null root node` on `GameAct`, same as previous smoke test, so GameAct font scale was validated by launch/focus/log rather than hierarchy bounds.

## Logcat result

PID-only log after smoke test:

- No `FATAL EXCEPTION`
- No `ANR`
- No `WindowLeaked`
- No `OutOfMemory`
- No app-side `StrictMode` violation observed
- No LeakCanary leak report observed in the test window
- Observed app log lines are normal runtime noise:
  - dialog back callback set/unset
  - IME hide/cancelled because no editor is focused
  - one background young GC, memory still healthy (`97% free`, heap cap `256MB`)

Initial launch log note:

- `Glide`: missing `GeneratedAppGlideModule` warning was addressed by adding `glide:compiler` and `MyGlideModule`.
- LeakCanary started correctly and watched destroyed `SplashActivity`; no leak was reported during this short test.

## Automated tests added

- Unit test: `GameStateTest` covers solved state, adjacency, move swap, undo-by-moving-back, shuffle-by-valid-moves, and board size label format.
- Widget test: `WidgetFontScaleTest` launches `BoardOptionsAct`, verifies wrapped base context uses `fontScale = 1.0f`, and checks key views are displayed.
- Integration test: `AppFlowIntegrationTest` launches `GameAct` with real Intent extras, verifies board visibility, opens the shuffle dialog, cancels it, and returns to the game controls.

## Release guard

- `./gradlew preReleaseBuild`: intentionally FAILS when `ADMOB_BANNER_ID`, `ADMOB_INTERSTITIAL_ID`, or `ADMOB_APP_OPEN_ID` are missing from `local.properties` or CI properties.
- Current result: FAIL as expected with message `Release build requires ADMOB_BANNER_ID, ADMOB_INTERSTITIAL_ID, and ADMOB_APP_OPEN_ID in local.properties or CI properties.`

## Quality assessment

### Good

- Debug build installs and launches cleanly on a real Pixel 7 Pro.
- Main navigation flow from splash -> options -> game works.
- Device font scale can be large (`1.8`) without inflating app text after the `BaseActivity` fix.
- Recent refactor is validated on device: selecting an image still opens `GameAct`, so Intent-based image id handoff works.
- No crash/runtime exception in tested flows.
- No visible memory pressure in logcat after moving away from eager Bitmap decode on the options screen.
- `lintDebug`, `testDebugUnitTest`, and `connectedDebugAndroidTest` are passing.
- Release builds are protected from shipping placeholder AdMob IDs.
- Glide generated module warning is fixed.

### Still needs work

- Real release AdMob IDs still need to be provided outside source control before production build.
- UIAutomator returned `null root node` on `GameAct`; manual adb taps still worked, but automated UI testing may need Espresso or better view ids/content descriptions.
- Unused resources can be cleaned in a separate UI-safe pass.

## Verdict

Quality after this pass: strong for debug/device validation. The remaining production blocker is external configuration: real release AdMob IDs must be supplied through `local.properties` or CI properties.
