# 12 — Theming đa sắc (palette switcher, persist, apply runtime)

> **Status:** ⬜ Todo · **Depends:** 13 (UI picker trong Settings) · **Verify:** lint + unit + connectedAndroidTest + WCAG AA cho mọi palette. Spec: [`../00-overview.md`](../00-overview.md) §Phase 2
>
> Mục tiêu: người chơi chọn **tông neon chủ đạo** (accent). Hiện accent khoá cứng cyan/magenta/lime. Biến accent thành **runtime-switchable**, persist, áp toàn app + canvas.

## T1 — Định nghĩa các palette
- [ ] 4–5 palette accent giữ chung nền deep-navy + glass: **Cyan (default)**, **Magenta**, **Lime**, **Violet**, (tuỳ chọn **Aurora** đa sắc xoay vòng dùng border Wave 10 G3).
- [ ] Mỗi palette = bộ {primary, secondary, glow, on_accent}. Định nghĩa trong `colors.xml` (nhóm `theme_<name>_*`) + bản Kotlin trong `NeonPalette.kt` (map theo id).
- **Acceptance:** các palette định nghĩa nhất quán, không hard-code rải rác.

## T2 — Persist & nguồn sự thật accent
- [ ] Key `accent_theme` (String/int) trong `puzzle_prefs`; default = cyan (giữ nguyên hiện trạng).
- [ ] `NeonPalette` đọc accent hiện tại từ prefs (hàm `current(context)` trả về bộ màu) — canvas `GameBoard` dùng nguồn này thay hằng số.
- **Acceptance:** đổi key → canvas đổi màu sau khi áp; default không đổi trải nghiệm cũ.

## T3 — Apply runtime cho View/XML
- [ ] Drawable accent (`neon_btn_primary`, `neon_ring`, stroke glass, text glow) chuyển sang **tint runtime** qua `ColorStateList`/`setColorFilter` hoặc theme overlay áp ở `BaseActivity.onCreate` (đọc accent → set theme/tint trước `setContentView`).
- [ ] Đổi palette → `recreate()` các Activity đang mở (hoặc áp lại tint ngay). Status/nav bar giữ deep-navy.
- **Acceptance:** mọi màn (Splash/BoardOptions/Game/Dialog) đổi accent đồng bộ, không sót chỗ cyan cứng.

## T4 — UI chọn palette
- [ ] Trong Settings screen (Wave 13): hàng swatch glow cho từng palette; chọn → lưu + áp ngay (recreate). Swatch hiển thị bằng `neon_ring` tint theo palette.
- [ ] Nếu Wave 13 chưa xong: tạm 1 dialog chọn nhanh (reuse `DialogUtils`) + TODO chuyển vào Settings.
- **Acceptance:** chọn palette trực quan, phản hồi tức thì.

## T5 — Test & verify (gồm WCAG mọi palette)
- [ ] Unit: `NeonPalette.current()` trả đúng bộ theo key; default fallback an toàn khi key lạ.
- [ ] Kiểm **tương phản WCAG AA** text/accent trên nền `#0E1230` cho **từng** palette (magenta hiện 6.7 — đảm bảo accent mới ≥ 4.5:1; nếu thấp → chỉ dùng cho viền/không cho chữ nhỏ).
- [ ] Instrumented: đổi palette → recreate không crash; canvas render màu mới.
- [ ] `lintDebug` + `testDebugUnitTest` + `connectedDebugAndroidTest` PASS.
- [ ] Cập nhật `00-overview.md` (bảng palette), `README.md`, `tasks.md`.
- **Acceptance:** mọi palette đạt AA cho chữ chính, build xanh, đổi theme không regress.
