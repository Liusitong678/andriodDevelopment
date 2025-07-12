package com.example.sneaknest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val navigationTask = Runnable { navigateToNextScreen() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 3秒后跳转页面
        handler.postDelayed(navigationTask, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 避免内存泄漏
        handler.removeCallbacks(navigationTask)
    }

    private fun navigateToNextScreen() {
        val nextActivity = if (FirebaseAuth.getInstance().currentUser != null) {
            Log.i(TAG, "User already logged in, navigating to HomeActivity.")
            HomeActivity::class.java
        } else {
            Log.i(TAG, "No user logged in, navigating to LoginActivity.")
            LoginActivity::class.java
        }

        startActivity(Intent(this, nextActivity))
        finish()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
