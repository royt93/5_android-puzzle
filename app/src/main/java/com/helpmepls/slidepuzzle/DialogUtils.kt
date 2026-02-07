package com.helpmepls.slidepuzzle

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

object DialogUtils {

    fun showGameDialog(
        context: Context,
        title: String,
        message: String,
        yesText: String = "YES",
        noText: String = "NO",
        onYes: () -> Unit,
        onNo: (() -> Unit)? = null
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_game_custom, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Bind Views
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
        val btnYes = dialogView.findViewById<Button>(R.id.btnYes) // Depending on layout, might be AppCompatButton or Button
        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)

        tvTitle.text = title
        tvMessage.text = message
        btnYes.text = yesText
        btnNo.text = noText

        // Handle clicks with Dismiss
        btnYes.setOnClickListener {
            dialog.dismiss()
            onYes()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
            onNo?.invoke()
        }
        
        // Show dialog
        dialog.show()
    }
}
