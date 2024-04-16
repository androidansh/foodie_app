package com.example.food_order_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.food_order_app.R
import com.example.food_order_app.model.OrderFoodDB
import com.example.food_order_app.model.OrderFoodUser

class UserOrderAdapter(private var orderFood:ArrayList<OrderFoodDB>) : RecyclerView.Adapter<UserOrderAdapter.viewHolder>(){

    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var orderId:TextView = itemView.findViewById(R.id.orderID)
        var orderDate:TextView = itemView.findViewById(R.id.orderDate)
        var orderTime:TextView = itemView.findViewById(R.id.orderTime)
        var delivered:TextView = itemView.findViewById(R.id.orderDelivered)
        var notDelivered:TextView = itemView.findViewById(R.id.orderNotDelivered)
        var orderCost:TextView = itemView.findViewById(R.id.orderCost)
        var orderLayout:ConstraintLayout = itemView.findViewById(R.id.orderLayout)
        var trackOrderLayout:ConstraintLayout = itemView.findViewById(R.id.trackOrderLayout)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item_layout,parent,false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderFood.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        var order = orderFood[position]
        holder.orderId.text = order.uniqueID
        holder.orderDate.text = order.date
        holder.orderTime.text = order.time
        if(order.delivered){
            holder.delivered.visibility = View.VISIBLE
            holder.notDelivered.visibility = View.INVISIBLE
            holder.trackOrderLayout.visibility = View.INVISIBLE
        }
        else{
            holder.delivered.visibility = View.INVISIBLE
            holder.notDelivered.visibility = View.VISIBLE
            holder.trackOrderLayout.visibility = View.VISIBLE
        }
        holder.orderCost.text = "₹ ${order.totalPrice}"
    }
}