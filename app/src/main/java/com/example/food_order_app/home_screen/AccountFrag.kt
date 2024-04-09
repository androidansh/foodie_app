package com.example.food_order_app.home_screen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.food_order_app.cart_n_place_order.CartActivity
import com.example.food_order_app.R
import com.example.food_order_app.cart_n_place_order.PlaceOrderActivity


class AccountFrag : Fragment() {

    private lateinit var myCart:ConstraintLayout
    private lateinit var myOrders:ConstraintLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        myCart = view.findViewById(R.id.my_cart_layout)
        myCart.setOnClickListener {
            context?.startActivity(Intent(HomeScreenActivity.myContext, CartActivity::class.java))
        }
        myOrders = view.findViewById(R.id.my_order_layout)
        myOrders.setOnClickListener {
            context?.startActivity(Intent(HomeScreenActivity.myContext, PlaceOrderActivity::class.java))
        }

        return view
    }

}