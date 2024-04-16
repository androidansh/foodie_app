package com.example.food_order_app.model

import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date

data class OrderFoodDB(
    var food:ArrayList<OrderFood_ID_Num> = ArrayList(),
    var date:String  = "",
    var time:String = "",
    var delivered:Boolean = false,
    var uniqueID:String = "",
    var totalPrice:String = ""
):Comparable<OrderFoodDB>
{
    constructor():this(ArrayList(),"")

    override fun compareTo(other: OrderFoodDB): Int {
        val dateComparison = other.date.compareTo(this.date);
        if (dateComparison != 0) {
            return dateComparison;
        } else {
            // If creation dates are the same, compare creation times
            return other.time.compareTo(this.time);

        }
    }
}
