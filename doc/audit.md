# Source Audit - 2026-06-18

## Tong quan

- Project: Android app Kotlin, package `com.helpmepls.slidepuzzle`.
- Build config hien tai: AGP `8.12.1`, Gradle wrapper `8.13`, Kotlin plugin `2.0.20`, compile/target SDK `36`.
- Verification da chay:
  - `./gradlew testDebugUnitTest`: PASS, co unit test JVM chay that.
  - `./gradlew lintDebug`: PASS.
  - `./gradlew assembleDebug`: PASS.
  - `./gradlew connectedDebugAndroidTest`: PASS, 2 tests tren Pixel 7 Pro.
  - `./gradlew preReleaseBuild`: FAIL dung ky vong khi thieu AdMob ID that.

## Lint/build status

### Error dang chan lint

- Khong con error chan lint sau khi doi `android:tint` sang `app:tint`.

### Warning / no ky thuat con lai

- Deprecated status bar/transition usage trong code chinh da duoc don. `SplashActivity` dung `overrideActivityTransition` tren API moi va fallback co suppress cho API cu.
- `AndroidManifest.xml`: `screenOrientation="portrait"` bi canh bao voi Android 16+.
  - Ghi chu: Neu game bat buoc portrait thi can chap nhan/cau hinh suppress co chu dich; neu khong, nen test adaptive layout.
- `frm_board_options.xml`: overdraw do root co `android:background="@drawable/bkg"` trong khi theme cung ve background.
- Nhieu `UnusedResources`: animation, drawable, color, dimen, style. Can xoa theo dot nho sau khi build/lint xac nhan khong dung qua reflection/code dong.

## Findings uu tien cao

### 1. Touch handling tren board co the tao nhieu move ngoai y muon

- Status: DONE.
- File: `app/src/main/java/com/helpmepls/slidepuzzle/game/GameBoard.kt`
- Da chi xu ly move tren `ACTION_UP`, goi `performClick()` va guard toa do ngoai board.

### 2. Truyen Bitmap qua static field

- Status: DONE.
- File: `app/src/main/java/com/helpmepls/slidepuzzle/act/GameAct.kt`
- Da bo `BoardActivityParams` static Bitmap handoff. `GameAct` nhan image resource id va board size qua Intent roi decode trong Activity.

### 3. Decode tat ca anh thanh Bitmap ngay khi mo man chon

- Status: DONE.
- File: `app/src/main/java/com/helpmepls/slidepuzzle/frm/BoardOptionsFrm.kt`
- `TitledCardInfo` da giu `@DrawableRes Int`; adapter load resource id bang Glide, khong decode 27 bitmap full-size o man chon.

### 4. Release AdMob id dang la placeholder

- Status: DONE guard, BLOCKED config.
- File: `app/build.gradle`
- Da them guard chan release build neu `ADMOB_BANNER_ID`, `ADMOB_INTERSTITIAL_ID`, `ADMOB_APP_OPEN_ID` thieu hoac con placeholder.
- Viec con lai: dua ID that vao `local.properties`/CI secret truoc khi build production.

### 5. Activity noi bo exported=true

- Status: DONE.
- File: `app/src/main/AndroidManifest.xml`
- `BoardOptionsAct` va `GameAct` da doi sang `android:exported="false"`.

## Findings trung binh

- `saveHighScore(moves, time)` nhan `time` nhung khong dung. Nen luu ca time hoac bo tham so de tranh hieu nham.
- `isNewBest` trong `showWinDialog` tinh truoc khi save, voi lan dau `bestScore=0` se khong bao new best. Nen tinh sau khi doc current best, va luu score theo `(moves, time)` neu muon cong bang.
- Da them JUnit core test cho `SlidingPuzzleState` va Android widget/integration test. Van co the tang them coverage truc tiep cho `PuzzleGrid` neu tach tiep khoi Android `Bitmap`.
- `BitmapTile` tao tile bitmap moi cho moi resize, chua recycle/cache. Nen tranh resize lap lai lien tuc va can nhac cache theo `(imageId, boardSize)`.
- `DialogUtils.showGameDialog` dung `Context` Activity va `setCancelable(false)`. Nen check `isFinishing/isDestroyed` truoc khi show neu goi tu callback bat dong bo, va can nhac cho phep back/cancel.
- `BoardSizeSpinnerFrm` cast `activity as Context`; nen dung `requireContext()` de ro lifecycle va crash message tot hon.

## Tai lieu/ky thuat no

- Da co test source:
  - `app/src/test/java/com/helpmepls/slidepuzzle/game/state/GameStateTest.kt`
  - `app/src/androidTest/java/com/helpmepls/slidepuzzle/WidgetFontScaleTest.kt`
  - `app/src/androidTest/java/com/helpmepls/slidepuzzle/AppFlowIntegrationTest.kt`
- Kotlin daemon bao loi cache/incremental roi fallback compile thanh cong. Neu lap lai, chay `./gradlew --stop` va clean cache build cuc bo.
- `doc/quick_win.md` co mot so muc "[DONE]" nhung can doi chieu bang lint/source truoc khi xem la hoan tat san sang release.

## De xuat thu tu xu ly

1. Xu ly release AdMob placeholder `"~"` truoc khi build production.
2. Don unused resources sau khi lint pass va test man hinh chinh.
3. Neu muon tang coverage tiep, tach logic `PuzzleGrid` khoi Android `Bitmap` de test move/undo/shuffle/reset sau hon.
