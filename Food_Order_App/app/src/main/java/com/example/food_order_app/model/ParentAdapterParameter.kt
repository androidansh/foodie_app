package com.example.food_order_app.model

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager

data class ParentAdapterParameter
    (var foodList:ArrayList<FoodItemsData>,
     var foodCategoryList:ArrayList<FoodCategoryModel>,
    var layoutManager:LinearLayoutManager,
    var foodCategory:String
    )
