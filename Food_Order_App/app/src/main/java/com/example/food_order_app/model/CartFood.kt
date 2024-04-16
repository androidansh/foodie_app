package com.example.food_order_app.model
import  java.util.*
import kotlin.collections.ArrayList


data class CartFood(
    var foodPic:String="",
    var foodProviderName:String="",
    var foodDesc1:String="",
    var foodDesc2:String="",
    var foodDesc3:String="",
    var foodRate:String="",
    var foodDeliveryTime:String="",
    var foodDistance: String="",
    var foodRating:ArrayList<ReviewClass>,
    var foodRatingPoint:String="",
    var aboutFood:String="",
    var foodUID:String = "",
    var foodBuyers:ArrayList<ReviewClass>,
    var foodNumber:Int = 1
)
{
    constructor():this(foodRating = ArrayList(),foodBuyers = ArrayList())

}
