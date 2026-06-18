# Bao cao loi va memory leak

Cap nhat: 2026-06-18. Xem them audit tong hop tai `doc/audit.md`.

## Ket qua kiem tra

- `./gradlew testDebugUnitTest`: PASS, nhung khong co unit test (`NO-SOURCE`).
- `./gradlew lintDebug`: FAIL, 1 error + 121 warnings.
- LeakCanary da co trong `debugImplementation`, nhung chua co ket qua runtime leak tu thiet bi/emulator trong dot audit nay.

## Loi chan lint/build quality

- [OPEN] `act_game.xml:155`: `android:tint` tren `ImageButton` phai doi sang `app:tint`.

## Rui ro memory/lifecycle can uu tien

- [OPEN] `BoardOptionsFrm.kt`: decode 27 anh drawable thanh `Bitmap` ngay khi mo man chon. Nen giu drawable resource id trong model/adapter va chi decode anh duoc chon.
- [OPEN] `GameAct.kt`: `initialConfig` la static field giu `Bitmap`. Code da clear sau khi doc, nhung van co rui ro memory spike va mat state khi Activity recreate. Nen truyen image id qua Intent.
- [OPEN] `BitmapTile.kt`: moi lan resize tao nhieu tile bitmap moi. Can tranh resize lap lai khong can thiet, can nhac cache/reuse theo board size.
- [OPEN] `DialogUtils.kt`: dialog dung Activity context; nen tranh show khi Activity dang finishing/destroyed neu goi tu callback async.

## Warning/performance/UX

- [OPEN] `frm_board_options.xml`: lint van bao overdraw o root background `@drawable/bkg`.
- [OPEN] `act_game.xml`: nested weights co the tang chi phi measure.
- [OPEN] `GameBoard.kt`: touch listener xu ly moi MotionEvent, nen chi xu ly click/up gesture de tranh move ngoai y muon va dung accessibility.
- [OPEN] `AndroidManifest.xml`: Activity noi bo `BoardOptionsAct` va `GameAct` dang `exported=true`; nen doi `false`.
- [OPEN] `AndroidManifest.xml`: portrait lock bi canh bao voi Android 16+. Neu game bat buoc portrait, can chap nhan/suppress co chu dich va test large screen.

## Dependency/config

- [OPEN] Gradle wrapper `8.13` co warning co ban moi `8.14.5`.
- [OPEN] Kotlin stdlib `2.1.20`, Material `1.13.0`, Glide `5.0.5` co warning version moi.
- [OPEN] Kotlin daemon co loi incremental cache va fallback compile thanh cong. Neu lap lai, chay `./gradlew --stop` va clean cache build cuc bo.
