package com.example.food_order_app.cart_n_place_order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.food_order_app.R
import com.example.food_order_app.databinding.ActivityMyOrdersBinding

class My_OrdersActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMyOrdersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }
}