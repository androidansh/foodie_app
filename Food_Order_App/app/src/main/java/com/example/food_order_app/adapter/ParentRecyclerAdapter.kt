package com.example.food_order_app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.food_order_app.R
import com.example.food_order_app.model.ParentAdapterParameter

class ParentRecyclerAdapter(private var childRecyclerList:ArrayList<ParentAdapterParameter>):RecyclerView.Adapter<ParentRecyclerAdapter.ViewHolder>() {

    lateinit var context:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.child_recycler_view,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         if(childRecyclerList[position].foodCategory == "recommended"){
             Log.i("my_msg","Array Size =  ${childRecyclerList[position].foodCategoryList.size}")
             holder.childRecycler.adapter = RecommendedFoodAdapter(childRecyclerList[position].foodList)
             holder.recycler_heading.text = context.getString(R.string.featuredText)
        }
        else if(childRecyclerList[position].foodCategory == "popular"){
             holder.childRecycler.adapter = PopularRestroAdapter(childRecyclerList[position].foodList)
             holder.recycler_heading.text = context.getString(R.string.popularText)
        }
        else if(childRecyclerList[position].foodCategory == "subMenu"){
            holder.childRecycler.adapter = SubMenuAdapter(childRecyclerList[position].foodCategoryList)
            holder.recycler_heading.text = context.getString(R.string.subMenuText)
        }
        else if(childRecyclerList[position].foodCategory == "mustTry"){
            holder.childRecycler.adapter = MustTryAdapter(childRecyclerList[position].foodCategoryList)
            holder.recycler_heading.text = context.getString(R.string.mustTry)
        }
        else if(childRecyclerList[position].foodCategory == "food-category"){
            holder.childRecycler.adapter = FoodCategoryAdapter(childRecyclerList[position].foodCategoryList)
            holder.recycler_heading.text = context.getString(R.string.recommendText)
        }
//        else if(childRecyclerList[position].foodCategory == "search"){
//             holder.childRecycler.adapter = SearchBarAdapter(childRecyclerList[position].foodCategoryList)
//             holder.recycler_heading.visibility = View.INVISIBLE
//             holder.bgLine.visibility = View.INVISIBLE
//             holder.childLayout.setBackgroundResource(R.drawable.bottom_round_corner_image)
//         }
        holder.childRecycler.layoutManager = childRecyclerList[position].layoutManager
        holder.childRecycler.setHasFixedSize(true)

    }

    override fun getItemCount(): Int {
        return childRecyclerList.size
    }

    inner class ViewHolder(private var itemView: View) : RecyclerView.ViewHolder(itemView){
        val childRecycler:RecyclerView = itemView.findViewById(R.id.childRecycler)
        val recycler_heading:TextView = itemView.findViewById(R.id.child_recycler_heading)
        var bgLine:TextView = itemView.findViewById(R.id.bgLine)
        var childLayout:ConstraintLayout = itemView.findViewById(R.id.childLayout)
    }
}