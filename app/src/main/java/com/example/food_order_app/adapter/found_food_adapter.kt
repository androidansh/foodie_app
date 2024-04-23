package com.example.food_order_app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_order_app.IndividualActivity.Food_detailed_Activity
import com.example.food_order_app.R
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.home_screen.SearchActivity
import com.example.food_order_app.model.CartFood
import com.example.food_order_app.model.FoodItemsData

class found_food_adapter(private var foodArray:ArrayList<FoodItemsData>) : RecyclerView.Adapter<found_food_adapter.viewHolder>() {

    private lateinit var context:Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_food_item_layout,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        Glide.with(context).load(foodArray[position].foodPic).into(holder.foodPic)

        holder.foodName.text = foodArray[position].foodProviderName
        //Log.i("my_msg","Found id = ${foodArray.size}")
        holder.food.setOnClickListener {
            val cartFood = CartFood()
            val food = foodArray[position]
            cartFood.foodPic = food.foodPic
            cartFood.foodProviderName = food.foodProviderName
            cartFood.foodDesc1 = food.foodDesc1
            cartFood.foodDesc2 = food.foodDesc2
            cartFood.foodDesc3 = food.foodDesc3
            cartFood.foodRate = food.foodRate
            cartFood.foodDeliveryTime = food.foodDeliveryTime
            cartFood.foodDistance = food.foodDistance
            cartFood.foodDistance = food.foodDistance
            cartFood.foodRating = food.foodRating
            cartFood.foodRatingPoint = food.foodRatingPoint
            cartFood.aboutFood = food.aboutFood
            cartFood.foodUID = food.foodUID
            cartFood.foodBuyers = food.foodBuyers
            HomeScreenActivity.clickedFood = cartFood
            context.startActivity(Intent(SearchActivity.search_context, Food_detailed_Activity::class.java))
        }
        holder.foodRating.text = "Rated ${foodArray[position].foodRatingPoint} with ${foodArray[position].foodRating.size}"

    }

    override fun getItemCount(): Int {
        return foodArray.size
    }

    inner class viewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var foodPic:ImageView = itemView.findViewById(R.id.found_food_pic)
        var foodName:TextView = itemView.findViewById(R.id.found_food_name)
        var foodRating:TextView = itemView.findViewById(R.id.found_food_rating)
        var food:ConstraintLayout = itemView.findViewById(R.id.found_food_layout)
    }
}