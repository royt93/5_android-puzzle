# Wave 3 — GameBoard canvas neon (`game/GameBoard.kt`)

> Phần "đắt" nhất về hiệu năng. Glow CÓ CHỌN LỌC, không glow toàn bộ tile mỗi frame.

## G1 — Halo viền board
- [ ] Vẽ 1 lớp glow quanh khung board (RoundRect) bằng Paint riêng `glowPaint` với `BlurMaskFilter(glow_radius_md, NORMAL)`, màu `neon_cyan_glow`. Vẽ 1 lần/frame, ngoài vòng lặp tile.
- **Acceptance:** board có viền sáng dịu; không tụt FPS rõ rệt.

## G2 — Glow tile-active (đang trượt / highlight)
- [ ] Thay `paint.color = highlightColor` (xanh lá cứng) bằng stroke `neon_cyan` + glow `BlurMaskFilter`. CHỈ áp cho tile `active` (1 tile), không cho 64 tile.
- **Acceptance:** tile đang trượt phát sáng cyan; tile tĩnh không glow.

## G3 — Glow số thứ tự (`drawSlideTitle`)
- [ ] Khi `showNumbers`: dùng `paint.setShadowLayer(6f, 0f, 0f, neon_cyan_glow)` thay stroke trắng/đen hiện tại; cân nhắc set `textSize` hợp lý theo tileSize (hiện không set → số quá nhỏ).
- **Acceptance:** số rõ hơn, có glow nhẹ, đọc được trên ảnh sáng/tối.

## G4 — Khe tile (spacing) thành đường neon
- [ ] `tileSpacing` (3px) → vẽ line mảnh màu `neon_violet`/`glass_stroke` giữa tile để tạo cảm giác lưới phát sáng (tùy chọn, nếu không hại perf).
- **Acceptance:** lưới có cảm giác mạch neon; bỏ nếu gây overdraw nặng.

## G5 — Perf guard (BẮT BUỘC)
- [ ] Tất cả Paint/Rect/MaskFilter cấp phát ở init hoặc field, KHÔNG trong `onDraw`.
- [ ] BlurMaskFilter không chạy trên hardware layer pre-P: nếu glow biến mất hoặc giật → set `setLayerType(View.LAYER_TYPE_SOFTWARE, null)` cho riêng glow hoặc dùng `RenderEffect` (API 31+) có fallback.
- [ ] Profile khi shuffle (animation chạy liên tục) trên OPPO CPH2577: mục tiêu ~60fps, không jank kéo dài.
- **Acceptance:** GPU profiler không vượt ngưỡng đỏ kéo dài khi slide/shuffle; không cấp phát trong onDraw (kiểm Allocation Tracker).

## G6 — Hồi quy
- [ ] Move/undo/shuffle/reset/isSolved vẫn đúng; `PuzzleGridInstrumentedTest` vẫn PASS.
- **Acceptance:** logic không đổi, chỉ thêm vẽ.

**Build gate Wave 3:** smoke slide/shuffle mượt trên OPPO; instrumented test PASS.
