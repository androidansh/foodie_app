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
import com.example.food_order_app.IndividualActivity.FoodCategoryActivity
import com.example.food_order_app.R
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.FoodCategoryModel

class FoodCategoryAdapter(private var foodList: ArrayList<FoodCategoryModel>): RecyclerView.Adapter<FoodCategoryAdapter.ViewHolder>() {

    private lateinit var context: Context
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var food:ImageView = itemView.findViewById(R.id.foodCategoryPic)
        var foodName: TextView = itemView.findViewById(R.id.foodCategoryName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.food_category_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(foodList[position].foodCategoryPic).into(holder.food)
        holder.foodName.text = foodList[position].foodCategoryName
        holder.food.setOnClickListener {
            val intent = Intent(HomeScreenActivity.myContext, FoodCategoryActivity::class.java)
            intent.putExtra("category",foodList[position].foodCategoryName)
            context.startActivity(intent)
        }
    }
}