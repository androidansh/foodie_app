package com.example.food_order_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_order_app.R
import com.example.food_order_app.model.FoodItemsData

class PopularRestroAdapter(private var foodList:ArrayList<FoodItemsData>) : RecyclerView.Adapter<PopularRestroAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){
        var food: ImageView = itemView.findViewById(R.id.restroFoodImage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.popular_restro,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context).load(foodList[position].foodPic).into(holder.food)

//        holder.food.setBackgroundResource(foodList[position].foodPic)
    }
}