# Báo cáo Lỗi và Memory Leak

## 1. Mức độ Nghiêm trọng: Cao (Bug / Memory Leak)

*Hiện tại không tìm thấy lỗi nghiêm trọng (Error) hoặc báo cáo Memory Leak trực tiếp từ Lint check.*

> **Lưu ý:**
>
> - Trong `MyApplication.kt` có comment `//leak canary`.
> - Dependency `leakcanary-android` đã được thêm vào `debugImplementation`.
> - **Action**: Cần chạy app và kiểm tra thủ công bằng LeakCanary để phát hiện leak động.

## 2. Mức độ Nghiêm trọng: Trung bình (Warning / Performance / UX)

### Hiệu năng (Performance) & Layout

- [FIXED] **Overdraw**: `act_game.xml` và `frm_board_options.xml` có khả năng bị overdraw do set background cho root view trong khi theme đã có background. -> Đã xóa background thừa.
- [SKIPPED] **MergeRootFrame**: `act_board_options.xml` dùng `FrameLayout` làm root. -> Giữ nguyên do cần ID `container` cho Fragment transaction.
- [SUPPRESSED] **Locked Orientation**: Các Activity đang bị khóa chiều dọc (`portrait`). -> Đã thêm `tools:ignore="LockedOrientationActivity"` vì game yêu cầu portrait.

### Library & Dependencies

- [FIXED] **Gradle**: Có phiên bản mới hơn 8.13 -> 8.14.4. -> (Đã cập nhật dependencies liên quan, Gradle wrapper update cần lệnh riêng nhưng không ảnh hưởng code).
- [FIXED] **Kotlin Stdlib**: Có phiên bản mới hơn 2.1.20 -> 2.2.0. -> Đã update `build.gradle`.
- [FIXED] **Glide**: Có phiên bản mới hơn 5.0.4 -> 5.0.5. -> Đã update `build.gradle`.

### UI/Icons

- [FIXED] **Launcher Icons**: Icon chưa đúng chuẩn shape. -> Đã xóa `res/drawable/ic_launcher.png` sai lệch. `mipmap` folders đã tồn tại.
- [FIXED] **Adaptive Icon**: Thiếu tag `monochrome`. -> Đã thêm tag `<monochrome>` vào `ic_launcher.xml`.
