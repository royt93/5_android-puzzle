# Task 17 — Splash Screen Neon Upgrade

**Priority:** P1 — ấn tượng đầu tiên của app

## Vấn đề hiện tại

- Splash hiện tại: logo 256dp + progress bar + disclaimer text trên nền tối đồng nhất
- Không có tên app hiển thị rõ với glow effect
- Logo không có ring/halo neon xung quanh
- Không có ambient light hay gradient background
- Trông như màn loading placeholder, không phải splash screen của game neon

## Mục tiêu

1. Thêm app name "QUICK PUZZLE" với neon glow text (shadowLayer cyan)
2. Logo có neon ring/halo animated (pulse glow từ neon_ring drawable)
3. Background: gradient radial bloom từ trung tâm
4. Tagline ngắn (optional): "Slide. Solve. Glow."

## Thay đổi cần làm

### `activity_splash.xml`
```xml
<!-- Background: dùng neon_bg_ambient hoặc gradient drawable -->
android:background="@drawable/neon_ambient_bloom"

<!-- Logo: thêm neon ring -->
<FrameLayout center-constrained>
    <!-- Halo pulse (behind logo) -->
    <View
        android:id="@+id/logoHalo"
        android:layout_width="300dp"
        android:layout_height="300dp"
        android:background="@drawable/neon_ring_large"
        android:layout_gravity="center" />
    <ImageView logo ... android:layout_width="200dp" android:layout_height="200dp" />
</FrameLayout>

<!-- App name -->
<TextView
    android:text="QUICK PUZZLE"
    android:textSize="32sp"
    android:textStyle="bold"
    android:textColor="?attr/colorPrimary"
    android:letterSpacing="0.15"
    android:shadowColor="?attr/colorPrimary"
    android:shadowDx="0"  android:shadowDy="0"  android:shadowRadius="20" />

<!-- Tagline -->
<TextView
    android:text="Slide · Solve · Glow"
    android:textSize="14sp"
    android:textColor="@color/neon_text_secondary"
    android:letterSpacing="0.1" />
```

### `SplashActivity.kt`
Thêm pulse animation cho `logoHalo`:
```kotlin
val pulse = ObjectAnimator.ofFloat(logoHalo, "alpha", 0.3f, 0.8f, 0.3f).apply {
    duration = 1500
    repeatCount = ObjectAnimator.INFINITE
    interpolator = AccelerateDecelerateInterpolator()
}
pulse.start()
```

### Tạo `drawable/neon_ring_large.xml`
```xml
<shape android:shape="oval">
    <solid android:color="#00000000" />
    <stroke android:width="3dp" android:color="@color/neon_cyan_glow" />
</shape>
```

## File ảnh hưởng
- `app/src/main/res/layout/activity_splash.xml`
- `app/src/main/java/com/helpmepls/slidepuzzle/act/SplashActivity.kt`
- `app/src/main/res/drawable/neon_ring_large.xml` (tạo mới)

## Acceptance
- [ ] Splash hiển thị tên app rõ ràng với glow
- [ ] Logo có neon ring pulse animation
- [ ] Background có ambient gradient (không phải tối đồng nhất)
- [ ] Thời gian splash không tăng (animation chạy song song với init)
