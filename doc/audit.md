# Source Audit - 2026-06-18

## Tong quan

- Project: Android app Kotlin, package `com.helpmepls.slidepuzzle`.
- Build config hien tai: AGP `8.12.1`, Gradle wrapper `8.13`, Kotlin plugin `2.0.20`, compile/target SDK `36`.
- Verification da chay:
  - `./gradlew testDebugUnitTest`: PASS, nhung khong co unit test (`NO-SOURCE`).
  - `./gradlew lintDebug`: FAIL voi 1 error va 121 warnings.

## Lint/build status

### Error dang chan lint

- `app/src/main/res/layout/act_game.xml:155`: `ImageButton` dang dung `android:tint="#FFFFFF"`.
  - Lint issue: `UseAppTint`.
  - Fix de xuat: them namespace `xmlns:app="http://schemas.android.com/apk/res-auto"` neu chua co, doi sang `app:tint="#FFFFFF"`.

### Warning can xu ly som

- `GameAct.kt:82`: `String.format(...)` khong truyen `Locale`.
  - Fix de xuat: `String.format(Locale.US, "...", mins, secs)` hoac `Locale.getDefault()` neu muon format theo locale nguoi dung.
- `BoardOptionsFrm.kt:55`: dung API deprecated `onActivityCreated`.
  - Fix de xuat: chuyen setup view sang `onViewCreated`.
- `SplashActivity.kt:36`: `overridePendingTransition` deprecated.
  - Fix de xuat: dung `overrideActivityTransition` voi API moi, giu fallback cho API cu neu can.
- `GameAct.kt:368`, `GameAct.kt:370`: `statusBarColor` va `FLAG_TRANSLUCENT_STATUS` deprecated.
  - Fix de xuat: chuan hoa edge-to-edge/status bar theo AndroidX `WindowInsetsControllerCompat` va theme.
- `AndroidManifest.xml`: `screenOrientation="portrait"` bi canh bao voi Android 16+.
  - Ghi chu: Neu game bat buoc portrait thi can chap nhan/cau hinh suppress co chu dich; neu khong, nen test adaptive layout.
- `frm_board_options.xml`: `android:drawableTop` nen doi sang `app:drawableTopCompat`.
- `frm_board_options.xml`: overdraw do root co `android:background="@drawable/bkg"` trong khi theme cung ve background.
- Nhieu `UnusedResources`: animation, drawable, color, dimen, style. Can xoa theo dot nho sau khi build/lint xac nhan khong dung qua reflection/code dong.

## Findings uu tien cao

### 1. Touch handling tren board co the tao nhieu move ngoai y muon

- File: `app/src/main/java/com/helpmepls/slidepuzzle/game/GameBoard.kt`
- Hien trang: `setOnTouchListener` goi `onSlide(...)` voi moi `MotionEvent`, khong loc `ACTION_UP`/`ACTION_DOWN`.
- Rủi ro: mot lan cham co the gui nhieu event, de gay move lap, animation bi tranh bang `animator != null` nhung van khong ro rang va co the tao hanh vi kho reproduce.
- Fix de xuat: chi xu ly `ACTION_UP` hoac click gesture hop le, goi `performClick()`, override `performClick()` de thoa accessibility.

### 2. Truyen Bitmap qua static field

- File: `app/src/main/java/com/helpmepls/slidepuzzle/act/GameAct.kt`
- Hien trang: `companion object var initialConfig: BoardActivityParams?` giu `Bitmap` de truyen tu options sang game.
- Diem tot: code da set `initialConfig = null` sau khi lay.
- Rủi ro con lai: neu process/activity bi recreate khong theo duong binh thuong, config co the mat; static bitmap van la vung rui ro memory spike trong khoang chuyen Activity.
- Fix de xuat: truyen resource id/image id qua Intent, decode trong `GameAct` voi kich thuoc can thiet; hoac dung repository/cache co lifecycle ro rang.

### 3. Decode tat ca anh thanh Bitmap ngay khi mo man chon

- File: `app/src/main/java/com/helpmepls/slidepuzzle/frm/BoardOptionsFrm.kt`
- Hien trang: `PREDEFINED_IMAGES.map { BitmapFactory.decodeResource(...) }` tao 27 bitmap full-size cho adapter.
- Rủi ro: tang memory footprint, thiet bi RAM thap de bi jank/OOM; Glide dang load lai tu Bitmap nen khong tan dung tot resize/cache theo resource id.
- Fix de xuat: model card nen giu `@DrawableRes Int`, adapter load resource id bang Glide, chi decode bitmap goc khi vao game va resize theo board.

### 4. Release AdMob id dang la placeholder

- File: `app/build.gradle`
- Hien trang: release `ADMOB_BANNER_ID`, `ADMOB_INTERSTITIAL_ID`, `ADMOB_APP_OPEN_ID` deu la `"~"`.
- Rủi ro: neu code quang cao duoc bat lai, release co the fail load ads hoac vi pham cau hinh.
- Fix de xuat: dua ID that vao `local.properties`/CI secret va validate khong build release voi placeholder.

### 5. Activity noi bo exported=true

- File: `app/src/main/AndroidManifest.xml`
- Hien trang: `BoardOptionsAct` va `GameAct` dang `android:exported="true"` du khong co intent-filter.
- Rủi ro: surface bi mo rong khong can thiet; Activity noi bo co the bi app khac launch truc tiep voi state thieu.
- Fix de xuat: doi `exported=false` cho Activity khong phai launcher/deep link.

## Findings trung binh

- `saveHighScore(moves, time)` nhan `time` nhung khong dung. Nen luu ca time hoac bo tham so de tranh hieu nham.
- `isNewBest` trong `showWinDialog` tinh truoc khi save, voi lan dau `bestScore=0` se khong bao new best. Nen tinh sau khi doc current best, va luu score theo `(moves, time)` neu muon cong bang.
- `undo()` can them unit test/instrumented test cho sequence move/undo/shuffle/reset. Logic comment hien tai tu thua nhan tung nham lan ve toa do.
- `BitmapTile` tao tile bitmap moi cho moi resize, chua recycle/cache. Nen tranh resize lap lai lien tuc va can nhac cache theo `(imageId, boardSize)`.
- `DialogUtils.showGameDialog` dung `Context` Activity va `setCancelable(false)`. Nen check `isFinishing/isDestroyed` truoc khi show neu goi tu callback bat dong bo, va can nhac cho phep back/cancel.
- `BoardSizeSpinnerFrm` cast `activity as Context`; nen dung `requireContext()` de ro lifecycle va crash message tot hon.

## Tai lieu/ky thuat no

- Khong co test source trong `testDebugUnitTest`; build pass nhung khong bao phu logic game.
- Kotlin daemon bao loi cache/incremental roi fallback compile thanh cong. Neu lap lai, chay `./gradlew --stop` va clean cache build cuc bo.
- `doc/quick_win.md` co mot so muc "[DONE]" nhung can doi chieu bang lint/source truoc khi xem la hoan tat san sang release.

## De xuat thu tu xu ly

1. Fix lint error `app:tint`, chay lai `./gradlew lintDebug`.
2. Doi Activity noi bo sang `exported=false`.
3. Refactor chon anh: giu resource id thay vi bitmap full-size trong adapter/static field.
4. Sua touch handling cua `GameBoard` va them test cho move/undo/shuffle/reset.
5. Don deprecated API/lint warnings theo cum nho.
6. Don unused resources sau khi lint pass va test man hinh chinh.
