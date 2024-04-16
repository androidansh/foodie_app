package com.example.food_order_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_order_app.R
import com.example.food_order_app.model.FoodItemsData

class Recent_Suggested_Adapter(val recentFood:List<FoodItemsData>):RecyclerView.Adapter<Recent_Suggested_Adapter.ViewHolder>() {

    var context:Context ?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.suggested_recent_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context!!).load(recentFood[position].foodPic).into(holder.foodPic)
        holder.foodProvider.text = recentFood[position].foodProviderName
        holder.foodPrice.text = "₹${recentFood[position].foodRate}"
    }

    override fun getItemCount(): Int {
        return recentFood.size
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
        val foodPic: ImageView = itemview.findViewById(R.id.recentFoodPic)
        val foodProvider: TextView = itemview.findViewById(R.id.food_provider_recent)
        val foodPrice: TextView = itemview.findViewById(R.id.recentFoodPrice)
    }

}