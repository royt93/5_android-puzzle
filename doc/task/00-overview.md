# 00 — Overview & Spec (Neon/Glow Revamp)

> Tài liệu kỹ thuật nền (không phải task). Mọi file task trong `todo/ inprogress/ done/` tham chiếu spec này.

## Hướng thiết kế đã chốt
- **Candy-neon bổ trợ pastel**: nền tối deep-navy + glow mềm (cyan/magenta/lime). Giữ ảnh ghép pastel làm điểm sáng; chrome UI quanh ảnh phát glow nhẹ.
- **Triển khai**: big-bang mọi màn, nhưng Foundation (tokens + primitive) phải đi trước về kỹ thuật.
- **Ảnh ghép**: bổ sung bộ ảnh neon tự tạo bằng vector/canvas, đặt cạnh bộ pastel.

## Design tokens (khóa cứng — `res/values/colors.xml`)
Không hard-code hex trong layout; chỉ tham chiếu token. Bản sao Kotlin cho canvas: `util/NeonPalette.kt`.

| Nhóm | Token | Hex |
|---|---|---|
| Nền | `neon_bg_deep` / `neon_bg_surface` / `neon_bg_elevated` / `neon_bg_scrim` | `#0E1230` / `#161B3D` / `#232A63` / `#CC0A0E24` |
| Accent | `neon_cyan` / `neon_magenta` / `neon_lime` / `neon_violet` | `#38F9E4` / `#FF5DCB` / `#C6FF4E` / `#8A6CFF` |
| Glow (40%) | `neon_cyan_glow` / `neon_magenta_glow` / `neon_lime_glow` / `glass_stroke` | `#6638F9E4` / `#66FF5DCB` / `#66C6FF4E` / `#33FFFFFF` |
| Text | `neon_text_primary` / `neon_text_secondary` / `neon_text_on_accent` | `#EAF2FF` / `#9FB0D9` / `#0E1230` |

## Kỹ thuật glow & fallback (minSdk 23)
| Hiệu ứng | Kỹ thuật | API note |
|---|---|---|
| Glow chữ (title/timer/số) | `shadowLayer` | ưu tiên (rẻ); glow rõ từ API 28 |
| Glow viền tile/halo board (canvas) | **layered-stroke** nhiều lớp alpha giảm dần | chạy hardware layer, không cần software → đã chọn thay `BlurMaskFilter` |
| Glow shadow view | `outlineSpotShadowColor`/`outlineAmbientShadowColor` (`NeonGlow.kt`) | API 28+, fallback no-op |
| Bloom nền | `<gradient type="radial">` (`neon_bg_ambient`) | mọi API |
| Blur thật (polish, tùy chọn) | `RenderEffect.createBlurEffect` | API 31+ only |

**Nguyên tắc hiệu năng**: ưu tiên shadowLayer/stroke tĩnh thay blur runtime; chỉ glow tile-active + halo (không glow toàn bộ tile/frame); cache Paint/Rect, không cấp phát trong `onDraw`.

## Glow primitives tái dùng (`res/drawable/`)
`neon_bg_ambient`, `neon_glass_panel`, `neon_glass_card`, `neon_btn_primary`, `neon_btn_danger`, `neon_btn_success`, `neon_ring` + biến thể `TextAppearance.App.*.Glow`.

## Risk register
| Rủi ro | Mức | Giảm thiểu |
|---|---|---|
| Neon xung đột pastel | Cao | glow mềm + giữ pastel focal |
| Overdraw / tụt FPS | Cao | shadowLayer/stroke thay blur; glow chọn lọc; profile thiết bị |
| Tương phản chữ fail WCAG | TB | text `#EAF2FF` trên nền tối; kiểm AA ≥ 4.5:1 |
| Fallback API 28/31 | TB | luôn có nhánh layered-drawable |
| Lật dark base vỡ status/nav bar | TB | đổi theme cha + bars ở Foundation trước |
