# Bao cao loi va memory leak

Cap nhat: 2026-06-18. Xem them audit tong hop tai `doc/audit.md`.

## Ket qua kiem tra

- `./gradlew testDebugUnitTest`: PASS, co unit test JVM chay that.
- `./gradlew lintDebug`: PASS.
- LeakCanary da co trong `debugImplementation`, nhung chua co ket qua runtime leak tu thiet bi/emulator trong dot audit nay.

## Loi chan lint/build quality

- [DONE] `act_game.xml:155`: da doi `android:tint` sang `app:tint`.

## Rui ro memory/lifecycle can uu tien

- [DONE] `BoardOptionsFrm.kt`: khong con decode 27 anh drawable thanh `Bitmap` ngay khi mo man chon; adapter load drawable resource id.
- [DONE] `GameAct.kt`: da bo static Bitmap handoff; Activity nhan image id va board size qua Intent.
- [DONE] `BitmapTile.kt` da xoa han. `PuzzleDescriptor` chi giu `index`; `GameBoard` ve tung manh truc tiep tu anh nguon bang `src Rect` (canvas.drawBitmap voi src/dst). Khong con cat N bitmap con => khong cap phat/recycle bitmap thua. Verify: 6/6 instrumented test PASS (OPPO CPH2577, Pixel 7 Pro), smoke render dung tren OPPO.
- [DONE] `DialogUtils.kt`: them guard bo qua `showGameDialog` khi Activity dang `isFinishing`/`isDestroyed` (tranh crash khi goi tu callback async).

## Warning/performance/UX

- [DONE Task 07] `frm_board_options.xml`: root da `@android:color/transparent` (het overdraw kep); them go FrameLayout wrapper thua + tools:ignore dat sai. Lint sach.
- [DONE Task 09] `act_game.xml`: phang 3 cap wrapper -> 1 root LinearLayout; bo nested weights (glass panel timer/moves wrap + center); them `baselineAligned=false`. Lint NestedWeights = 0.
- [DONE] `GameBoard.kt`: touch listener chi move tren `ACTION_UP`, co `performClick()` va guard bounds.
- [DONE] `AndroidManifest.xml`: Activity noi bo `BoardOptionsAct` va `GameAct` da doi `exported=false`.
- [DONE Task 09] `AndroidManifest.xml`: portrait lock = quyet dinh CO CHU DICH (board vuong + UI doc); suppress `DiscouragedApi` + `LockedOrientationActivity` kem comment. Lint DiscouragedApi = 0. Xem `doc/audit.md` P3.

## Dependency/config

- [DONE] Gradle wrapper da nang `8.14.5` (xem tasks.md).
- [DEFERRED Task 09 P4] Kotlin stdlib `2.1.20`, Material `1.13.0`, Glide `5.0.5`: deu kha moi; nang version = rui ro breaking, hoan sang pass rieng co retest device.
- [OPEN] Kotlin daemon co loi incremental cache va fallback compile thanh cong. Neu lap lai, chay `./gradlew --stop` va clean cache build cuc bo.
