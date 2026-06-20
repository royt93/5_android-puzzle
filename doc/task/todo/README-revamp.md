# Revamp Plan — Neon/Glow Tỏa Sáng

**Ngày tạo:** 2026-06-20  
**Thiết bị test:** Pixel 7 Pro (2B051FDH3006MU)  
**Trạng thái hiện tại:** UI đã có nền neon nhưng glow quá nhạt, thiếu depth và visual impact

---

## Phân tích từ screenshots thực tế

| Màn hình | Vấn đề chính |
|---|---|
| **Game board** | Tile khoảng cách 3px quá mỏng, tile nghỉ không có glow, trông như 1 ảnh bị cắt |
| **Board Options** | Card border 1dp alpha 40% — gần như tàng hình; title label bị ẩn |
| **Settings** | Swatch active không phân biệt được — functional bug |
| **Splash** | Logo + progress bar — tối giản quá, không ấn tượng |
| **Buttons** | UNDO dùng system icon; footer bar icon/text quá mờ |

---

## Tasks theo priority

| Task | Tên | Priority | Effort |
|------|-----|----------|--------|
| [14](14-tile-depth-and-resting-glow.md) | Tile Depth & Resting Glow | **P0** | Medium |
| [15](15-card-neon-border-and-labels.md) | Card Neon Border + Labels | **P0** | Small |
| [16](16-ambient-board-background.md) | Ambient Board Background | P1 | Small |
| [17](17-splash-neon-upgrade.md) | Splash Neon Upgrade | P1 | Medium |
| [18](18-settings-swatch-active-state.md) | Settings Swatch Active State | P1 | Small |
| [19](19-footer-bar-and-button-glow.md) | Footer Bar & Button Glow | P2 | Small |

---

## Thứ tự implement đề xuất

1. **Task 15** (card border + label) — XML only, zero risk, ngay lập tức thấy kết quả
2. **Task 14** (tile depth + resting glow) — 1 file Kotlin, impact cao nhất
3. **Task 18** (swatch active) — fix UX gap, ~20 dòng code
4. **Task 16** (ambient bloom) — thêm drawable + 1 view trong XML
5. **Task 17** (splash) — medium effort, polish
6. **Task 19** (button glow) — polish pass cuối

---

## Design principles cho revamp này

- **Glow = radiating light**: mỗi element neon phải *phát sáng ra ngoài*, không chỉ có màu
- **Depth via spacing**: gap giữa tile tối → làm nổi bật từng tile
- **Consistent accent**: `?attr/colorPrimary` làm anchor cho mọi glow stroke
- **No clip**: `clipChildren=false` để shadow/glow không bị cắt bởi parent bounds
