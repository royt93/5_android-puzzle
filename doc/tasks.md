# Task Tracker

Cap nhat: 2026-06-18

## In progress

- Khong co.

## Todo

- [todo] Don unused resources theo dot rieng sau khi test UI tren thiet bi.

## Done

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
