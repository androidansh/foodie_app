package com.example.food_order_app.model

class OrderFoodUser
    (
    var date:String  = "",
    var time:String = "",
    var delivered:Boolean = false,
    var uniqueID:String = "",
    var foodArray: ArrayList<CartFood> ?= null,
    var totalPrice:String = ""
    )
{
        constructor():this("")
}