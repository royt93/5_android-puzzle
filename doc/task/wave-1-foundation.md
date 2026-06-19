# Wave 1 — Foundation (tokens + dark base + glow primitives)

> Chặn mọi wave sau. Phải xong + build pass trước khi đụng màn.

## F1 — Bảng màu neon
- [x] `res/values/colors.xml`: thêm toàn bộ token ở README §2 (nền, glow accent, glow alpha, text). Giữ lại token cũ đang dùng (`board_active`, `colorPrimary`, `white`, `black`) cho tới khi gỡ ở từng màn.
- **Acceptance:** build pass, token resolve được, chưa đổi giao diện.

## F2 — Lật theme sang dark base
- [x] `res/values/styles.xml` `AppTheme`: đổi `android:windowBackground` → `@color/neon_bg_deep`; map `colorSurface`/`colorBackground` → token nền tối; `colorPrimary` → `neon_cyan`; `android:statusBarColor` → `neon_bg_deep` (hoặc transparent + edge-to-edge); `navigationBarColor` → `neon_bg_deep`; `isAppearanceLightStatusBars=false`.
- [x] Cân nhắc đổi parent sang `Theme.Material3.Dark.NoActionBar` (vì candy-neon luôn nền tối, không cần DayNight).
- [x] `AppToolbarTheme`: nền `neon_bg_surface`, text `neon_text_primary`, accent cyan.
- **Acceptance:** mở app → mọi màn nền tối, status/nav bar tối, không còn vệt trắng/đỏ; chữ đọc được.

## F3 — Glow primitive drawables
- [x] `drawable/neon_bg_ambient.xml` — radial gradient bloom (cyan/magenta loãng) trên nền `neon_bg_deep`. Dùng làm window/scene background thay `bkg.jpg` + `bg_liquid_gradient`.
- [x] `drawable/neon_glass_panel.xml` — solid `neon_bg_surface` ~88% alpha, radius 24–32dp, stroke 1dp `neon_cyan_glow`.
- [x] `drawable/neon_glass_card.xml` — solid `neon_bg_surface`, radius 16dp, stroke 1dp `glass_stroke` + accent cyan nhẹ.
- [x] `drawable/neon_btn_primary.xml` — ripple cyan, solid trong suốt, stroke 1.5dp `neon_cyan`, radius 16dp.
- [x] `drawable/neon_btn_danger.xml` — như trên nhưng accent `neon_magenta` (Reset/NO).
- [x] `drawable/neon_ring.xml` — ring/halo tròn cho icon/avatar.
- **Acceptance:** preview render đúng trong Android Studio; không tham chiếu màu hard-code.

## F4 — Typography glow
- [x] `res/values/typography.xml`: đổi màu mặc định text sang `neon_text_primary`.
- [x] Thêm biến thể `.Glow` cho Title/Headline/GameTitle dùng `android:shadowColor=@color/neon_cyan_glow`, `shadowRadius=12`, `shadowDx=0`, `shadowDy=0`.
- **Acceptance:** title có halo nhẹ; chữ thường vẫn sắc nét (không bị nhòe quá).

## F5 — Helper API-guard (tùy chọn nhưng nên có)
- [x] `util/NeonGlow.kt`: hàm `applyGlow(view, color)` set `outlineSpotShadowColor`/`outlineAmbientShadowColor` khi `SDK_INT >= 28`, no-op khi thấp hơn.
- **Acceptance:** gọi an toàn ở mọi API; không crash < 28.

## F6 — Dimens glow
- [x] `res/values/dimens.xml`: thêm `glow_radius_sm=8dp`, `glow_radius_md=14dp`, `glow_stroke=1.5dp`, `neon_corner=20dp`.

## F7 — Tests (đã thêm theo yêu cầu)
- [x] Unit (JVM) `test/util/NeonPaletteTest.kt`: kiểm ARGB + alpha glow 40% của bảng màu Kotlin `NeonPalette`.
- [x] Widget `androidTest/NeonGlowWidgetTest.kt`: `NeonGlow.apply` set outline shadow (API 28+); token XML khớp `NeonPalette` (ContextCompat.getColor); 6 primitive drawable inflate được.
- [x] Integration `androidTest/NeonThemeIntegrationTest.kt`: GameAct launch + boardView hiển thị dưới neon theme (bắt lỗi thiếu color/attr khi lật dark).
- **Thêm** `main/util/NeonPalette.kt` (bản sao Kotlin của token, dùng cho canvas Wave 3).

**Build gate Wave 1:** ✅ `assembleDebug` + `lintDebug` + `testDebugUnitTest` PASS; ✅ `connectedDebugAndroidTest` 10/10 PASS trên OPPO CPH2577 (anim=0); ✅ smoke: toolbar + status/nav bar đã chuyển navy tối. Thân màn vẫn đỏ do layout background riêng — gỡ ở Wave 2.
