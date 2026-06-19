# Task Tracker

Cap nhat: 2026-06-19

## In progress

- Khong co.

## Todo

- [todo] (Tuy chon) Nang Material/Glide len ban moi hon va test ky tren thiet bi.

## Done

- [done] Don unused resources: xoa 21 file anim + 4 drawable thua; bo color/dimen khong dung trong colors.xml/dimens.xml. `lintDebug` + `testDebugUnitTest` PASS.
- [done] BitmapTile: xoa han class, render bang `src Rect` tu anh nguon trong `GameBoard` (`PuzzleDescriptor` chi con `index`). Het cap phat bitmap con.
- [done] Them PuzzleGridInstrumentedTest + TileNumbersToggleTest; 6/6 instrumented PASS tren OPPO CPH2577 + Pixel 7 Pro (animation scale = 0). Luu y: AppFlowIntegrationTest treo tren Redmi do Espresso cho idle voi animation bat - khong phai bug app.
- [done] DialogUtils guard: bo qua khi Activity dang `isFinishing`/`isDestroyed` de tranh crash callback async.
- [done] Feature "Show Tile Numbers": toggle hien/an so thu tu qua menu game, persist `show_numbers` trong SharedPreferences.
- [done] Nang version: Gradle wrapper 8.13 -> 8.14.5, align Kotlin plugin 2.0.20 -> 2.1.20 (khop stdlib).
- [done] Fix font scale: ep Activity context dung `fontScale = 1.0f` qua `BaseActivity`.
- [done] Tao audit tong hop tai `doc/audit.md`.
- [done] Fix lint blocker `UseAppTint` trong `act_game.xml`.
- [done] Doi drawableTop sang `app:drawableTopCompat` trong `frm_board_options.xml`.
- [done] Doi `BoardOptionsAct` va `GameAct` sang `exported=false`.
- [done] Refactor image flow de adapter load drawable resource id thay vi giu 27 `Bitmap`.
- [done] Bo static Bitmap handoff; `GameAct` nhan image id va board size qua Intent.
- [done] Dat default board size trong `BoardOptionsVm`.
- [done] Harden touch handling trong `GameBoard`: chi move tren `ACTION_UP`, goi `performClick()`, guard toa do ngoai board.
- [done] Doi timer format sang `Locale.US`.
- [done] Chuyen `BoardOptionsFrm` tu `onActivityCreated` sang `onViewCreated`.
- [done] Them JUnit va unit test JVM nho cho game state/board size.
- [done] Doi transition name cua image card sang resource id de tranh trung ten khi title rong.
- [done] `./gradlew testDebugUnitTest` PASS va da co test source chay that.
- [done] `./gradlew lintDebug` PASS.
- [done] Cap nhat `doc/audit.md`, `doc/memory_leak.md`, `doc/quick_win.md`.
- [done] Build/cai/launch/smoke test tren Pixel 7 Pro.
- [done] Doc logcat theo PID app va luu ket qua tai `doc/device_test.md`.
- [done] Test font scale tren Pixel 7 Pro voi system `font_scale = 1.8`; app giu UI font gan 100%.
- [done] Bo Glide thumbnail `SIZE_ORIGINAL`, load thumbnail theo kich thuoc card.
- [done] Decode board bitmap co `inSampleSize` theo display width.
- [done] Sua button spring touch: chi fire click tren `ACTION_UP` trong bounds.
- [done] Them core `SlidingPuzzleState` thuan Kotlin va unit test move/shuffle/solved.
- [done] Them Android widget test cho font scale.
- [done] Them Android integration test cho flow options -> game -> shuffle dialog.
- [done] Them Glide compiler va `AppGlideModule` de bo generated module warning.
- [done] Them release guard: fail release build neu AdMob ID con placeholder.
- [done] Don deprecated status bar/transition usage trong code chinh.
- [done] `./gradlew connectedDebugAndroidTest` PASS tren Pixel 7 Pro voi widget test + integration test.
- [done] `./gradlew preReleaseBuild` bi chan dung ky vong khi thieu AdMob ID that.
