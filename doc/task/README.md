# Neon/Glow UI Revamp — Tổng quan & Kế hoạch

Cập nhật: 2026-06-19. Chủ sở hữu: roy.

## 1. Mục tiêu & hướng thiết kế đã chốt

- **Hướng:** Candy-neon bổ trợ pastel. Nền tối deep-navy + glow mềm (cyan/magenta/lime). **Giữ ảnh ghép pastel làm điểm sáng**, chrome UI quanh ảnh phát glow nhẹ.
- **Triển khai:** Big-bang (revamp mọi màn), nhưng kỹ thuật bắt buộc **Wave 1 Foundation đi trước** vì mọi màn phụ thuộc token + glow primitive.
- **Ảnh ghép:** Bổ sung bộ ảnh tông neon **tự tạo bằng XML vector / canvas** (không dùng raster bản quyền), đặt cạnh bộ pastel hiện có. Xem `wave-4-neon-images.md`.
- **Phạm vi màn:** Splash, BoardOptions (toolbar + spinner + grid card + footer), Game (info bar + GameBoard canvas + dock + dialog). Tất cả.

## 2. Design tokens (khóa cứng — mọi màn dùng chung)

> Thêm vào `res/values/colors.xml`. Không hard-code hex trong layout; chỉ tham chiếu token.

### Nền (dark base)
| Token | Hex | Dùng cho |
|---|---|---|
| `neon_bg_deep` | `#0E1230` | Nền gốc toàn app (window background) |
| `neon_bg_surface` | `#161B3D` | Panel/card nền |
| `neon_bg_elevated` | `#232A63` | Phần tử nổi (dock, dialog) |
| `neon_bg_scrim` | `#CC0A0E24` | Lớp phủ tối sau dialog |

### Glow accent
| Token | Hex | Vai trò |
|---|---|---|
| `neon_cyan` | `#38F9E4` | Accent chính, tile-active, title glow |
| `neon_magenta` | `#FF5DCB` | Accent phụ, nút nguy hiểm (NO/Reset) |
| `neon_lime` | `#C6FF4E` | Success, YES, highscore |
| `neon_violet` | `#8A6CFF` | Accent trang trí, gradient phụ |

### Glow alpha (stroke/shadow — dùng cho viền & shadowLayer)
| Token | Hex | Ghi chú |
|---|---|---|
| `neon_cyan_glow` | `#6638F9E4` | cyan 40% — viền/halo |
| `neon_magenta_glow` | `#66FF5DCB` | magenta 40% |
| `neon_lime_glow` | `#66C6FF4E` | lime 40% |
| `glass_stroke` | `#33FFFFFF` | viền glass mảnh |

### Text
| Token | Hex | Dùng cho |
|---|---|---|
| `neon_text_primary` | `#EAF2FF` | Chữ chính (trắng lạnh) |
| `neon_text_secondary` | `#9FB0D9` | Chữ phụ |
| `neon_text_on_accent` | `#0E1230` | Chữ trên nền accent sáng |

## 3. Kỹ thuật glow (quyết định & fallback minSdk 23)

| Hiệu ứng | Kỹ thuật | minSdk note |
|---|---|---|
| Glow chữ (title, timer, số tile) | `setShadowLayer()` / `android:shadowColor+shadowRadius+shadowDx/Dy` | OK mọi API. **Rẻ — ưu tiên dùng** |
| Glow viền tile / active highlight (canvas) | `Paint.maskFilter = BlurMaskFilter(r, NORMAL)` trong `GameBoard` | BlurMaskFilter cần software layer → set `setLayerType(SOFTWARE)` cho paint glow hoặc dùng có chọn lọc. **Giới hạn số lần vẽ/frame** |
| Glow panel/nút (view) | Tô shadow elevation màu neon: `android:outlineSpotShadowColor` / `outlineAmbientShadowColor` | **API 28+**. Fallback <28: dùng layered drawable (ring gradient) |
| Bloom nền (ambient) | `<gradient type="radial">` drawable đặt sau nội dung | OK mọi API |
| Blur thật (tùy chọn polish) | `RenderEffect.createBlurEffect()` | **API 31+** only — chỉ dùng như tăng cường, luôn có nhánh không-blur |

**Nguyên tắc hiệu năng:** audit cũ đã cảnh báo overdraw. Glow nhân overdraw → (a) ưu tiên shadowLayer/gradient tĩnh thay vì blur runtime; (b) trong `GameBoard` chỉ glow tile-active + halo board, KHÔNG glow toàn bộ 64 tile mỗi frame; (c) cache Paint/Rect, không cấp phát trong `onDraw`; (d) đo lại trên OPPO CPH2577 (máy tầm trung).

## 4. Glow primitives tái dùng (build 1 lần, dùng mọi nơi)

- `drawable/neon_glass_panel.xml` — panel glass nền tối + viền glow.
- `drawable/neon_glass_card.xml` — card ảnh nền tối + viền cyan glow nhẹ.
- `drawable/neon_btn_primary.xml`, `neon_btn_danger.xml` — nút glass + ripple + viền glow.
- `drawable/neon_bg_ambient.xml` — radial bloom nền.
- `drawable/neon_divider.xml`, `neon_ring.xml` — viền/halo tròn.
- Style `TextAppearance.App.*` thêm biến thể `.Glow` (shadowLayer).
- Helper Kotlin (tùy chọn) `NeonGlow` cho view set outlineSpotShadowColor có guard API.

## 5. Risk register

| Rủi ro | Mức | Giảm thiểu |
|---|---|---|
| Neon xung đột ảnh pastel | Cao | Glow mềm + giữ pastel làm focal; ảnh neon mới đặt cùng nhóm riêng |
| Overdraw / tụt FPS khi animate | Cao | shadowLayer thay blur; glow có chọn lọc; profile trên OPPO |
| Tương phản chữ neon fail WCAG | Trung bình | text trắng lạnh `#EAF2FF` trên nền tối; kiểm AA ≥ 4.5:1 |
| Fallback API (28 shadow tint, 31 blur) | Trung bình | luôn có nhánh layered-drawable; test trên Android < 9 nếu có máy |
| Lật dark base vỡ status/nav bar, dialog Material3 sáng | Trung bình | đổi theme cha + status/nav bar trong Wave 1 trước khi đụng màn |

## 6. Roadmap (thứ tự thực thi)

1. **Wave 1 — Foundation** (`wave-1-foundation.md`): tokens, lật dark theme, glow primitives, typography glow. *Chặn mọi wave sau.*
2. **Wave 2 — Screens** (`wave-2-screens.md`): Splash, BoardOptions, Game, Dialog dùng primitives.
3. **Wave 3 — GameBoard canvas** (`wave-3-gameboard-canvas.md`): glow tile-active, halo board, glow số.
4. **Wave 4 — Neon images** (`wave-4-neon-images.md`): bộ ảnh neon XML/canvas + tích hợp `PREDEFINED_IMAGES`.
5. **Wave 5 — Motion & QA** (`wave-5-motion-polish-qa.md`): pulse glow, transitions, contrast/perf/lint/test trên OPPO.

## 7. Acceptance toàn cục (Definition of Done)

- [ ] Mọi màn dùng token neon, không còn nền sáng `#FFFBFE`/đỏ `#F44336` chủ đạo.
- [ ] `./gradlew lintDebug testDebugUnitTest` PASS; `connectedDebugAndroidTest` PASS trên OPPO (animation scale = 0).
- [ ] Smoke tay trên OPPO mọi màn: render đúng, không crash, FPS mượt khi shuffle/slide.
- [ ] Tương phản chữ chính ≥ WCAG AA.
- [ ] Không hồi quy: font-scale guard, toggle số, undo, highscore vẫn hoạt động.

## 8. Status legend
`[ ]` todo · `[~]` đang làm · `[x]` xong+verify · `[!]` blocked · `[-]` bỏ
