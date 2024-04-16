package com.example.food_order_app.model

data class FoodCategoryModel(
    var foodCategoryName:String,
    var foodCategoryPic:String
){
    constructor() : this("","")
}
