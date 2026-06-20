# 11 — Win celebration & motion (particle, animated counter, redesign win dialog)

> **Status:** ⬜ Todo · **Depends:** 10 (bloom/blur dùng lại), 13 (toggle reduce-motion) · **Verify:** lint + unit + connectedAndroidTest (anim=0). Spec: [`../00-overview.md`](../00-overview.md) §Phase 2
>
> Mục tiêu: khoảnh khắc **thắng** thành điểm nhấn — particle neon + đếm số động + dialog hoành tráng. Hiện tại chỉ có halo lime + dialog tĩnh (`playWinFeedback` + `dialog_game_custom`).

## W1 — Particle / confetti neon
- [ ] `v/NeonParticleView.kt`: canvas vẽ hạt (chấm/đường glow) màu cyan/magenta/lime burst từ tâm board khi thắng; vật lý đơn giản (gravity + velocity), alpha fade.
- [ ] `ValueAnimator` 1 vòng (~1.2s), pool/array hạt cố định (không alloc trong `onDraw`), `cancel()` + clear ở `onDetachedFromWindow`.
- [ ] Overlay phủ toàn màn (thêm vào root `act_game.xml` hoặc window overlay), `clickable=false` để không chặn touch.
- **Acceptance:** confetti bùng đẹp khi win, tự dọn, không leak, không chặn nút.

## W2 — Animated counters (đếm lên)
- [ ] Trong win dialog: số **moves** và **time** chạy từ 0 → giá trị thật bằng `ValueAnimator` (~600ms, `DecelerateInterpolator`), kèm glow chữ.
- [ ] Nếu là **best mới**: badge "NEW BEST" glow pulse (dùng `neon_pulse` đã có) + màu lime; tách riêng best-moves vs best-time (logic `ScoreUtils.isNewBestTime` đã có).
- **Acceptance:** số đếm mượt, badge best hiện đúng điều kiện.

## W3 — Redesign win dialog (`dialog_game_custom.xml`)
- [ ] Bố cục neon mới: tiêu đề glow, hàng stats (⏱ time / 📊 moves) trong glass panel, **đánh giá sao** theo hiệu suất (vd ≤ngưỡng moves → 3★ glow), nút Share + Play again.
- [ ] Dùng token neon + frosted-glass nền (W trong Wave 10 G2). Không hard-code hex, i18n mọi string.
- [ ] Share kết quả (reuse intent share đã có ở footer): "Solved <w>x<h> in <moves> moves / <time>".
- **Acceptance:** dialog đẹp, sao đúng logic, share hoạt động, không vỡ ở font-scale.

## W4 — Solved reveal sequence (tuỳ chọn nâng cao)
- [ ] Trước confetti: quét sáng từng tile theo thứ tự (ripple lime chạy chéo board) ~400ms rồi mới bung particle + mở dialog (giữ tổng delay hợp lý ~700–900ms như hiện tại).
- [ ] `GameBoard` expose hàm sequence; cancel an toàn nếu user thoát.
- **Acceptance:** chuỗi reveal mượt, không chặn, thoát giữa chừng không crash.

## W5 — Tôn trọng reduce-motion / toggle
- [ ] Đọc `fx_reduce_motion`/`fx_quality` (Wave 13): bật reduce → bỏ particle + sequence, giữ halo + dialog tĩnh (counters set thẳng giá trị).
- [ ] Tôn trọng system `Settings.Global.ANIMATOR_DURATION_SCALE == 0` → skip animation (cho test/anim=0).
- **Acceptance:** reduce-motion tắt đúng hiệu ứng nặng; test anim=0 không treo.

## W6 — Test & verify
- [ ] Widget: `NeonParticleView` chạy không crash; win dialog inflate + hiển thị stats; animated counter kết thúc đúng giá trị.
- [ ] Instrumented: flow solve → win dialog hiện (như test win hiện có), không treo với anim=0.
- [ ] `lintDebug` + `testDebugUnitTest` + `connectedDebugAndroidTest` PASS (device thật).
- [ ] Cập nhật `README.md`, `tasks.md`, đánh dấu kanban.
- **Acceptance:** build xanh, test win xanh, tài liệu đồng bộ.
