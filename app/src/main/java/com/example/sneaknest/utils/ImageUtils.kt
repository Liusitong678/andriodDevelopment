package com.example.sneaknest.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.sneaknest.R
import com.google.firebase.storage.FirebaseStorage

object ImageUtils {

    private const val TAG = "ImageUtils"
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    const val IMAGE_DETAIL_TYPE    = "detail"
    const val IMAGE_THUMBNAIL_TYPE = "thumbnail"


    private fun getThumbnailImageResourceByName(name: String): Int = when (name) {
        "Nike"          -> R.drawable.nike
        "Nike Men"      -> R.drawable.nike2
        "Asics"         -> R.drawable.asics
        "Adidas"        -> R.drawable.adidas
        "Under Armour"  -> R.drawable.ua
        else            -> R.drawable.placeholder_image
    }

    private fun getDetailImageResourceByName(name: String): Int = when (name) {
        "Nike"          -> R.drawable.nike
        "Nike Men"      -> R.drawable.nike2
        "Asics"         -> R.drawable.asics
        "Adidas"        -> R.drawable.adidas
        "Under Armour"  -> R.drawable.ua
        else            -> R.drawable.placeholder_image
    }

    fun getImageResource(productName: String, type: String): Int = when (type) {
        IMAGE_DETAIL_TYPE    -> getDetailImageResourceByName(productName)
        IMAGE_THUMBNAIL_TYPE -> getThumbnailImageResourceByName(productName)
        else                 -> R.drawable.placeholder_image
    }


    fun loadImageFromStorage(
        context: Context?,
        imageView: ImageView?,
        imageUri: String?,
        name: String,
        type: String
    ) {
        if (context == null || imageView == null || imageUri.isNullOrBlank()) {
            loadFallbackImage(context ?: return, imageView ?: return, name, type)
            return
        }

        // 如果是下载直链，直接 Glide
        if (imageUri.startsWith("http")) {
            Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.placeholder_image)
                .error(getImageResource(name, type))
                .into(imageView)
            return
        }

        try {
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri)
            ref.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(getImageResource(name, type))
                    .into(imageView)
            }.addOnFailureListener {
                loadFallbackImage(context, imageView, name, type)
            }
        } catch (e: Exception) {
            loadFallbackImage(context, imageView, name, type)
        }
    }

    private fun loadFallbackImage(context: Context, imageView: ImageView, name: String, type: String) {
        Glide.with(context)
            .load(getImageResource(name, type))
            .error(R.drawable.placeholder_image)
            .into(imageView)
    }
}
