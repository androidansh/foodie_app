package com.example.food_order_app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_order_app.IndividualActivity.Food_detailed_Activity
import com.example.food_order_app.R
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.CartFood
import com.example.food_order_app.model.FoodItemsData

class RecommendedFoodAdapter(private var foodList:ArrayList<FoodItemsData>) : RecyclerView.Adapter<RecommendedFoodAdapter.ViewHolder>() {

    private lateinit var context:Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.recommended_food_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = foodList[position]
        Glide.with(context).load(foodList[position].foodPic).thumbnail(0.1f).into(holder.food)
        holder.foodProvider.text = food.foodProviderName
        holder.foodDesc1.text = food.foodDesc1
        holder.foodDesc2.text = food.foodDesc2
        holder.foodDesc3.text = food.foodDesc3
        holder.foodRate.text = "${food.foodRate} for one"
        holder.foodTime.text = food.foodDeliveryTime
        holder.foodDist.text = food.foodDistance

        holder.food.setOnClickListener{
            val cartFood = CartFood()
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
            context.startActivity(Intent(HomeScreenActivity.myContext, Food_detailed_Activity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var foodProvider: TextView = itemView.findViewById(R.id.featuredFoodName)
        var foodDesc1: TextView = itemView.findViewById(R.id.featuredFoodDesc1)
        var foodDesc2: TextView = itemView.findViewById(R.id.featuredFoodDesc2)
        var foodDesc3: TextView = itemView.findViewById(R.id.featuredFoodDesc3)
        var foodTime: TextView = itemView.findViewById(R.id.featuredFoodTime)
        var foodRate: TextView = itemView.findViewById(R.id.featuredFoodPrice)
        var foodDist: TextView = itemView.findViewById(R.id.featuredFoodDistance)
        var food:ImageView = itemView.findViewById(R.id.featuredFoodImage)

    }
}