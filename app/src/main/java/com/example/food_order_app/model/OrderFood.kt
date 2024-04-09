package com.example.food_order_app.model

data class OrderFood(
    var food:ArrayList<String> = ArrayList(),
    var date:String  = ""
)
{
    constructor():this(ArrayList(),"")
}
