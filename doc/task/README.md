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

**Tiến độ:** 6/6 task done · verify: `lintDebug` + `testDebugUnitTest` + `connectedDebugAndroidTest` **19/19 PASS** (Pixel 7 Pro / Android 16), logcat sạch, không jank, WCAG AA pass.

## Definition of Done (toàn cục)
- [x] Mọi màn dùng token neon, nền tối, không còn `#FFFBFE`/`#F44336` chủ đạo.
- [x] `lintDebug` + `testDebugUnitTest` + `connectedDebugAndroidTest` PASS.
- [x] Smoke mọi màn: render đúng, không crash, không jank khi slide.
- [x] Tương phản chữ ≥ WCAG AA (đo: text 16.2 / cyan 13.8 / lime 15.5 / magenta 6.7).
- [x] Không hồi quy: font-scale guard, toggle số, undo, highscore.
- [x] Dọn drawable/màu/anim cũ không dùng (06: 10 file + red theme; 05: 7 anim).
