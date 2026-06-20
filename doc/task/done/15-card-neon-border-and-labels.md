# Task 15 — Board Options: Card Neon Border + Labels

**Priority:** P0 — màn chọn ảnh là màn đầu tiên user thấy sau splash

## Vấn đề hiện tại

- `strokeWidth="1dp"` với `strokeColor="@color/neon_cyan_glow"` (alpha 0x66=40%) → viền mờ nhạt, gần như không nhìn thấy
- `android:visibility="gone"` trên TextView title → card không có nhãn tên
- Card không có hover/selected state visual → user không biết mình đang chọn cái gì
- Thiếu "Select an image:" header có neon style rõ hơn

## Mục tiêu

1. Tăng neon border card: `strokeWidth="2dp"`, `strokeColor` → accent theme color (solid, không alpha)
2. Hiển thị title label bên dưới mỗi card (nếu có text)
3. Thêm `ripple` glow effect khi tap card
4. Header "Select an image:" tăng cỡ chữ + glow mạnh hơn

## Thay đổi cần làm

### `frm_titled_image_card.xml`
```xml
<!-- Đổi strokeColor và strokeWidth -->
card_view:strokeColor="?attr/colorPrimary"       <!-- thay vì neon_cyan_glow cố định -->
card_view:strokeWidth="2dp"                       <!-- thay vì 1dp -->

<!-- Title: bỏ visibility gone -->
android:visibility="visible"
android:textColor="?attr/colorPrimary"
android:shadowColor="?attr/colorPrimary"
android:shadowRadius="6"
android:shadowDx="0"
android:shadowDy="0"
android:textSize="12sp"
android:textStyle="bold"
android:textAllCaps="true"
android:letterSpacing="0.05"
```

### `BoardOptionsFrm.kt` (hoặc `ImageCardsAdt.kt`)
Truyền tên ảnh vào `tvTitle` khi bind — hiện tại `title.visibility = GONE` nên text không set.
Set `title.text = item.title` và `title.visibility = View.VISIBLE`.

### Header "Select an image:" trong `item_board_options_grid_header.xml` (hoặc adapter)
- `textSize`: 13sp → 15sp
- Thêm `shadowRadius="12"`, `shadowColor="?attr/colorPrimary"`
- `textColor`: `?attr/colorPrimary` thay vì secondary

### `neon_glass_card.xml` (drawable cho card background)
Thêm `stroke` mạnh hơn theo accent — hiện chỉ có `glass_stroke` (33% alpha white):
```xml
<stroke android:width="2dp" android:color="?attr/colorPrimary" />
```
(Hoặc dùng MaterialCardView strokeColor trực tiếp — ưu tiên cách này)

## File ảnh hưởng
- `app/src/main/res/layout/frm_titled_image_card.xml`
- `app/src/main/java/com/helpmepls/slidepuzzle/adp/ImageCardsAdt.kt` (hoặc tương đương)
- `app/src/main/res/layout/item_board_options_grid_header.xml` (nếu tồn tại)

## Acceptance
- [ ] Mỗi card có viền neon rõ ràng (2dp, màu accent)
- [ ] Label tên hiển thị bên dưới ảnh
- [ ] Header "Select an image:" trông như section label neon
- [ ] Tap card → ripple với màu accent
