package com.example.food_order_app.cart_n_place_order

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food_order_app.R
import com.example.food_order_app.adapter.Cart_Item_Adapter
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.CartFood

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var proceed:TextView
    private lateinit var back:ImageView
    private lateinit var windowFrame: Window
    private var totalFoodCost:Int = 0

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var cartTotal:TextView

        var recentCart:HashMap<String,CartFood> = HashMap()
        lateinit var foodArray:ArrayList<CartFood>
        lateinit var context:Context
        lateinit var foodAdapter:Cart_Item_Adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        cartTotal = findViewById(R.id.cartCost)
        context = applicationContext

        windowFrame = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.acc_frag_bg_color)

        foodArray = ArrayList()
        val map = HomeScreenActivity.userCartMap
        for(i in map.keys){
            foodArray.add(map[i]!!)
            val price:Int ?= map[i]!!.foodRate.toIntOrNull()
            if(price != null){

                //totalFoodCost += (price * map[i]!!.foodNumber)
                //Log.i("my_msg","Cart COst = $totalFoodCost")
            }

        }

        cartTotal.text = totalFoodCost.toString()

        recyclerView  = findViewById(R.id.cartRecycler)

        proceed = findViewById(R.id.ConfirmCart)
        back = findViewById(R.id.cartBack)

        foodAdapter = Cart_Item_Adapter(foodArray)

        recyclerView.adapter = foodAdapter
//        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        back.setOnClickListener {
            totalFoodCost = 0
            finish()

        }

        proceed.setOnClickListener {
            val price = cartTotal.text.toString().toIntOrNull()
            if(price != null && price != 0){
                val intent = Intent(CartActivity.context,PlaceOrderActivity::class.java)
                intent.putExtra("totalCost",price)
                startActivity(intent)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        foodAdapter.notifyDataSetChanged()
    }
}