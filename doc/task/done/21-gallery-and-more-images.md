# Task 21 — Thêm ảnh + Chọn ảnh từ thư viện

**Priority:** P0 — tăng replayability, content là lý do user quay lại  
**Effort:** Medium-Large (2 phần độc lập: built-in images + gallery picker)

---

## Hiện trạng

App có 18 ảnh built-in (`i1..i9`, `a1..a9`) trong `res/drawable/`. Không có chức năng chọn ảnh từ gallery. GridView chỉ hiển thị 18 card cố định.

---

## P1 — Thêm bộ ảnh built-in mới (6–9 ảnh)

### Thiết kế
Thêm bộ thứ 3 với chủ đề khác (vd: space/galaxy neon, animals khác, landscapes pixel art). Đặt tên `b1..b9`.

### Thực hiện
1. Thêm file `res/drawable/b1.png .. b9.png` (hoặc webp, khuyến nghị webp cho nhỏ hơn ~40%)
2. Trong `BoardOptionsVm.kt`, thêm vào `PREDEFINED_IMAGES`:
   ```kotlin
   TitledResourcePair(first = R.drawable.b1, second = ""),
   // ... b2..b9
   ```
3. Grid tự load thêm — không cần thay đổi adapter hay layout

**Acceptance:**
- [ ] 27 ảnh hiển thị trong grid, scroll mượt
- [ ] Glide cache đúng, không OOM khi scroll nhanh
- [ ] Mỗi ảnh build OK (lint sạch, không warning large bitmap)

---

## P2 — Gallery picker (chọn ảnh từ thiết bị)

### UX Flow
Ô cuối grid (hoặc header thứ 2) là **"+ Photo"** card:
```
[ảnh 1][ảnh 2]
[ảnh 3][ảnh 4]
...
[+ Photo]  ← tap → mở gallery picker
```

Khi user chọn ảnh → crop về 1:1 (dùng intent crop hoặc thư viện) → launch GameAct với `Uri` thay vì `imageResId`.

### Thực hiện

**Step 1 — "Add Photo" card trong adapter:**

Trong `TitledCardInfo`, thêm field `isAddPhotoSlot: Boolean = false`.

`ImageCardsAdt.getView()`:
```kotlin
if (card.isAddPhotoSlot) {
    // Inflate layout khác hoặc show neon "+" icon + "Your Photo" text
    holder.imageView.setImageResource(R.drawable.ic_add_photo_neon)
    holder.titleView.text = "YOUR PHOTO"
    holder.titleView.visibility = View.VISIBLE
}
```

Thêm 1 slot cuối trong `BoardOptionsVm`:
```kotlin
TitledResourcePair(first = 0, second = "YOUR_PHOTO_SLOT")  // id=0 = gallery marker
```

**Step 2 — Permission + Picker:**

Trong `BoardOptionsFrm.kt`, khi click vào slot gallery:
```kotlin
// Android 13+ dùng READ_MEDIA_IMAGES, cũ hơn dùng READ_EXTERNAL_STORAGE
val permission = if (Build.VERSION.SDK_INT >= 33)
    Manifest.permission.READ_MEDIA_IMAGES
else
    Manifest.permission.READ_EXTERNAL_STORAGE

if (checkSelfPermission(permission) == GRANTED) {
    launchGalleryPicker()
} else {
    requestPermissions(arrayOf(permission), REQ_GALLERY)
}
```

```kotlin
private fun launchGalleryPicker() {
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intent.type = "image/*"
    galleryLauncher.launch(intent)
}
```

**Step 3 — Nhận Uri và decode:**

```kotlin
val galleryLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    val uri = result.data?.data ?: return@registerForActivityResult
    // Decode bitmap crop về square với inSampleSize
    val bitmap = decodeCroppedBitmap(uri, targetSize = 512)
    // Launch GameAct với bitmap (pass qua file cache, không qua Intent trực tiếp)
    val cacheFile = saveBitmapToCache(bitmap, context)
    val intent = Intent(activity, GameAct::class.java).apply {
        putExtra(GameAct.EXTRA_CUSTOM_IMAGE_PATH, cacheFile.absolutePath)
        putExtra(GameAct.EXTRA_BOARD_WIDTH, boardSize.width)
        putExtra(GameAct.EXTRA_BOARD_HEIGHT, boardSize.height)
    }
    startActivity(intent)
}
```

**Step 4 — GameAct nhận custom image:**

```kotlin
// Trong GameAct.setupBoard():
val customPath = intent.getStringExtra(EXTRA_CUSTOM_IMAGE_PATH)
if (customPath != null) {
    val bmp = BitmapFactory.decodeFile(customPath)
    boardView.resize(size, image = bmp, shuffle = true)
} else {
    // flow cũ với imageResId
}
```

**Step 5 — Manifest:**
```xml
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

### Crop về square

```kotlin
fun decodeCroppedBitmap(uri: Uri, targetSize: Int): Bitmap {
    // 1. Decode kích thước gốc
    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
    // 2. tính inSampleSize
    val inSampleSize = calculateInSampleSize(opts, targetSize, targetSize)
    // 3. Decode full
    val fullOpts = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
    val bitmap = contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, fullOpts)
    } ?: return Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
    // 4. Center-crop to square
    val side = minOf(bitmap.width, bitmap.height)
    val x = (bitmap.width - side) / 2
    val y = (bitmap.height - side) / 2
    return Bitmap.createBitmap(bitmap, x, y, side, side)
}
```

---

## Files ảnh hưởng

- `BoardOptionsVm.kt` — thêm ảnh mới vào PREDEFINED_IMAGES + gallery slot
- `BoardOptionsFrm.kt` — permission, gallery launcher, click handler
- `ImageCardsAdt.kt` — render gallery slot khác với normal card
- `GameAct.kt` — nhận `EXTRA_CUSTOM_IMAGE_PATH`, decode bitmap
- `AndroidManifest.xml` — READ_MEDIA_IMAGES permission
- `res/drawable/ic_add_photo_neon.xml` — vector icon "+" camera neon (tạo mới)
- `res/drawable/b1..b9.*` — ảnh mới (bạn cung cấp)

## Acceptance tổng

- [ ] 27+ ảnh built-in hiển thị và chơi được
- [ ] Tap "YOUR PHOTO" → xin permission đúng → mở gallery
- [ ] Ảnh được crop 1:1, load vào game không crash/OOM
- [ ] Cache file được dọn khi game exit (hoặc khi chọn ảnh mới)
- [ ] Back button trên game quay lại board options đúng
