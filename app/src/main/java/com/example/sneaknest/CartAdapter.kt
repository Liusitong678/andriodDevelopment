package com.example.sneaknest

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sneaknest.enums.CollectionName
import com.example.sneaknest.listener.OnProductClickListener
import com.example.sneaknest.model.CartItem
import com.example.sneaknest.model.Product
import com.example.sneaknest.utils.FirebaseAuthUtils
import com.example.sneaknest.utils.ImageUtils
import com.google.firebase.database.*

class CartAdapter(
    private val cartItemList: MutableList<CartItem>,
    private val context: Context
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    companion object {
        private const val TAG = "CartAdapter"
    }

    private var onCartChangedListener: OnCartChangedListener? = null
    private var onProductClickListener: OnProductClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItemList[position]
        val productItem = cartItem.productItem ?: return

        holder.productNameTextView.text = productItem.productName
        holder.productPriceTextView.text = String.format("$%.2f", productItem.price)
        holder.quantityTextView.text = cartItem.totalQuantity.toString()

        ImageUtils.loadImageFromStorage(
            context,
            holder.productImageView,
            productItem.productImageUrl,
            productItem.productName ?: "default",
            CommonConstant.IMAGE_THUMBNAIL_TYPE
        )

        holder.decreaseQuantityButton.setOnClickListener {
            if (cartItem.totalQuantity > 1) {
                cartItem.totalQuantity--
                cartItem.totalPrice = productItem.price * cartItem.totalQuantity
                updateOrDeleteCartItemFromDatabase(cartItem)
                notifyItemChanged(position)
            } else {
                removeCartItemFromDatabase(cartItem)
                cartItemList.removeAt(position)
                CartManager.removeFromCart(productItem.productId ?: "")
                notifyItemRemoved(position)
            }
            onCartChangedListener?.onCartChanged()
        }

        holder.increaseQuantityButton.setOnClickListener {
            if (cartItem.totalQuantity >= 9) {
                Toast.makeText(context, "The quantity has exceeded the maximum limit.", Toast.LENGTH_SHORT).show()
            } else {
                cartItem.totalQuantity++
                cartItem.totalPrice = productItem.price * cartItem.totalQuantity
                updateOrDeleteCartItemFromDatabase(cartItem)
                notifyItemChanged(position)
            }
            onCartChangedListener?.onCartChanged()
        }

        holder.removeButton.setOnClickListener {
            removeCartItemFromDatabase(cartItem)
            cartItemList.removeAt(position)
            CartManager.removeFromCart(productItem.productId ?: "")
            notifyItemRemoved(position)
            onCartChangedListener?.onCartChanged()
        }

        holder.itemView.setOnClickListener {
            onProductClickListener?.onProductItemClick(productItem)
        }
    }

    override fun getItemCount(): Int = cartItemList.size

    private fun removeCartItemFromDatabase(cartItem: CartItem) {
        val currentUserId = FirebaseAuthUtils.currentUserId
        if (!TextUtils.isEmpty(currentUserId)) {
            val productId = cartItem.productItem?.productId ?: return
            val cartRef = FirebaseDatabase.getInstance()
                .getReference(CollectionName.CART_ITEMS.name)
                .child(currentUserId)
                .child(productId)

            cartRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Removed item: $productId for user: $currentUserId")
                } else {
                    Log.w(TAG, "Failed to remove item: $productId")
                }
            }
        }
    }

    private fun updateOrDeleteCartItemFromDatabase(cartItem: CartItem) {
        val currentUserId = FirebaseAuthUtils.currentUserId
        if (!TextUtils.isEmpty(currentUserId)) {
            val productId = cartItem.productItem?.productId ?: return
            val cartRef = FirebaseDatabase.getInstance()
                .getReference(CollectionName.CART_ITEMS.name)
                .child(currentUserId)
                .child(productId)

            if (cartItem.totalQuantity == 0) {
                removeCartItemFromDatabase(cartItem)
                return
            }

            cartRef.setValue(cartItem).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Updated item: $productId for user: $currentUserId")
                } else {
                    Log.w(TAG, "Failed to update item: $productId")
                }
            }
        }
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productName)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPrice)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantity)
        val productImageView: ImageView = itemView.findViewById(R.id.productImage)
        val decreaseQuantityButton: Button = itemView.findViewById(R.id.decreaseQuantityButton)
        val increaseQuantityButton: Button = itemView.findViewById(R.id.increaseQuantityButton)
        val removeButton: Button = itemView.findViewById(R.id.removeButton)
    }

    interface OnCartChangedListener {
        fun onCartChanged()
    }

    fun setOnCartChangedListener(listener: OnCartChangedListener) {
        this.onCartChangedListener = listener
    }

    fun setOnProductClickListener(listener: OnProductClickListener) {
        this.onProductClickListener = listener
    }
}