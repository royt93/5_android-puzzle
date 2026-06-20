# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Debug build (compile check only)
./gradlew assembleDebug

# Install on ZFlip5 only (NEVER use Pixel 7 Pro or S24 Ultra for testing)
./gradlew installDebug -Pandroid.serial=R5CX626GNXL

# JVM unit tests (no device needed, fast)
./gradlew testDebugUnitTest

# Instrumented tests on ZFlip5
./gradlew connectedDebugAndroidTest -Pandroid.serial=R5CX626GNXL

# Run a single instrumented test class
./gradlew connectedDebugAndroidTest -Pandroid.serial=R5CX626GNXL \
  -Pandroid.testInstrumentationRunnerArguments.class=com.helpmepls.slidepuzzle.GameModeIntegrationTest

# Lint
./gradlew lintDebug

# Release build requires ADMOB_BANNER_ID, ADMOB_INTERSTITIAL_ID, ADMOB_APP_OPEN_ID in local.properties
# → guarded by afterEvaluate task; will throw GradleException with placeholder values
```

## Device Policy

- **Test device: Samsung Galaxy Z Flip 5** (serial `R5CX626GNXL`) — **only device allowed**
- **NEVER** touch Pixel 7 Pro (`2B051FDH3006MU`) or S24 Ultra (`SM-S928B`)
- Always ask the user before installing to a device (`./gradlew installDebug`)
- If multiple displays warning appears in `adb screencap`, it is cosmetic — ignore it

## Architecture

### Screen Flow

```
SplashActivity → BoardOptionsAct (host)
                    ├── BoardOptionsFrm       (image grid + game mode selector)
                    └── BoardSizeSpinnerFrm   (toolbar spinner)
                         ↓ Intent + extras
                    GameAct                   (puzzle gameplay)
                    SettingsActivity          (preferences)
```

### GameAct Intent Contract

`GameAct` is launched with these extras (all defined as constants in `GameAct.Companion`):

| Extra | Type | Notes |
|---|---|---|
| `EXTRA_IMAGE_RES_ID` | Int | `0` = gallery slot (requires `EXTRA_CUSTOM_IMAGE_PATH`) |
| `EXTRA_BOARD_WIDTH` | Int | 3–8 |
| `EXTRA_BOARD_HEIGHT` | Int | 3–8 |
| `EXTRA_CUSTOM_IMAGE_PATH` | String? | absolute path to cached gallery image |
| `EXTRA_MOVE_BUDGET` | Int | `-1` = Classic; `>0` = Move Challenge |
| `EXTRA_TIME_LIMIT_SECONDS` | Int | `-1` = Classic; `90/180/300` = Time Attack |

### Game Engine

`GameBoard` (custom `View`) owns all rendering and touch handling. It delegates state to `PuzzleGrid`:

- **`PuzzleGrid`** — 2D array of `PuzzleDescriptor` (just an `index`). Handles shuffle (100 random valid moves), solved check, move validation.
- **`SlidingPuzzleState`** — pure-Kotlin mirror of `PuzzleGrid` used only in JVM unit tests (no Android dependency).
- **`GameBoard.onMoveListener: ((moves: Int, solved: Boolean) -> Unit)`** — the single callback `GameAct` uses to react to all tile events (start timer, update UI, trigger win dialog).

Tile rendering: `canvas.drawBitmap(sourceBitmap, srcRect, dstRect, paint)` — no bitmap slicing, no per-tile bitmap allocation.

### Image Data Flow

```
BoardOptionsVm.PREDEFINED_IMAGES  →  ImageCardsAdt  →  Glide (load by resId)
                                                     →  bindGallerySlot (CENTER_INSIDE, FIT_XY reset on recycle)
BoardOptionsFrm (gallery picker)  →  crop + save to cacheDir/custom_puzzle.jpg
GameAct.decodeBoardBitmap()       →  decodes resId or cacheFile  →  BoardOptionsVm.boardImage
```

`GALLERY_SLOT_RES_ID = 0` is a marker, not a real drawable. It must always be the **first** entry in `PREDEFINED_IMAGES`.

### Visual System

All colors defined as tokens in `res/values/colors.xml`; never hard-code hex. Kotlin constants mirror in `NeonPalette.kt`.

Glow techniques by cost (cheapest first):
1. `android:shadowLayer` on TextView — used for all text glow
2. Layered stroke (4 passes, decreasing alpha) in `GameBoard.onDraw` — tile glow
3. `NeonGlow.apply(view, accentColor)` — elevation shadow tint (API 28+, no-op below)
4. `NeonBlur` / `RenderEffect.createBlurEffect` — dialog backdrop blur (API 31+ only)

FX toggles (read from `Prefs`): `fx_quality` (high/low), `fx_blur`, `fx_reduce_motion`. All heavy effects must check these before running and degrade gracefully to the layered-stroke baseline.

Accent theme (cyan/magenta/lime/violet) is stored in `Prefs.ACCENT_THEME`. `GameBoard.accentColor` is lazy-read once at draw time; changing theme requires `recreate()` on the Activity.

### Preferences

Single `SharedPreferences` file: `puzzle_prefs` (also `ScoreUtils.PREFS_NAME`).

High-score keys are per-puzzle: `best_{resId}_{w}x{h}` and `besttime_{resId}_{w}x{h}`.

### Test Architecture

- **JVM unit tests** (`src/test`): pure Kotlin, no Android. `GameStateTest`, `WinCelebrationLogicTest`, `ScoreUtilsTest`, `GameModeUnitTest`, etc.
- **Widget tests** (`src/androidTest`): launch `GameAct` directly via `ActivityScenario` + extras. Check view visibility/content.
- **Integration tests** (`src/androidTest`): full flow assertions.

When writing instrumented tests that launch `GameAct`, always skip the gallery slot:
```kotlin
BoardOptionsVm.PREDEFINED_IMAGES.first { it.first != BoardOptionsVm.GALLERY_SLOT_RES_ID }.first
```

### Key Invariants

- `BoardSizeSpinnerFrm.onDestroyView()` must null `sizeSpinner` and clear `onItemSelectedListener` (leak fix).
- `GameBoard` tile touch fires only on `ACTION_UP`; never allocate in `onDraw`.
- Dialog callbacks (`DialogUtils.showGameDialog`) are guarded against `isFinishing`/`isDestroyed`.
- Portrait-only (`screenOrientation="portrait"`) is intentional — board is square, UI is vertical.
- Release build blocks if AdMob IDs are placeholders (`~`); debug build uses Google test IDs automatically.

## Task Backlog

See `doc/tasks.md` for full kanban. Pending items in `doc/task/todo/`:
- **23** Time Attack mode, **24** Daily Puzzle (P1)
- **29** Achievement badges, **31** Custom tile shape (P2)
- **28** Unlock image packs — needs new image content first (P3)
