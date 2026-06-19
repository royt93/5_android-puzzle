# 10 — Advanced glow & blur (frosted glass, bloom thật, animated border)

> **Status:** ⬜ Todo · **Depends:** 13 (đọc toggle `fx_quality`/`fx_blur`) — có thể chạy trước nếu hardcode key default · **Verify:** lint + unit + connectedAndroidTest (anim=0) + gfxinfo/overdraw trên device. Spec: [`../00-overview.md`](../00-overview.md) §Phase 2
>
> Mục tiêu: nâng glow từ "stroke tĩnh" lên **blur thật + bloom + viền gradient động**. Định hướng **phô diễn tối đa** — chấp nhận hiệu ứng nặng hơn, nhưng MỌI hiệu ứng nặng phải đọc toggle để tắt được trên máy yếu (mặc định bật).

## G1 — `util/NeonBlur.kt` (RenderEffect helper)
- [ ] Hàm `applyBlur(view, radiusPx, tint?)`: API 31+ dùng `RenderEffect.createBlurEffect(r, r, Shader.TileMode.CLAMP)` (chain với `createColorFilterEffect` nếu có tint); API < 31 → no-op (fallback dùng scrim drawable ở G2).
- [ ] Hàm `clearBlur(view)` set `setRenderEffect(null)`.
- [ ] Guard `Build.VERSION.SDK_INT >= 31`; tài liệu hoá fallback. Cùng style API-guard như `NeonGlow.kt`.
- **Acceptance:** helper compile, no-op an toàn < API 31, không crash.

## G2 — Frosted-glass behind dialog / panel
- [ ] Khi mở dialog (win `dialog_game_custom`, About): blur **content phía sau** (root của Activity) qua `NeonBlur.applyBlur` + scrim `neon_bg_scrim`; `clearBlur` khi dismiss. Fallback < 31: chỉ scrim như hiện tại (không regress).
- [ ] Glass panel (stats bar + bottom dock trong `act_game.xml`): thử `RenderEffect` blur lớp nền (nếu khả thi không tốn fps) HOẶC giữ glass drawable + thêm inner highlight gradient cho cảm giác kính dày hơn.
- [ ] Đảm bảo blur áp/đóng đúng vòng đời (không để blur dính khi Activity resume lại).
- **Acceptance:** dialog nổi trên nền mờ đẹp (API31+), máy cũ vẫn scrim sạch, không leak blur.

## G3 — Animated gradient border (sweep glow chạy quanh khung)
- [ ] `v/NeonBorderView.kt` (hoặc Drawable): `SweepGradient` (cyan→magenta→lime→cyan) + `Matrix.postRotate` theo `ValueAnimator` lặp → viền sáng chạy vòng quanh board frame / card đang chọn.
- [ ] Vẽ trên hardware layer; cache `Paint`/`Shader`/`Matrix`, KHÔNG cấp phát trong `onDraw`; `cancel()` animator ở `onDetachedFromWindow`.
- [ ] Áp cho: khung `GameBoard` (viền ngoài) + card ảnh đang focus trong grid (tuỳ chọn). Đọc toggle `fx_quality` — tắt → viền tĩnh.
- **Acceptance:** viền chạy mượt 60fps trên Pixel 7 Pro; tắt được; không jank lúc slide.

## G4 — Bloom thật cho tile-active & win-halo
- [ ] Nâng glow tile-active: ngoài layered-stroke hiện có, thử overlay bloom bằng `RenderEffect` blur trên 1 lớp overlay (API31+), fallback giữ layered-stroke 4 lớp.
- [ ] Win halo (`playWinFeedback`): tăng cường bloom lime (bán kính lớn hơn, kèm blur nếu API31+).
- [ ] Profile: không vượt 16.6ms/frame khi đang phát hiệu ứng (đo `gfxinfo`).
- **Acceptance:** glow dày/mềm hơn rõ rệt, vẫn trong ngân sách frame trên thiết bị flagship.

## G5 — Tôn trọng toggle hiệu năng
- [ ] Đọc `fx_quality` (high/low) + `fx_blur` (bool) từ `puzzle_prefs` (key do Wave 13 định nghĩa; nếu 13 chưa xong → default `high`/`true` hardcode + TODO nối UI).
- [ ] `low` → tắt blur (G2/G4), viền gradient thành tĩnh (G3), giữ glow stroke cơ bản (không regress baseline 9 wave).
- **Acceptance:** chuyển toggle thấy khác biệt rõ; chế độ low đạt fps như baseline cũ.

## G6 — Test & verify
- [ ] Unit/Robolectric: `NeonBlur` no-op path không ném lỗi.
- [ ] Instrumented: mở/đóng win dialog không crash trên emulator API < 31 và device API 31+; `NeonBorderView` inflate OK; toggle low/high không crash.
- [ ] `./gradlew lintDebug` + `testDebugUnitTest` + `connectedDebugAndroidTest` PASS (anim=0, device thật).
- [ ] Đo `dumpsys gfxinfo` + `debug.hwui.overdraw` khi bật full FX; ghi số liệu vào `doc/device_test.md`.
- [ ] Cập nhật `00-overview.md` (kỹ thuật blur/border), `README.md` dashboard, `tasks.md`.
- **Acceptance:** build xanh, không crash đa API, số liệu fps/overdraw ghi nhận.
