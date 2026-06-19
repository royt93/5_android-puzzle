# 06 — Audit fixes (sau Wave 1–4)

> **Status:** ✅ Done · **Depends:** 02, 03, 04 · **Verify:** lint + unit + instrumented + smoke Pixel, đo overdraw. Spec: [`../00-overview.md`](../00-overview.md)
> Nguồn: audit code neon revamp 2026-06-19.

## 🔴 Cao
- [x] **A1 — Recycle bitmap**: `GameAct` giải phóng `boardImage` cũ trước khi gán mới + recycle ở `onDestroy`. Chặn rò ~4.6MB/lần đổi ảnh. (`GameAct.kt` decodeBoardBitmap/mountBoard).
- [x] **A2 — Cắt overdraw nền kép**: bỏ `android:background="@drawable/neon_bg_ambient"` ở `act_game.xml`, `frm_board_options.xml`, `activity_splash.xml` (đã có ở windowBackground) → set `transparent`. Overdraw 6x→3x.

## 🟡 Trung
- [x] **A3 — Dọn dead resources** (verify 0-ref rồi xóa): `bkg.jpg`, `bg_glass_card/panel`, `bg_liquid_gradient`, `btn_game_yes/no`, `bg_game_dialog_panel`, `btn_glass_action`, `bg_dialog_clean`, `startup.xml`, `board_active(_trans)`, `white`, `black`, `neon_bg_scrim`.
- [x] **A4 — Dọn red theme**: xóa block `md_theme_*` + alias `colorPrimary`/`colorIcons` trong `colors.xml` (sau khi xóa startup.xml).
- [x] **A5 — contentDescription**: `ivOriginal` + card ảnh → string mô tả đúng (ảnh ghép); `btUndo` → `@string` (i18n).
- [x] **A6 — Bug high score lần đầu**: dùng cờ `hasPreviousBest` thay `bestScore > 0` (`GameAct.kt`).
- [x] **A7 — Glow mềm hơn**: tăng layered-stroke 3→4 lớp (alpha 0x14/0x30/0x66/0xE0). CHỦ Ý giữ hardware layer (không `setLayerType`/`BlurMaskFilter`) để không hy sinh perf; text vẫn `shadowLayer` (glow rõ từ API 28).
- [x] **A8 — Cache nhãn số tile**: mảng `"1".."n²"` thay `toString()` mỗi tile mỗi frame trong `onDraw`.

## 🟢 Thấp
- [x] **A9 — Dùng style `.Glow`**: wire `TitleLarge.Glow` vào `Widget.App.Toolbar` → title toolbar có glow, style được dùng thật. Dialog giữ glow cyan inline (đúng tông).
- [x] **A10 — Token hóa hex bloom** trong `neon_bg_ambient.xml` (`neon_cyan_bloom`/`neon_magenta_bloom`).
- [x] **A11 — Refactor `undo()`**: bỏ ~25 dòng comment mâu thuẫn, đổi tên biến (`GameBoard.kt`).
- [x] **A12 — Xóa token/style thừa**: bỏ `glow_radius_sm/md`, `image_preview_size`, `HeadlineLarge.Glow`, `GameTitle.Glow`. GIỮ `neon_violet` (dùng ở `neon_img_6`) và `TitleLarge.Glow` (dùng ở toolbar).

**Lưu ý:** mọi thay đổi kèm unit + widget + integration test; mỗi đợt chạy lint/test + smoke Pixel.
