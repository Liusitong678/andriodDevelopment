package com.example.sneaknest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sneaknest.fragment.CartFragment
import com.example.sneaknest.fragment.CheckoutFragment
import com.example.sneaknest.fragment.ProductDetailFragment
import com.example.sneaknest.fragment.ProductListFragment
import com.example.sneaknest.listener.OnCheckoutClickListener
import com.example.sneaknest.listener.OnProductClickListener
import com.example.sneaknest.listener.ToolbarTitleListener
import com.example.sneaknest.model.CartItem
import com.example.sneaknest.model.Product
import com.example.sneaknest.utils.FirebaseAuthUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(),
    OnProductClickListener,
    OnCheckoutClickListener,
    ToolbarTitleListener {

    companion object {
        private const val TAG = "HomeActivity"
        private const val EXIT_INTERVAL = 2_000L
    }

    private lateinit var toolbarTitle: TextView
    private lateinit var bottomNav: BottomNavigationView
    private var lastBackPressed = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbarTitle = findViewById(R.id.appTitle)
        bottomNav = findViewById(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            loadFragment(ProductListFragment())
            updateToolbarTitle(getString(R.string.products))
        }

        initBottomNav()
        initDoubleBackExit()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "HomeActivity onDestroy")
    }

    // ---------------- 接口实现 ----------------
    override fun onProductItemClick(product: Product?) {          // ③ 与接口保持一致
        product?.let { loadFragment(ProductDetailFragment.newInstance(it)) }
    }

    override fun onCheckoutClick(cartItems: List<CartItem>) {
        loadFragment(CheckoutFragment.newInstance(cartItems))
    }

    override fun updateToolbarTitle(title: String) {
        toolbarTitle.text = title
    }

    // ---------------- 私有方法 ----------------
    private fun loadFragment(fragment: Fragment) {
        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (current != null && current::class == fragment::class) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .apply { if (fragment !is ProductListFragment) addToBackStack(null) }
            .commit()
    }

    private fun initBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(ProductListFragment())
                    updateToolbarTitle(getString(R.string.products))
                    true
                }

                R.id.nav_cart -> {
                    CartFragment().apply { setOnCheckoutClickListener(this@HomeActivity) }
                        .also(::loadFragment)
                    updateToolbarTitle(getString(R.string.cart))
                    true
                }

                R.id.nav_logout -> {
                    logoutUser()
                    true
                }

                else -> false
            }
        }
    }

    private fun initDoubleBackExit() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        supportFragmentManager.popBackStack()
                    } else if (System.currentTimeMillis() - lastBackPressed < EXIT_INTERVAL) {
                        finish()
                    } else {
                        lastBackPressed = System.currentTimeMillis()
                        Toast.makeText(
                            this@HomeActivity,
                            "Press again to exit",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }

    private fun logoutUser() {
        FirebaseAuthUtils.currentUserId.takeIf { it.isNotEmpty() }?.let {
            Log.i(TAG, "User signed out: $it")
        } ?: Log.w(TAG, "No user signed in")

        FirebaseAuth.getInstance().signOut()

        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
        finish()
    }
}
