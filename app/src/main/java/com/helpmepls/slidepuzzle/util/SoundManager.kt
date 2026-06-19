package com.helpmepls.slidepuzzle.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.helpmepls.slidepuzzle.R

/**
 * Quan ly SFX game bang [SoundPool]. Load lazy/async tu `res/raw`, release khi Activity destroy.
 * Tach rieng khoi GameBoard (View khong phu thuoc audio) — Activity so huu va goi.
 *
 * - [isEnabled] dieu khien phat (persist o tang goi, vd SharedPreferences `sound_enabled`).
 * - `play(...)` truoc khi load xong la no-op an toan (soundId chua san sang).
 */
class SoundManager(context: Context) {

    /** Bat/tat phat am. Khi false, [playMove]/[playWin] khong phat gi. */
    var isEnabled: Boolean = true

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(MAX_STREAMS)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val moveSoundId: Int = soundPool.load(context.applicationContext, R.raw.sfx_move, 1)
    private val winSoundId: Int = soundPool.load(context.applicationContext, R.raw.sfx_win, 1)

    /** Am khi truot 1 manh hop le. Volume vua phai de khong gay kho chiu khi choi lien tuc. */
    fun playMove() {
        if (!isEnabled) return
        soundPool.play(moveSoundId, MOVE_VOLUME, MOVE_VOLUME, 1, 0, 1.0f)
    }

    /** Am khi giai xong (tang am hon move mot chut). */
    fun playWin() {
        if (!isEnabled) return
        soundPool.play(winSoundId, WIN_VOLUME, WIN_VOLUME, 1, 0, 1.0f)
    }

    /** Giai phong tai nguyen audio. Goi trong Activity.onDestroy. */
    fun release() {
        soundPool.release()
    }

    private companion object {
        const val MAX_STREAMS = 4
        const val MOVE_VOLUME = 0.6f
        const val WIN_VOLUME = 0.9f
    }
}
