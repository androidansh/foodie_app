package com.example.food_order_app.home_screen

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.food_order_app.IndividualActivity.AddReviewActivity
import com.example.food_order_app.cart_n_place_order.CartActivity
import com.example.food_order_app.R
import com.example.food_order_app.TrackOrderService
import com.example.food_order_app.cart_n_place_order.My_OrdersActivity
import com.example.food_order_app.cart_n_place_order.PlaceOrderActivity
import com.example.food_order_app.coroutine_tasks.ThreadTasks
import com.example.food_order_app.model.FoodieUser
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AccountFrag : Fragment() {

    private lateinit var myCart:ConstraintLayout
    private lateinit var myOrders:ConstraintLayout
    private lateinit var myWishlist:ConstraintLayout
    private lateinit var name:TextView
    private lateinit var address:TextView
    private lateinit var initName:TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        name = view.findViewById(R.id.AccountName)
        address = view.findViewById(R.id.addressAccount)
        initName = view.findViewById(R.id.AccountInitial)

        name.text = HomeScreenActivity.foodieUser.userName
        address.text = HomeScreenActivity.foodieUser.address
        initName.text = name.text.subSequence(0,1)

        myCart = view.findViewById(R.id.my_cart_layout)
        myCart.setOnClickListener {
            context?.startActivity(Intent(HomeScreenActivity.myContext, CartActivity::class.java))
        }
        myOrders = view.findViewById(R.id.my_order_layout)
        myOrders.setOnClickListener {
            context?.startActivity(Intent(HomeScreenActivity.myContext, My_OrdersActivity::class.java))
        }

        myWishlist = view.findViewById(R.id.userWishlist)
        myWishlist.setOnClickListener {
//            CoroutineScope(Dispatchers.IO).launch {
//                ThreadTasks.syncUserData()
//            }
            context?.startActivity(Intent(HomeScreenActivity.myContext,AddReviewActivity::class.java))
        }

        return view
    }

}