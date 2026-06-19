# Task Board — Neon/Glow UI Revamp

Cập nhật: 2026-06-19 · Owner: roy

Bảng task theo **kanban**: mỗi task là 1 file `.md`, **di chuyển file giữa thư mục** theo trạng thái.

```
doc/task/
├── README.md        ← file này (index + dashboard)
├── 00-overview.md   ← spec: design tokens, kỹ thuật glow, risk (không phải task)
├── todo/            ← chưa làm
├── inprogress/      ← đang làm
└── done/            ← xong + đã verify
```

**Quy ước:** khi bắt đầu 1 task → `git mv todo/<x>.md inprogress/`; khi xong+verify → `git mv inprogress/<x>.md done/`. Trong mỗi file dùng checkbox: `[ ]` todo · `[~]` đang làm · `[x]` xong · `[!]` blocked · `[-]` bỏ.

## Dashboard

| # | Task | Trạng thái | Vị trí |
|---|------|-----------|--------|
| 01 | Foundation (tokens, dark theme, glow primitives) | ✅ Done | `done/01-foundation.md` |
| 02 | Screens (Splash, BoardOptions, Game, Dialog) | ✅ Done | `done/02-screens.md` |
| 03 | GameBoard canvas (halo, tile-active, glow số) | ✅ Done | `done/03-gameboard-canvas.md` |
| 04 | Neon images (vector + bridge Vector→Bitmap) | ✅ Done | `done/04-neon-images.md` |
| 05 | Motion + polish + QA (pulse, win-feedback, WCAG) | ✅ Done | `done/05-motion-polish-qa.md` |
| 06 | Audit fixes (recycle, overdraw, dọn rác, a11y...) A1–A12 | ✅ Done | `done/06-audit-fixes.md` |
| 07 | Code cleanup + Sound Effects (SFX, best-time, test undo) | ✅ Done | `done/07-cleanup-sfx.md` |
| 08 | Engagement (Rate, More apps, About/License, GitHub) | ✅ Done | `done/08-engagement.md` |
| 09 | Tech polish (120Hz, nested weights, portrait-lock, version) | ✅ Done | `done/09-tech-polish.md` |
| 10 | Advanced glow & blur (RenderEffect frosted-glass, bloom, viền gradient động) | ⬜ Todo | `todo/10-advanced-glow-blur.md` |
| 11 | Win celebration & motion (particle/confetti, animated counter, redesign win dialog) | ⬜ Todo | `todo/11-win-celebration.md` |
| 12 | Theming đa sắc (palette switcher cyan/magenta/lime/violet, persist, runtime) | ⬜ Todo | `todo/12-multi-theme.md` |
| 13 | Settings screen riêng (gom toggle + FX/theme/haptic/reduce-motion) | ⬜ Todo | `todo/13-settings-screen.md` |

### Phase 2 — Advanced visual revamp (Wave 10–13)
Định hướng đã chốt với roy (2026-06-19): làm **cả 4 hướng**, ưu tiên **phô diễn tối đa** (chấp nhận hiệu ứng nặng nhưng MỌI hiệu ứng nặng phải có toggle tắt cho máy yếu), task **đầy đủ** như wave cũ.

**Thứ tự đề xuất:** `13` (Settings — host cho toggle FX + theme picker) → `10` (Advanced glow/blur) → `11` (Win celebration) → `12` (Theming đa sắc). Wave 10/11 có thể chạy song song nếu hardcode key default `high` rồi nối UI sau khi 13 xong.

**Tiến độ:** Phase 1 (Neon revamp) 9/9 done · Phase 2 (10–13) 0/4 todo · verify: `lintDebug` + `testDebugUnitTest` + `connectedDebugAndroidTest` **19/19 PASS** (Pixel 7 Pro / Android 16), logcat sạch, không jank, WCAG AA pass.

**Wave 2 (07–09) — DONE:** 07 ✅ (SFX + best-time + dọn layout) · 08 ✅ (About/License/GitHub; Rate/More/Share đã có sẵn) · 09 ✅ (120Hz + phẳng nested weights + portrait-lock decision). Verify: **25/25 instrumented PASS** Pixel 7 Pro (anim=0), lint sạch (DiscouragedApi + NestedWeights = 0). P4 version-bump hoãn chủ ý sang pass riêng.

## Definition of Done (toàn cục)
- [x] Mọi màn dùng token neon, nền tối, không còn `#FFFBFE`/`#F44336` chủ đạo.
- [x] `lintDebug` + `testDebugUnitTest` + `connectedDebugAndroidTest` PASS.
- [x] Smoke mọi màn: render đúng, không crash, không jank khi slide.
- [x] Tương phản chữ ≥ WCAG AA (đo: text 16.2 / cyan 13.8 / lime 15.5 / magenta 6.7).
- [x] Không hồi quy: font-scale guard, toggle số, undo, highscore.
- [x] Dọn drawable/màu/anim cũ không dùng (06: 10 file + red theme; 05: 7 anim).
