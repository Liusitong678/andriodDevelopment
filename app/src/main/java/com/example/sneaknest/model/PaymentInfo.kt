package com.example.sneaknest.model
import java.io.Serializable

data class PaymentInfo(
    var firstName: String? = null,
    var lastName: String? = null,
    var address: String? = null,
    var unitNumber: String? = null,
    var city: String? = null,
    var state: String? = null,
    var postalCode: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var paymentMethod: String? = null,
    var cardNumber: String? = null,
    var cvv: String? = null
) : Serializable
