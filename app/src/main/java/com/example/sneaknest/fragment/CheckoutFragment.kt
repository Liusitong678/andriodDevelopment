package com.example.sneaknest.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.sneaknest.R
import com.example.sneaknest.listener.ToolbarTitleListener
import com.example.sneaknest.model.CartItem
import com.example.sneaknest.utils.ValidateUtils

class CheckoutFragment : Fragment() {

    // ------------------------------ UI ------------------------------
    private lateinit var firstNameEt: EditText
    private lateinit var lastNameEt: EditText
    private lateinit var addressEt: EditText
    private lateinit var unitEt: EditText
    private lateinit var cityEt: EditText
    private lateinit var stateEt: EditText
    private lateinit var postalEt: EditText
    private lateinit var phoneEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var cardEt: EditText
    private lateinit var cvvEt: EditText
    private lateinit var paymentSp: Spinner
    private lateinit var submitBtn: Button

    // ------------------------------ DATA ------------------------------
    private var cartList: ArrayList<CartItem> = arrayListOf()

    // ------------------------------ 生命周期 ------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        cartList = arguments?.getSerializable(ARG_CART_ITEMS) as? ArrayList<CartItem>
            ?: arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        initSpinner()

        submitBtn.setOnClickListener {
            if (validate()) {
                // 校验通过 → 跳转感谢页
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ThankYouFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? ToolbarTitleListener)?.updateToolbarTitle(getString(R.string.checkout))
    }

    // ------------------------------ 工具函数 ------------------------------
    private fun bindViews(v: View) {
        firstNameEt = v.findViewById(R.id.firstName)
        lastNameEt = v.findViewById(R.id.lastName)
        addressEt = v.findViewById(R.id.address)
        unitEt = v.findViewById(R.id.unitNumber)
        cityEt = v.findViewById(R.id.city)
        stateEt = v.findViewById(R.id.state)
        postalEt = v.findViewById(R.id.postalCode)
        phoneEt = v.findViewById(R.id.phone)
        emailEt = v.findViewById(R.id.email)
        cardEt = v.findViewById(R.id.cardNumber)
        cvvEt = v.findViewById(R.id.cvv)
        paymentSp = v.findViewById(R.id.paymentOptions)
        submitBtn = v.findViewById(R.id.submitOrderButton)
    }

    private fun initSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.payment_methods,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            paymentSp.adapter = it
        }
    }

    /** 表单校验 */
    private fun validate(): Boolean = when {
        firstNameEt.text.isBlank() -> { firstNameEt.error = "Required"; false }
        lastNameEt.text.isBlank()  -> { lastNameEt.error = "Required"; false }
        addressEt.text.isBlank()   -> { addressEt.error = "Required"; false }
        cityEt.text.isBlank()      -> { cityEt.error = "Required"; false }
        stateEt.text.isBlank()     -> { stateEt.error = "Required"; false }

        postalEt.text.isBlank() ||
                !ValidateUtils.isValidPostalCode(postalEt.text.toString()) ->
        { postalEt.error = "Invalid postal"; false }

        phoneEt.text.isBlank() ||
                !ValidateUtils.isNumeric(phoneEt.text.toString()) ->
        { phoneEt.error = "Invalid phone"; false }

        emailEt.text.isBlank() ||
                !Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches() ->
        { emailEt.error = "Invalid email"; false }

        cardEt.text.isBlank() ||
                !ValidateUtils.isValidCardNumber(cardEt.text.toString()) ->
        { cardEt.error = "Invalid card"; false }

        cvvEt.text.isBlank() ||
                !ValidateUtils.isNumeric(cvvEt.text.toString()) ->
        { cvvEt.error = "Invalid CVV"; false }

        else -> true
    }

    // ------------------------------ newInstance ------------------------------
    companion object {
        private const val ARG_CART_ITEMS = "arg_cart_items"

        /** 统一工厂方法，外部调用 */
        @JvmStatic
        fun newInstance(cartItems: List<CartItem>?): CheckoutFragment =
            CheckoutFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CART_ITEMS, ArrayList(cartItems ?: emptyList()))
                }
            }
    }
}
