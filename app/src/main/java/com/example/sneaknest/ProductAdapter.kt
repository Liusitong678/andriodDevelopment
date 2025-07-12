package com.example.sneaknest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sneaknest.model.Product
import com.example.sneaknest.utils.ImageUtils.loadImageFromStorage
import com.example.sneaknest.listener.OnProductClickListener

class ProductAdapter(
    private val productList: MutableList<Product>,
    private val listener: OnProductClickListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.productNameTextView.text = product.productName
        holder.productPriceTextView.text = "$%.2f".format(product.price)
        holder.productDescriptionTextView.text = product.description

        val context = holder.itemView.context
        val imageUrl = product.productImageUrl
        val imageName = product.productName ?: "default"

        if (!imageUrl.isNullOrEmpty()) {
            loadImageFromStorage(
                context,
                holder.productImageImageView,
                imageUrl,
                imageName,
                CommonConstant.IMAGE_THUMBNAIL_TYPE
            )
        }

        holder.itemView.setOnClickListener {
            listener.onProductItemClick(product)
        }
    }

    override fun getItemCount(): Int = productList.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productName)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPrice)
        val productDescriptionTextView: TextView = itemView.findViewById(R.id.productDescription)
        val productImageImageView: ImageView = itemView.findViewById(R.id.productImage)
    }
}
