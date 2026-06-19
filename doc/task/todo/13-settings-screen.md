# 13 — Settings screen riêng (gom toggle + FX/theme controls)

> **Status:** ⬜ Todo · **Depends:** — (nền cho 10/11/12) · **Verify:** lint + unit + connectedAndroidTest (anim=0). Spec: [`../00-overview.md`](../00-overview.md) §Phase 2
>
> Mục tiêu: gom mọi tuỳ chọn vào **1 màn Settings neon**, thay vì rải trong overflow `menu_game.xml`. Đây là **host** cho toggle hiệu năng (Wave 10/11) và theme picker (Wave 12) → nên làm trước.

## S1 — `SettingsActivity` + `act_settings.xml` (neon)
- [ ] Tạo `act/SettingsActivity.kt` (extends `BaseActivity`) + layout neon: toolbar glass + danh sách hàng setting (View-based để giữ token neon; không dùng `PreferenceFragment` mặc định vì khó neon-hoá).
- [ ] Mỗi hàng: icon + tiêu đề + subtitle + control (Switch tint cyan / chevron). Dùng `neon_glass_panel`, `neon_text_*`.
- **Acceptance:** màn Settings render đúng neon, cuộn mượt.

## S2 — Gom & mở rộng toggles
- [ ] Chuyển **Show tile numbers** (`show_numbers`) + **Sound effects** (`sound_enabled`) từ `menu_game.xml` → Settings (giữ key cũ, không mất dữ liệu).
- [ ] Thêm mới (key trong `puzzle_prefs`):
  - `haptic_enabled` (rung khi move — hiện luôn bật) → cho tắt.
  - `fx_quality` (high/low) — bật/tắt blur + viền gradient + bloom (Wave 10).
  - `fx_blur` (bool, chỉ hiện khi API 31+; ẩn/disabled + ghi chú khi < 31).
  - `fx_reduce_motion` (bool) — tắt particle/sequence (Wave 11).
  - `accent_theme` — vào hàng chọn palette (Wave 12).
- [ ] Quyết định: **bỏ** overflow menu game hay giữ shortcut? → giữ menu game tối giản (chỉ shortcut tới Settings) hoặc bỏ hẳn, ghi quyết định.
- **Acceptance:** mọi toggle đọc/ghi đúng `puzzle_prefs`, áp tức thì (sound/numbers/haptic) hoặc khi quay lại game.

## S3 — Entry points
- [ ] Icon Settings (vector neon `ic_settings.xml`) ở: footer `frm_board_options.xml` (cột mới hoặc thay) **và/hoặc** toolbar `act_game.xml`.
- [ ] Mở `SettingsActivity` qua intent, `exported=false`.
- **Acceptance:** vào Settings dễ từ màn chính + màn game.

## S4 — Áp đặt & backward-compat
- [ ] Đọc toggle ở nơi tiêu thụ: `SoundManager.isEnabled`, `GameBoard` (show numbers / fx_quality), `GameAct` (haptic). Default giữ hành vi cũ (numbers theo cũ, sound on, haptic on, fx high).
- [ ] Migration nhẹ: nếu key mới chưa tồn tại → set default 1 lần.
- **Acceptance:** không mất setting cũ; app cũ nâng cấp chạy đúng default.

## S5 — Test & verify
- [ ] Instrumented `SettingsScreenTest`: mở Settings, toggle từng switch → giá trị persist (đọc lại prefs); chọn palette → áp (link Wave 12).
- [ ] Hồi quy: tắt sound → không phát; tắt numbers → ẩn số; tắt haptic → không rung; fx low → không blur.
- [ ] `lintDebug` + `testDebugUnitTest` + `connectedDebugAndroidTest` PASS (anim=0, device thật).
- [ ] i18n mọi string settings; cập nhật `README.md`, `tasks.md`, kanban.
- **Acceptance:** Settings hoạt động đầy đủ, persist đúng, build/test xanh.
