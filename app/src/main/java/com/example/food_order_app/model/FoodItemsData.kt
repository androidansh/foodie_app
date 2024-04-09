package com.example.food_order_app.model

data class FoodItemsData(
    var foodPic:String="",
    var foodProviderName:String="",
    var foodDesc1:String="",
    var foodDesc2:String="",
    var foodDesc3:String="",
    var foodCategory:String = "",
    var foodRate:String="",
    var foodDeliveryTime:String="",
    var foodDistance: String="",
    var foodRating:ArrayList<ReviewClass>,
    var foodRatingPoint:String="",
    var aboutFood:String="",
    var foodUID:String = "",
    var foodBuyers:ArrayList<ReviewClass>
)
{
    constructor():this(foodRating = ArrayList(),foodBuyers = ArrayList())


}
