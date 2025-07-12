package com.example.sneaknest.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sneaknest.CartAdapter
import com.example.sneaknest.CartManager
import com.example.sneaknest.HomeActivity
import com.example.sneaknest.R
import com.example.sneaknest.enums.CollectionName
import com.example.sneaknest.listener.OnCheckoutClickListener
import com.example.sneaknest.listener.ToolbarTitleListener
import com.example.sneaknest.model.CartItem
import com.google.firebase.database.*
import com.example.sneaknest.utils.FirebaseAuthUtils

class CartFragment : Fragment() {

    // --- UI ---
    private lateinit var recyclerView: RecyclerView
    private lateinit var subtotalText: TextView
    private lateinit var taxText: TextView
    private lateinit var totalPriceText: TextView
    private lateinit var checkoutButton: Button

    // --- Adapter ---
    private lateinit var cartAdapter: CartAdapter

    // --- 外部回调 ---
    private var onCheckoutClickListener: OnCheckoutClickListener? = null
    fun setOnCheckoutClickListener(listener: OnCheckoutClickListener?) {
        onCheckoutClickListener = listener
    }

    // 读取购物车列表（动态）
    private val cartItemList: MutableList<CartItem>
        get() = CartManager.getCartItems().toMutableList()

    // --------------------------------------------------------------------
    // 生命周期
    // --------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.activity_cart, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 绑定控件
        recyclerView = view.findViewById(R.id.recyclerView)
        subtotalText = view.findViewById(R.id.subtotalText)
        taxText = view.findViewById(R.id.taxText)
        totalPriceText = view.findViewById(R.id.totalPriceText)
        checkoutButton = view.findViewById(R.id.checkoutButton)

        // 2. RecyclerView & Adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(
            cartItemList,
            requireContext()
        )
        (activity as? HomeActivity)?.let { cartAdapter.setOnProductClickListener(it) }
        recyclerView.adapter = cartAdapter

        // 3. Adapter 监听：数量变动刷新 UI
        cartAdapter.setOnCartChangedListener(object : CartAdapter.OnCartChangedListener {
            override fun onCartChanged() = updateCheckoutUI()
        })

        // 4. 结账按钮
        checkoutButton.setOnClickListener {
            onCheckoutClickListener?.onCheckoutClick(cartItemList)
        }

        // 5. 首次加载
        loadCartItems()
        updateCheckoutUI()
    }

    override fun onResume() {
        super.onResume()
        (activity as? ToolbarTitleListener)
            ?.updateToolbarTitle(getString(R.string.cart))
    }

    // --------------------------------------------------------------------
    // Firebase 读取购物车
    // --------------------------------------------------------------------
    private fun loadCartItems() {
        val userId = FirebaseAuthUtils.currentUserId
        if (userId.isNullOrBlank()) return

        val ref = FirebaseDatabase.getInstance()
            .getReference(CollectionName.CART_ITEMS.name)
            .child(userId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CartManager.clearCart()
                for (data in snapshot.children) {
                    data.getValue(CartItem::class.java)?.let { CartManager.addToCart(it) }
                }
                cartItemList.clear()
                cartItemList.addAll(CartManager.getCartItems())
                cartAdapter.notifyDataSetChanged()

                updateCheckoutUI()
                Log.i(TAG, "Cart items loaded for userId=$userId")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to load cart items. userId=$userId")
            }
        })
    }

    // --------------------------------------------------------------------
    // 价格/按钮 UI 刷新
    // --------------------------------------------------------------------
    private fun updateCheckoutUI() {
        val subtotal = cartItemList.sumOf {
            (it.productItem?.price ?: 0.0) * it.totalQuantity
        }
        val tax = subtotal * 0.13
        val total = subtotal + tax

        subtotalText.text = "Subtotal: $%.2f".format(subtotal)
        taxText.text = "Tax (13%%): $%.2f".format(tax)
        totalPriceText.text = "Total: $%.2f".format(total)

        checkoutButton.apply {
            setBackgroundResource(R.drawable.checkout_button_selector)
            isEnabled = total > 0
        }
    }

    companion object {
        private const val TAG = "CartFragment"
    }
}
