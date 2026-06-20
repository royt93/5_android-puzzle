package com.helpmepls.slidepuzzle.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.content.FileProvider
import java.io.File
import java.util.Locale

object ShareUtils {

    fun formatTime(seconds: Int): String =
        String.format(Locale.US, "%02d:%02d", seconds / 60, seconds % 60)

    fun createShareBitmap(ctx: Context, puzzleBitmap: Bitmap, moves: Int, timeSeconds: Int): Bitmap {
        val w = 800; val h = 900
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)

        // Nền deep navy
        c.drawColor(0xFF0E1230.toInt())

        // Puzzle thumbnail
        val thumbRect = RectF(80f, 60f, 720f, 700f)
        c.drawBitmap(puzzleBitmap, null, thumbRect, null)

        // Neon cyan border quanh thumbnail
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 4f
            color = NeonPalette.CYAN
            setShadowLayer(16f, 0f, 0f, NeonPalette.CYAN_GLOW)
        }
        c.drawRoundRect(thumbRect, 16f, 16f, borderPaint)

        // Stats text
        val statsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = NeonPalette.TEXT_PRIMARY
            textSize = 44f
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(8f, 0f, 0f, NeonPalette.CYAN_GLOW)
        }
        c.drawText("$moves moves  •  ${formatTime(timeSeconds)}", 80f, 770f, statsPaint)

        // App tag
        val tagPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = NeonPalette.TEXT_SECONDARY
            textSize = 32f
        }
        c.drawText("Neon Puzzle ✨", 80f, 840f, tagPaint)

        return bmp
    }

    fun shareImage(ctx: Context, bmp: Bitmap, moves: Int, timeSeconds: Int) {
        val file = File(ctx.cacheDir, "puzzle_result.png")
        file.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 90, it) }
        val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT,
                "I solved a Neon Puzzle in $moves moves (${formatTime(timeSeconds)})! 🧩✨")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ctx.startActivity(Intent.createChooser(intent, "Share your result"))
    }
}
