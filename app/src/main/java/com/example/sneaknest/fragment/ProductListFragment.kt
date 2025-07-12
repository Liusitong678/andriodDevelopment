package com.example.sneaknest.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import com.example.sneaknest.R
import com.example.sneaknest.ProductAdapter
import com.example.sneaknest.listener.ToolbarTitleListener
import com.example.sneaknest.listener.OnProductClickListener

import com.example.sneaknest.model.Product
import com.example.sneaknest.enums.CollectionName

import com.google.firebase.database.*

class ProductListFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var adapter: ProductAdapter? = null
    private val productList: MutableList<Product> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = GridLayoutManager(context, 2)
        recyclerView?.setHasFixedSize(true)
        adapter = ProductAdapter(productList, requireActivity() as OnProductClickListener)
        recyclerView?.adapter = adapter

        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        if (activity is ToolbarTitleListener) {
            (activity as ToolbarTitleListener).updateToolbarTitle(getString(R.string.products))
        }
    }
    // ProductListFragment.kt
    private fun loadProducts() {

        val db = FirebaseDatabase.getInstance()
        val productsRef = db.getReference(CollectionName.PRODUCTS.collectionName)   // 一定是 "products"

        productsRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                productList.clear()
                Log.d(TAG, "snapshot childrenCount = ${snapshot.childrenCount}")

                for (child in snapshot.children) {
                    val product = child.getValue(Product::class.java)
                    Log.d(TAG, "child node=${child.key}  product=$product")

                    product?.let(productList::add)
                }

                Log.d(TAG, "after fill, list size = ${productList.size}")

                adapter?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to load products: ${error.message}", error.toException())
                /* progressBar.visibility = View.GONE */
            }
        })
    }



    companion object {
        private const val TAG = "ProductListFragment"
    }
}
