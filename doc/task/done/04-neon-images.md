# 04 — Bộ ảnh ghép tông neon (vector + bridge Vector→Bitmap)

> **Status:** ✅ Done · **Depends:** 01 · **Verify:** chơi ảnh neon trên Pixel cắt tile đúng, không OOM. Spec: [`../00-overview.md`](../00-overview.md)

> Không dùng raster bản quyền. Tạo ảnh neon bằng vector drawable hoặc canvas, đặt **cạnh** bộ pastel (i*/a*/b*) trong `PREDEFINED_IMAGES`.

## Bối cảnh
- `vm/BoardOptionsVm.kt` có `PREDEFINED_IMAGES` = 27 ảnh (i1-9, a1-9, b1-9), pastel kawaii.
- Luồng decode: `GameAct.decodeBoardBitmap(resId)` dùng `BitmapFactory.decodeResource`. **Lưu ý:** decodeResource hoạt động tốt với raster; với **VectorDrawable phải render ra Bitmap thủ công** (Canvas) — xem N3.

## N1 — Thiết kế mẫu ảnh neon (≥ 6 mẫu)
- [ ] Mỗi ảnh là họa tiết hình học phát sáng trên nền `neon_bg_deep`: grid mạch điện, vòng tròn đồng tâm glow, sóng, tam giác Tron-style, hoa văn đối xứng. Phải **chia ô đẹp** cho 3x3…8x8 (đối xứng, không phụ thuộc chi tiết nhỏ).
- **Acceptance:** mỗi mẫu nhìn rõ khi cắt thành tile; tương phản đủ để chơi.

## N2 — Tạo dưới dạng VectorDrawable
- [ ] `drawable/neon_img_1.xml` … `neon_img_N.xml`: vector vuông (viewport 512x512), path + gradient cyan/magenta/lime, nền tối.
- **Acceptance:** preview đúng, file nhẹ.

## N3 — Cầu nối render Vector → Bitmap
- [ ] Sửa `decodeBoardBitmap` (hoặc thêm hàm) để nếu res là VectorDrawable thì `AppCompatResources.getDrawable` → vẽ vào `Bitmap.createBitmap(size,size)` qua `Canvas`, kích thước theo display width (giữ logic `inSampleSize` cho raster).
- [ ] Đảm bảo `PuzzleGrid`/`GameBoard` (render src Rect) vẫn cắt đúng (ảnh vuông).
- **Acceptance:** chọn ảnh neon → vào game cắt tile đúng, không méo, không OOM.

## N4 — (Phương án thay thế) Canvas-generated procedural
- [ ] Nếu vector khó "đẹp": tạo `NeonImageFactory.generate(seed,size): Bitmap` vẽ procedural (gradient + glow shapes theo seed). Cache theo seed+size.
- **Acceptance:** mỗi seed ra 1 ảnh ổn định, đẹp, rẻ.

## N5 — Tích hợp danh sách
- [ ] Thêm các ảnh neon vào `PREDEFINED_IMAGES` (nhóm riêng, có thể nhóm "Neon" tách "Pastel"). Cân nhắc nhãn nhóm trong grid.
- [ ] `frm_titled_image_card` hiển thị ảnh neon với cùng `neon_glass_card`.
- **Acceptance:** grid có cả pastel lẫn neon; chọn loại nào cũng chơi được.

## N6 — Hồi quy
- [ ] `AppFlowIntegrationTest` (chọn ảnh đầu → game) vẫn chạy; thêm test chọn 1 ảnh neon nếu khả thi.

**Build gate Wave 4:** chọn ảnh neon chơi được trên OPPO, không OOM/méo; test PASS.
