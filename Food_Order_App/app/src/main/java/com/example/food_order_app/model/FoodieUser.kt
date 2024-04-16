package com.example.food_order_app.model

data class FoodieUser
    (
    var userUid:String = "",
    var userName:String = "",
    var address:String = "",
    var email: String = "",
    var phone: String = "",
    var cart:ArrayList<String> = ArrayList(),
    var orders:ArrayList<OrderFoodDB> = ArrayList(),
    var wishList:ArrayList<String> = ArrayList(),
    var isProvider:Boolean=false,
    var productsID:String = ""
    )
