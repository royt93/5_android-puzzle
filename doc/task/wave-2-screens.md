# Wave 2 — Revamp các màn hình (dùng primitive Wave 1)

> Phụ thuộc Wave 1. Mỗi màn: đổi background → ambient bloom, panel → neon glass, nút → neon btn, text → token + glow.

## S1 — Splash (`activity_splash.xml` + `SplashActivity.kt` + `drawable/startup.xml`)
- [ ] Nền → `neon_bg_ambient` (thay solid đỏ trong `startup.xml`).
- [ ] Logo: bọc ring glow (`neon_ring`) + pulse nhẹ (xem Wave 5).
- [ ] ProgressBar: tint `neon_cyan`.
- [ ] `SplashTheme` windowBackground → ambient tối (tránh flash đỏ lúc cold start).
- **Acceptance:** cold start không flash trắng/đỏ; logo có halo; chuyển màn mượt.

## S2 — Toolbar + Spinner (`item_game_toolbar.xml`, `item_game_toolbar_spinner.xml`, `frm_board_size_spinner.xml`, `BoardSizeAdapter.kt`)
- [ ] Toolbar nền `neon_bg_surface`, title dùng `TextAppearance.App.TitleLarge.Glow`.
- [ ] Spinner: nền glass, item dropdown nền `neon_bg_elevated`, text `neon_text_primary`, selected highlight cyan.
- [ ] Icon overflow/back tint `neon_text_primary`.
- **Acceptance:** spinner đọc rõ trên nền tối; "4 x 4" có accent.

## S3 — BoardOptions: grid + card (`frm_board_options.xml`, `frm_titled_image_card.xml`, `item_board_options_grid_header.xml`, `ImageCardsAdt.kt`, `SquareImageView.kt`)
- [ ] Nền fragment → `neon_bg_ambient` (gỡ `bkg.jpg`).
- [ ] Card ảnh → `neon_glass_card`: viền cyan glow mảnh, radius 16dp; ảnh pastel giữ nguyên làm focal (glow chỉ ở viền, không phủ lên ảnh).
- [ ] Header "Select an image" → text glow.
- [ ] Card được chọn / nhấn: tăng glow cyan (state selector).
- **Acceptance:** lưới đẹp, ảnh pastel nổi bật trên nền tối, viền glow đều; cuộn mượt (không lag glow).

## S4 — BoardOptions: footer dock (`frm_board_options.xml` phần footer)
- [ ] Panel footer → `neon_glass_panel`.
- [ ] Nút Rate/More/Share: icon tint cyan + label `neon_text_secondary`; nhấn → glow.
- **Acceptance:** dock nổi glass, icon glow nhẹ.

## S5 — Game: top info bar (`act_game.xml` topInfoBar)
- [ ] Panel stats (timer + moves) → `neon_glass_panel`.
- [ ] `tvTimer` → màu cyan + glow; `tvMoves` → lime + glow.
- [ ] Preview ảnh gốc (`ivOriginal`): bọc `neon_ring` viền glow.
- **Acceptance:** timer/moves rõ, có accent; preview có khung glow.

## S6 — Game: bottom dock + nút (`act_game.xml` layoutBottom)
- [ ] SHUFFLE → `neon_btn_primary` (cyan); RESET → `neon_btn_danger` (magenta); Undo (ImageButton) → icon cyan + ring.
- [ ] Giữ `spring_button_press/release` + `addSpringClickAnimation()`.
- **Acceptance:** 3 nút đồng bộ glow, phân biệt primary/danger rõ; touch vẫn nảy.

## S7 — Dialog (`dialog_game_custom.xml`, `styles_dialog.xml`, `DialogUtils.kt`, drawable `bg_*dialog*`, `btn_game_yes/no`)
- [ ] Nền dialog → `neon_glass_panel` trên scrim `neon_bg_scrim` (thay `bg_game_dialog_panel` xám + `bg_dialog_clean` đỏ).
- [ ] Title → glow cyan; body → `neon_text_primary`.
- [ ] YES → lime glow (`btn_game_yes` đổi token); NO → magenta glow (`btn_game_no`).
- [ ] Giữ guard `isFinishing/isDestroyed` đã có trong `DialogUtils`.
- **Acceptance:** dialog win/shuffle/reset đồng bộ neon; nút phân biệt; không vỡ guard.

## S8 — Menu "Show tile numbers" (`menu/menu_game.xml`)
- [ ] Đảm bảo item checkable hiển thị đúng trên popup nền tối (text `neon_text_primary`, check cyan).
- **Acceptance:** mở overflow trên nền tối đọc rõ, toggle vẫn persist.

**Build gate Wave 2:** mọi màn nền tối + glass + glow đồng bộ; `lintDebug` PASS; smoke tay OPPO từng màn.
