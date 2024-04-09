package com.example.food_order_app.model

import android.location.Address
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Phone
import java.util.Locale

data class FoodieUser
    (
    var userUid:String = "",
    var userName:String = "",
    var address:String = "",
    var email: String = "",
    var phone: String = "",
    var cart:ArrayList<String> = ArrayList(),
    var orders:ArrayList<OrderFood> = ArrayList(),
    var wishList:ArrayList<String> = ArrayList(),
    var isProvider:Boolean=false,
    var productsID:String = ""
    )
