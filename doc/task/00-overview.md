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

## Phase 2 — Advanced visual revamp (Wave 10–13)
> Nền móng Phase 1 (tokens, dark theme, primitive, canvas glow, motion, audit) đã xong. Phase 2 nâng visual lên mức cao cấp. Định hướng: **phô diễn tối đa**, kèm **toggle tắt** cho máy yếu.

### Kỹ thuật mới bổ sung
| Hiệu ứng | Kỹ thuật | API note |
|---|---|---|
| Frosted-glass / blur nền dialog | `RenderEffect.createBlurEffect` (+ColorFilter) trên View | API 31+; < 31 fallback scrim (`neon_bg_scrim`) |
| Viền gradient động | `SweepGradient` + `Matrix.postRotate` qua `ValueAnimator`, hardware layer | mọi API; tắt → viền tĩnh khi `fx_quality=low` |
| Bloom tile/win | layered-stroke (đã có) + overlay `RenderEffect` blur khi API31+ | fallback giữ 4-lớp stroke |
| Particle / confetti win | Canvas hạt + `ValueAnimator`, pool cố định, no-alloc onDraw | mọi API; tắt khi `fx_reduce_motion` |
| Animated counter | `ValueAnimator` đếm số moves/time | mọi API |

### Tokens / prefs mới (`puzzle_prefs`)
| Key | Kiểu | Default | Dùng ở |
|---|---|---|---|
| `accent_theme` | String/int | `cyan` | Wave 12 — palette chủ đạo |
| `fx_quality` | high/low | `high` | Wave 10/11 — bật blur/border/bloom/particle |
| `fx_blur` | bool | `true` (API31+) | Wave 10 — blur frosted-glass |
| `fx_reduce_motion` | bool | `false` | Wave 11 — tắt particle/sequence |
| `haptic_enabled` | bool | `true` | Wave 13 — rung khi move |

Palette accent (Wave 12), nền + glass giữ nguyên: Cyan (default) · Magenta · Lime · Violet · (tuỳ chọn Aurora). Mỗi bộ = {primary, secondary, glow, on_accent}, định nghĩa ở `colors.xml` (`theme_<name>_*`) + `NeonPalette.kt`. **Bắt buộc kiểm WCAG AA cho chữ chính mỗi palette.**

### Nguyên tắc hiệu năng Phase 2
- Mọi hiệu ứng nặng (blur, particle, border anim, bloom) **phải đọc toggle** và degrade sạch về baseline Phase 1 khi `low`/reduce-motion.
- Tôn trọng `ANIMATOR_DURATION_SCALE == 0` (test/anim=0) → skip animation.
- Profile `gfxinfo` + `overdraw` trên device thật mỗi wave; ghi `doc/device_test.md`.
- Cache Paint/Shader/Matrix; `cancel()` animator ở `onDetachedFromWindow`; clear `RenderEffect` đúng vòng đời.

## Risk register
| Rủi ro | Mức | Giảm thiểu |
|---|---|---|
| Neon xung đột pastel | Cao | glow mềm + giữ pastel focal |
| Overdraw / tụt FPS | Cao | shadowLayer/stroke thay blur; glow chọn lọc; profile thiết bị |
| Tương phản chữ fail WCAG | TB | text `#EAF2FF` trên nền tối; kiểm AA ≥ 4.5:1 |
| Fallback API 28/31 | TB | luôn có nhánh layered-drawable |
| Lật dark base vỡ status/nav bar | TB | đổi theme cha + bars ở Foundation trước |
