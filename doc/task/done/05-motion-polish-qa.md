# 05 — Motion glow + Polish + QA

> **Status:** ✅ Done · **Depends:** 02, 03, 04 · **Verify:** WCAG AA pass + lint/unit/19 instrumented PASS (Pixel 7 Pro). Spec: [`../00-overview.md`](../00-overview.md)

## M1 — Pulse glow animations
- [x] `anim/neon_pulse.xml` (scale 1.0→1.05 + alpha 0.82→1.0, lặp reverse 900ms) áp cho **logo splash** (breathing neon).
- [-] Card/ring/title breathing: **bỏ chủ ý** — pulse lặp khi đang chơi gây phân tâm; chỉ giữ ở splash (màn idle).
- **Acceptance:** ✅ pulse tinh tế, chỉ ở splash, không ngốn CPU lúc chơi.

## M2 — Tile/win feedback
- [x] `GameBoard.playWinFeedback()`: lóe halo **lime** quanh board (ValueAnimator 0→1→0, 700ms); `GameAct` gọi khi solved + hoãn dialog 500ms để thấy hiệu ứng.
- [x] Slide hợp lệ: tile-active glow + `OvershootInterpolator` (đã có từ 03).
- **Acceptance:** ✅ thắng có phản hồi sáng; dialog vẫn mở (sau 500ms), không bị chặn.

## M3 — Transition giữa màn
- [x] Rà transition `GameAct` (Fade) + splash `smooth_slide_in_right/elegant_fade_out`: nền chuyển màn = `#0E1230` (xác nhận qua logcat `bc=ff0e1230`), không flash trắng/đỏ.
- **Acceptance:** ✅ liền mạch trên nền tối.

## M4 — QA tương phản & accessibility
- [x] WCAG (trên nền `#0E1230`): text_primary **16.2**, text_secondary **8.4**, cyan **13.8**, lime **15.5**, magenta **6.7** — tất cả ≥ AA (4.5:1).
- [x] contentDescription đúng nghĩa + i18n (A5).
- [x] Font-scale guard `BaseActivity` không đụng → vẫn hoạt động.
- **Acceptance:** ✅ đạt AA.

## M5 — QA hiệu năng
- [x] Overdraw nền giảm 6x→**2x** (A2 bỏ background kép + polish gộp `neon_bg_ambient` 3→2 lớp). Đo `debug.hwui.overdraw show`: nền xanh-lá (2x), status bar 1x.
- [x] GPU `dumpsys gfxinfo` khi slide/shuffle (Pixel 7 Pro): **Janky 6.67%**, 50th=5ms, 90th=14ms (< 16.6ms / 60fps), Slow bitmap uploads=0.

## Post-audit polish (5 nit → ~9.8)
- [x] Gỡ `bestScore`/`loadHighScore` state chết; `saveHighScore` gọn lại.
- [x] `winAnimator`/`animator` cancel trong `onDetachedFromWindow` (GameBoard).
- [x] `neon_bg_ambient` 3→2 lớp (bỏ bloom magenta + 2 token thừa) → overdraw 3x→2x.
- [x] Cache bitmap ảnh neon (vector) theo resId @512px; KHÔNG recycle (chỉ recycle raster lớn).
- [x] Đo GPU jank + overdraw thật trên thiết bị (số liệu trên).
- **Verify:** lint + unit + **19/19 instrumented PASS** (Pixel 7 Pro), logcat sạch.

## M6 — Dọn & verify build
- [x] Xóa dead anim (7 file): `elegant_splash_logo`, `smooth_slide_out_left/in_left/out_right`, `bounce_scale_in`, `button_click`, `layout_animation_grid` (verify 0-ref). (Drawable/màu cũ + red theme đã dọn ở task 06.)
- [x] `lintDebug` + `testDebugUnitTest` PASS.
- [x] `connectedDebugAndroidTest` **19/19 PASS** trên Pixel 7 Pro (animation scale = 0).
- **Acceptance:** ✅ sạch reference; test xanh.

## M7 — Cập nhật tài liệu
- [x] Cập nhật `doc/tasks.md` ghi nhận neon revamp.
- [x] Đánh dấu trạng thái kanban `doc/task/*` (05 → done).

**Tests bổ sung:** widget `playWinFeedbackRunsWithoutCrash`, `neonPulseAnimationInflates`. Tổng 19 instrumented + unit.
