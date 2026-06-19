# Wave 5 — Motion glow + Polish + QA

> Lớp cuối: chuyển động phát sáng + kiểm chất lượng toàn diện.

## M1 — Pulse glow animations
- [ ] `anim/neon_pulse.xml` (alpha/scale lặp dịu) cho logo splash, viền card được chọn, ring preview.
- [ ] Title màn: glow "breathing" nhẹ (ValueAnimator đổi shadowRadius) — tùy chọn, tránh gây phân tâm.
- **Acceptance:** pulse tinh tế, không chớp gắt, không ngốn CPU khi idle.

## M2 — Tile/win feedback
- [ ] Khi giải xong: board lóe glow lime + (tùy chọn) ripple sáng trước khi hiện dialog.
- [ ] Khi slide hợp lệ: tile-active glow tăng nhẹ theo `OvershootInterpolator` đã có.
- **Acceptance:** thắng có phản hồi "đã mắt"; không chặn mở dialog.

## M3 — Transition giữa màn
- [ ] Rà `smooth_slide_*`, Fade transition của `GameAct`: đảm bảo nền tối liên tục, không flash sáng giữa Activity.
- **Acceptance:** chuyển màn liền mạch trên nền tối.

## M4 — QA tương phản & accessibility
- [ ] Kiểm WCAG: text chính ≥ AA (4.5:1) trên nền tối; accent đủ tương phản.
- [ ] TalkBack: contentDescription nút/ảnh còn đúng.
- [ ] Font-scale guard (`BaseActivity` fontScale=1.0) vẫn hoạt động sau revamp.
- **Acceptance:** đạt AA; TalkBack đọc đúng; font-scale ổn.

## M5 — QA hiệu năng
- [ ] GPU rendering profile khi shuffle/slide trên OPPO CPH2577 (animation scale = 0 cho test, =1 cho cảm nhận thật).
- [ ] Kiểm overdraw (Developer Options → Debug GPU overdraw): vùng đỏ giảm/không tăng so với trước.
- **Acceptance:** không jank kéo dài; overdraw chấp nhận được.

## M6 — Dọn & verify build
- [ ] Gỡ drawable/màu cũ không còn dùng (`bg_liquid_gradient`, `bg_glass_*` cũ, `bkg.jpg`, token đỏ) sau khi mọi màn đã chuyển — verify 0 reference rồi mới xóa.
- [ ] `./gradlew lintDebug testDebugUnitTest` PASS.
- [ ] `connectedDebugAndroidTest` PASS trên OPPO (animation scale = 0).
- **Acceptance:** sạch reference; toàn bộ test xanh.

## M7 — Cập nhật tài liệu
- [ ] Cập nhật `doc/tasks.md`, `doc/audit.md` ghi nhận revamp; chụp before/after gửi kèm.
- [ ] Đánh dấu trạng thái các wave trong các file `doc/task/*`.

**Build gate cuối:** Definition of Done ở `README.md §7` thỏa toàn bộ.
