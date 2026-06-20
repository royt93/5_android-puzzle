package com.helpmepls.slidepuzzle

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
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
        onNo: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ) {
        // Tranh crash khi callback async show dialog luc Activity da finishing/destroyed.
        val activity = context as? Activity
        if (activity != null && (activity.isFinishing || activity.isDestroyed)) return

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
        
        if (onDismiss != null) {
            dialog.setOnDismissListener { onDismiss() }
        }

        // Show dialog
        dialog.show()
    }
}
