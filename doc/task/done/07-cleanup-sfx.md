# 07 — Code cleanup + Sound Effects (SFX)

> **Status:** ✅ Done · **Depends:** — · **Verify:** lint + unit + **24/24 instrumented PASS** (Pixel 7 Pro, anim scale = 0). Spec: [`../00-overview.md`](../00-overview.md)
>
> Nhóm việc **trong code thuần**, không cần SDK/config ngoài. Rủi ro thấp, giá trị ngay.

## C1 — Sound Effects (SFX) — Quick-win feature #2
- [x] `util/SoundManager.kt`: `SoundPool` (USAGE_GAME, maxStreams 4), load async từ `res/raw`, `release()` trong `onDestroy`.
- [x] SFX **move hợp lệ**: thêm hook `GameBoard.onMoveSound` fire đúng tại điểm slide thành công (cạnh haptic, KHÔNG fire khi undo/reset/shuffle); `GameAct` wire `onMoveSound = { soundManager.playMove() }`.
- [x] SFX **win**: `GameAct.showWinDialog` gọi `soundManager.playWin()` khi solved.
- [x] Toggle **bật/tắt âm thanh**: item `action_sound` (checkable) trong `menu_game.xml`, persist `sound_enabled` trong `puzzle_prefs`; `SoundManager.isEnabled` điều khiển phát.
- [x] Asset âm thanh tự tạo procedural (không bản quyền): `res/raw/sfx_move.wav` (~2.5KB click), `sfx_win.wav` (~28KB arpeggio C-E-G-C).
- **Acceptance:** ✅ move/win có tiếng; tắt toggle thì im (no-op); `release()` trong onDestroy; load async không chặn UI.

## C2 — `saveHighScore` không dùng `time`
- [x] `saveHighScore(moves, seconds)` lưu thêm `best_time_<w>x<h>` song song `best_<w>x<h>`.
- [x] `showWinDialog` đọc cả best-moves + best-time, hiển thị cờ "NEW BEST MOVES & TIME / NEW HIGH SCORE / NEW BEST TIME" theo từng loại độc lập.
- [x] `ScoreUtils.isNewBestTime()` + `NO_BEST_TIME` thuần Kotlin (test JVM).
- **Acceptance:** ✅ lưu/đọc đúng cả moves lẫn time; new-best đúng từng loại.

## C3 — Dọn layout nhỏ
- [x] `frm_board_options.xml`: gỡ `FrameLayout` wrapper thừa (UselessParent) bọc `ConstraintLayout` → giảm 1 cấp nesting; bỏ `tools:ignore="UselessParent"` đặt sai chỗ (LinearLayout có 2 con, không phải useless).
- [x] Overdraw: root đã `transparent` (đã xử lý ở wave neon); lint sạch, không cảnh báo overdraw mới.
- **Acceptance:** ✅ lint PASS, giảm nesting, build APK OK.

## C4 — Test còn thiếu
- [x] Instrumented `GameBoardWidgetTest`: `shuffleClearsUndoHistory` + `resetClearsUndoHistory` (shuffle/reset xoá undo stack → `undo()` = false) + `onMoveSoundHookCanBeSetAndInvoked`.
- [x] Instrumented `SoundToggleTest`: toggle `action_sound` persist `sound_enabled` (pattern `TileNumbersToggleTest`).
- [x] Instrumented `SoundManagerTest`: construct/play(enabled+disabled)/release không crash.
- [x] Unit `ScoreUtilsTest`: 4 case best-time (first/faster/slower/equal).
- **Acceptance:** ✅ test 3 lớp (unit + widget + instrumented) cho C1–C3.

## C5 — Verify & docs
- [x] `./gradlew lintDebug` + `testDebugUnitTest` PASS.
- [x] `./gradlew connectedDebugAndroidTest` **24/24 PASS** (Pixel 7 Pro, anim scale = 0).
- [x] Cập nhật `doc/quick_win.md` (SFX #2 → DONE), `doc/tasks.md`, README dashboard.
- **Acceptance:** ✅ build xanh, tài liệu đồng bộ.

**Tests bổ sung (5):** `SoundManagerTest`, `SoundToggleTest`, `GameBoardWidgetTest.shuffleClearsUndoHistory/resetClearsUndoHistory/onMoveSoundHookCanBeSetAndInvoked`, `ScoreUtilsTest` +4 case. Tổng **24 instrumented + unit PASS**.
