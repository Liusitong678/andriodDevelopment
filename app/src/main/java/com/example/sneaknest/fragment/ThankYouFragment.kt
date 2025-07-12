package com.example.sneaknest.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.sneaknest.R
import com.example.sneaknest.listener.ToolbarTitleListener

class ThankYouFragment : Fragment() {
    private var backToProductsButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thank_you, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backToProductsButton = view.findViewById(R.id.backToProductsButton)

        // 返回产品列表
        backToProductsButton?.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, ProductListFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is ToolbarTitleListener) {
            (activity as ToolbarTitleListener).updateToolbarTitle(getString(R.string.order_success))
        }
    }
}
