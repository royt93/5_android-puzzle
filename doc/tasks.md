# Task Tracker

Cap nhat: 2026-06-20

## In progress

- Khong co.

## Todo

### Wave 3 — Gameplay, Social, Visual (kanban tai `doc/task/todo/`)
> Chot voi roy 2026-06-20: 10 tinh nang moi chon tu 4 huong. Thu tu trien khai: 32 -> 25 -> 27 -> 30 -> 26 -> 23 -> 24 -> 29 -> 31 -> 28.

**P0 — Lam ngay:**
- [todo] **25 Hint system** — flash tile sai vi tri, cooldown 3s, +10 moves penalty. Chi tiet: `doc/task/todo/25-hint-system.md`.
- [todo] **27 Share result as image** — Canvas vẽ share card (puzzle + stats + neon frame), FileProvider, Intent share. Chi tiet: `doc/task/todo/27-share-result-image.md`.
- [todo] **32 Progress bar khi sap win** — >= 80% tile dung: banner "Almost there!" + board glow tang. Chi tiet: `doc/task/todo/32-progress-bar-almost-win.md`.

**P1 — Wave ke tiep:**
- [todo] **23 Time Attack mode** — countdown Easy/Med/Hard, Time's Up dialog, bonus badge. Chi tiet: `doc/task/todo/23-time-attack-mode.md`.
- [todo] **24 Daily Puzzle** — seed theo ngay, streak counter, lock neu da giai hom nay. Chi tiet: `doc/task/todo/24-daily-puzzle.md`.
- [todo] **30 Animated tile preview** — shimmer scan line tren thumbnail khi select card. Chi tiet: `doc/task/todo/30-animated-tile-preview.md`.

**P2 — Polish:**
- [todo] **26 Move challenge** — move budget countdown, badge Perfect/Under budget. Chi tiet: `doc/task/todo/26-move-challenge.md`.
- [todo] **29 Achievement badges** — 8 badge local (Speed Demon, Minimalist, Streak...), hien trong Settings. Chi tiet: `doc/task/todo/29-achievement-badges.md`.
- [todo] **31 Custom tile shape** — Square/Rounded/Soft, persist pref, render clip path. Chi tiet: `doc/task/todo/31-custom-tile-shape.md`.

**P3 — Can content truoc:**
- [todo] **28 Unlock image packs** — giai N puzzle mo khoa pack moi, lock overlay card. Prerequisite: them anh b1-b9. Chi tiet: `doc/task/todo/28-unlock-image-packs.md`.

### Ton dong khac
- [todo] (Tuy chon - P4 hoan tu wave 2) Nang Material/Glide/Kotlin len ban moi va test ky tren thiet bi (pass rieng co retest).
- [todo] (Monetization) AdMob + AppLovin integration (can real Ad IDs ngoai source; release guard dang chan placeholder).

Wave 2 + Phase 2 - DONE:

- [done] **07 Code cleanup + SFX** — Chi tiet: `doc/task/done/07-cleanup-sfx.md`.
- [done] **08 Engagement** — Chi tiet: `doc/task/done/08-engagement.md`.
- [done] **09 Tech polish** — 120Hz, flat layout, portrait-lock. Chi tiet: `doc/task/done/09-tech-polish.md`.
- [done] **10 Advanced glow & blur** — NeonBlur (RenderEffect API31+), NeonBorderView (SweepGradient), frosted-glass dialog. Chi tiet: `doc/task/done/10-advanced-glow-blur.md`.
- [done] **11 Win celebration & motion** — particle confetti, animated counter, win dialog redesign. Chi tiet: `doc/task/done/11-win-celebration.md`.
- [done] **12 Theming da sac** — palette switcher cyan/magenta/lime/violet, WCAG AA. Chi tiet: `doc/task/done/12-multi-theme.md`.
- [done] **13 Settings screen** — SettingsActivity neon, Prefs.kt. Chi tiet: `doc/task/done/13-settings-screen.md`.
- [done] **14 Tile depth & resting glow** — Chi tiet: `doc/task/done/14-tile-depth-and-resting-glow.md`.
- [done] **15 Card neon border + labels** — Chi tiet: `doc/task/done/15-card-neon-border-and-labels.md`.
- [done] **16 Ambient board background** — Chi tiet: `doc/task/done/16-ambient-board-background.md`.
- [done] **17 Splash neon upgrade** — Chi tiet: `doc/task/done/17-splash-neon-upgrade.md`.
- [done] **18 Settings swatch active state** — Chi tiet: `doc/task/done/18-settings-swatch-active-state.md`.
- [done] **19 Footer bar & button glow** — Chi tiet: `doc/task/done/19-footer-bar-and-button-glow.md`.
- [done] **20 Win celebration v2** — Chi tiet: `doc/task/done/20-win-celebration.md`.
- [done] **21 Gallery + more images** — gallery slot + custom image picker. Chi tiet: `doc/task/done/21-gallery-and-more-images.md`.
- [done] **22 Best score per puzzle** — per-image×size leaderboard local. Chi tiet: `doc/task/done/22-best-score-leaderboard.md`.

> AdMob/AppLovin (monetization) chua dua vao wave nay theo lua chon cua roy; release guard van chan placeholder.

## Done

- [done] **Neon/Glow UI revamp** (candy-neon tren nen toi) - 6 wave kanban tai `doc/task/`: foundation tokens + dark theme, revamp moi man, GameBoard canvas glow, bo anh neon vector, audit fixes (A1-A12), motion/polish/QA. Verify: lint + unit + **19/19 instrumented PASS** (Pixel 7 Pro), WCAG AA pass, logcat sach.
- [done] Don unused resources: xoa 21 file anim + 4 drawable thua; bo color/dimen khong dung trong colors.xml/dimens.xml. `lintDebug` + `testDebugUnitTest` PASS.
- [done] BitmapTile: xoa han class, render bang `src Rect` tu anh nguon trong `GameBoard` (`PuzzleDescriptor` chi con `index`). Het cap phat bitmap con.
- [done] Them PuzzleGridInstrumentedTest + TileNumbersToggleTest; 6/6 instrumented PASS tren OPPO CPH2577 + Pixel 7 Pro (animation scale = 0). Luu y: AppFlowIntegrationTest treo tren Redmi do Espresso cho idle voi animation bat - khong phai bug app.
- [done] DialogUtils guard: bo qua khi Activity dang `isFinishing`/`isDestroyed` de tranh crash callback async.
- [done] Feature "Show Tile Numbers": toggle hien/an so thu tu qua menu game, persist `show_numbers` trong SharedPreferences.
- [done] Nang version: Gradle wrapper 8.13 -> 8.14.5, align Kotlin plugin 2.0.20 -> 2.1.20 (khop stdlib).
- [done] Fix font scale: ep Activity context dung `fontScale = 1.0f` qua `BaseActivity`.
- [done] Tao audit tong hop tai `doc/audit.md`.
- [done] Fix lint blocker `UseAppTint` trong `act_game.xml`.
- [done] Doi drawableTop sang `app:drawableTopCompat` trong `frm_board_options.xml`.
- [done] Doi `BoardOptionsAct` va `GameAct` sang `exported=false`.
- [done] Refactor image flow de adapter load drawable resource id thay vi giu 27 `Bitmap`.
- [done] Bo static Bitmap handoff; `GameAct` nhan image id va board size qua Intent.
- [done] Dat default board size trong `BoardOptionsVm`.
- [done] Harden touch handling trong `GameBoard`: chi move tren `ACTION_UP`, goi `performClick()`, guard toa do ngoai board.
- [done] Doi timer format sang `Locale.US`.
- [done] Chuyen `BoardOptionsFrm` tu `onActivityCreated` sang `onViewCreated`.
- [done] Them JUnit va unit test JVM nho cho game state/board size.
- [done] Doi transition name cua image card sang resource id de tranh trung ten khi title rong.
- [done] `./gradlew testDebugUnitTest` PASS va da co test source chay that.
- [done] `./gradlew lintDebug` PASS.
- [done] Cap nhat `doc/audit.md`, `doc/memory_leak.md`, `doc/quick_win.md`.
- [done] Build/cai/launch/smoke test tren Pixel 7 Pro.
- [done] Doc logcat theo PID app va luu ket qua tai `doc/device_test.md`.
- [done] Test font scale tren Pixel 7 Pro voi system `font_scale = 1.8`; app giu UI font gan 100%.
- [done] Bo Glide thumbnail `SIZE_ORIGINAL`, load thumbnail theo kich thuoc card.
- [done] Decode board bitmap co `inSampleSize` theo display width.
- [done] Sua button spring touch: chi fire click tren `ACTION_UP` trong bounds.
- [done] Them core `SlidingPuzzleState` thuan Kotlin va unit test move/shuffle/solved.
- [done] Them Android widget test cho font scale.
- [done] Them Android integration test cho flow options -> game -> shuffle dialog.
- [done] Them Glide compiler va `AppGlideModule` de bo generated module warning.
- [done] Them release guard: fail release build neu AdMob ID con placeholder.
- [done] Don deprecated status bar/transition usage trong code chinh.
- [done] `./gradlew connectedDebugAndroidTest` PASS tren Pixel 7 Pro voi widget test + integration test.
- [done] `./gradlew preReleaseBuild` bi chan dung ky vong khi thieu AdMob ID that.
