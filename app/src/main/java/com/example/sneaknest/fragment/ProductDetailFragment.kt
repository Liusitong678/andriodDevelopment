package com.example.sneaknest.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.sneaknest.*
import com.example.sneaknest.enums.CollectionName
import com.example.sneaknest.listener.ToolbarTitleListener
import com.example.sneaknest.model.CartItem
import com.example.sneaknest.model.Product
import com.example.sneaknest.utils.FirebaseAuthUtils
import com.example.sneaknest.utils.ImageUtils
import com.google.firebase.database.FirebaseDatabase

class ProductDetailFragment : Fragment() {

    // ---- UI ----
    private lateinit var productImage: ImageView
    private lateinit var nameText: TextView
    private lateinit var priceText: TextView
    private lateinit var descText: TextView
    private lateinit var stockText: TextView
    private lateinit var quantityInput: EditText
    private lateinit var addBtn: Button

    // ---- Data ----
    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = arguments?.getSerializable(ARG_PRODUCT) as? Product
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_product_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view) {
            productImage   = findViewById(R.id.productImage)
            nameText       = findViewById(R.id.productName)
            priceText      = findViewById(R.id.productPrice)
            descText       = findViewById(R.id.productDescription)
            stockText      = findViewById(R.id.stock)
            quantityInput  = findViewById(R.id.quantityInput)
            addBtn         = findViewById(R.id.addToCartButton)
        }

        product?.let { p ->
            nameText.text  = p.productName
            priceText.text = String.format("$%.2f", p.price ?: 0.0)
            descText.text  = p.description
            stockText.text = "Stock: ${p.stock ?: 0}"

            ImageUtils.loadImageFromStorage(
                context,
                productImage,
                p.productImageDetailUrl ?: "",
                p.productName ?: "",
                CommonConstant.IMAGE_DETAIL_TYPE
            )

            addBtn.setOnClickListener { addToCart(p) }
        }
    }


    override fun onResume() {
        super.onResume()
        (activity as? ToolbarTitleListener)?.updateToolbarTitle(getString(R.string.product_detail))
    }

    // -----------------------------------------------------------------

    private fun addToCart(prod: Product) {
        val qty = quantityInput.text.toString().trim().toIntOrNull() ?: 0
        if (qty !in 1..9) {
            Toast.makeText(context, "Quantity must be 1-9", Toast.LENGTH_SHORT).show()
            return
        }
        if ((prod.stock ?: 0) < qty) {
            Toast.makeText(context, "Not enough stock", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. 内存购物车
        CartManager.addToCart(CartItem(prod, prod.price ?: 0.0, qty))

        // 2. Firebase DB
        val uid = FirebaseAuthUtils.currentUserId
        if (uid.isNotEmpty()) {
            val cartRef = FirebaseDatabase.getInstance()
                .getReference(CollectionName.CART_ITEMS.name)
                .child(uid)
                .child((prod.productId ?: "").toString())

            cartRef.setValue(CartItem(prod, prod.price ?: 0.0, qty))
        }

        Toast.makeText(context, "$qty ${prod.productName} added", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ARG_PRODUCT = "product"

        fun newInstance(p: Product) = ProductDetailFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_PRODUCT, p) }
        }
    }
}
