package com.example.food_order_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.food_order_app.R
import com.example.food_order_app.model.FoodCategoryModel

class SearchBarAdapter(var search:ArrayList<FoodCategoryModel>) : RecyclerView.Adapter<SearchBarAdapter.ViewHolder>(){



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.search_bar_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 1;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}