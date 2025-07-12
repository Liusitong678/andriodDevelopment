package com.example.sneaknest.utils

import android.widget.Button
import com.example.sneaknest.R

object ButtonUtils {
    fun disabledButton(button: Button) {
        button.isEnabled = false
        button.setBackgroundResource(R.drawable.button_disabled)
    }

    fun displayButton(button: Button) {
        button.isEnabled = true
        button.setBackgroundResource(R.drawable.rounded_button)
    }
}
