package com.example.food_order_app.model

data class ReviewClass(
    var reviewerName:String = "",
    var reviewText:String = "",
    var reviewUID : String = "aaabbbcccddd",
    var reviewPoint:Int = 1){
    constructor():this("")
}
