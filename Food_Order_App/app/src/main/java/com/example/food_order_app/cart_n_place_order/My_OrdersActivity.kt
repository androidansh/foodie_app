package com.example.food_order_app.cart_n_place_order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_order_app.R
import com.example.food_order_app.adapter.UserOrderAdapter
import com.example.food_order_app.databinding.ActivityMyOrdersBinding
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.CartFood
import com.example.food_order_app.model.FoodItemsData
import com.example.food_order_app.model.FoodieUser
import com.example.food_order_app.model.OrderFoodDB
import com.example.food_order_app.model.OrderFoodUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.Collections

class My_OrdersActivity : AppCompatActivity() {


    private lateinit var binding:ActivityMyOrdersBinding
    companion object{
        var orderArray:ArrayList<OrderFoodDB> = ArrayList()
        lateinit var adapter:UserOrderAdapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

//         binding.orderRecycler.visibility = View.INVISIBLE
//        binding.orderAnimLayout.visibility = View.VISIBLE
        if(orderArray.size != 0){
            orderArray.removeAll(orderArray.toSet())
        }
        for( data in HomeScreenActivity.foodieUser.orders){
            orderArray.add(data)
        }
        orderArray.sort()
        adapter = UserOrderAdapter(orderArray)
        binding.orderRecycler.adapter = adapter
        binding.orderRecycler.layoutManager = LinearLayoutManager(applicationContext)
        binding.orderRecycler.addItemDecoration(DividerItemDecoration(applicationContext,DividerItemDecoration.VERTICAL))

    }


}