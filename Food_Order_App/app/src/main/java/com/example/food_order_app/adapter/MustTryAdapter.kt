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
import com.example.food_order_app.model.FoodCategoryModel
import com.example.food_order_app.model.FoodItemsData

class MustTryAdapter (private var foodList: ArrayList<FoodCategoryModel>): RecyclerView.Adapter<MustTryAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var food: ImageView = itemView.findViewById(R.id.mustTryFood)
        var text: TextView = itemView.findViewById(R.id.mustTryFoodCategory)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.dinner_sub_menu_2,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context).load(foodList.get(position).foodCategoryPic).into(holder.food)

        //holder.food.setImageResource(foodList.get(position).foodPic)
        if(foodList[position].foodCategoryName != ""){
            holder.text.text = foodList[position].foodCategoryName
        }
    }
}