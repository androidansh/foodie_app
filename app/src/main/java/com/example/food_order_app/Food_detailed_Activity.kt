package com.example.food_order_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_order_app.adapter.Recent_Suggested_Adapter
import com.example.food_order_app.adapter.ReviewAdapter
import com.example.food_order_app.cart_n_place_order.CartActivity
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Food_detailed_Activity : AppCompatActivity() {

    private lateinit var foodPic:ImageView
    private lateinit var foodProvider:TextView
    private lateinit var foodRatingNum:TextView
    private lateinit var ratingBar:RatingBar
    private lateinit var foodMinus:ImageView
    private lateinit var foodPlus:ImageView
    private lateinit var foodNum:TextView
    private lateinit var foodDesc:TextView
    private lateinit var totalCartCost:TextView
    private lateinit var addToCart:ConstraintLayout
    private lateinit var suggestedRecycler:RecyclerView
    private lateinit var reviewRecycler:RecyclerView
    private lateinit var viewMoreReviews:TextView
    private lateinit var addToCartMsg:TextView
    private var foodCost:Int ?= 0
    private lateinit var windowFrame: Window
    companion object{
        lateinit var context1:Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detailed)
        windowFrame = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)
        context1 = applicationContext
        init_parameters()
        setViews()
        foodMinus.setOnClickListener {
            var foodCount:Int ?= foodNum.text.toString().toIntOrNull()
            if(foodCount != null && foodCount > 1 && foodCost != null){
                foodCount--
                foodNum.text = foodCount.toString()
                totalCartCost.text = "₹${(foodCost!! *foodCount)}"
            }
        }
//        val array = HomeScreenActivity.recommendedSnack.take(10)
//        Log.i("my_msg", array.toString())

        foodPlus.setOnClickListener {
            var foodCount:Int ?= foodNum.text.toString().toIntOrNull()
            if(foodCount != null && foodCost != null){
                foodCount++
                foodNum.text = foodCount.toString()
                totalCartCost.text = "₹${(foodCost!! *foodCount)}"
            }
        }


        addToCart.setOnClickListener {
            if(addToCartMsg.text == "Go to Cart"){
                startActivity(Intent(Food_detailed_Activity.context1, CartActivity::class.java))
                return@setOnClickListener
            }
            val cartFood = HomeScreenActivity.clickedFood!!.copy()
            val foodCount:Int ?= foodNum.text.toString().toIntOrNull()
            if(foodCount != null){
                cartFood.foodNumber = foodCount
            }

            HomeScreenActivity.userCartMap[HomeScreenActivity.clickedFood!!.foodUID] = cartFood
            addToCartMsg.text = "Go to Cart"
//            Log.i("my_msg","Total number of food in cart = ${HomeScreenActivity.userCartMap.size}")
            Toast.makeText(this,"Food added to caret.",Toast.LENGTH_LONG).show()

            CoroutineScope(Dispatchers.IO).launch {

                val pref = getSharedPreferences("UserCartData", MODE_PRIVATE)
                val gson = Gson()
                val jsonStr = gson.toJson(HomeScreenActivity.userCartMap)
                val edit = pref.edit()
                edit.putString("cartFood",jsonStr)
                edit.apply()
            }
            CoroutineScope(Dispatchers.IO).launch {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                val doc = FirebaseFirestore.getInstance().collection("FoodieUser").document(userId!!)
                val list:ArrayList<String> = ArrayList()
                for( l in HomeScreenActivity.userCartMap.keys){
                    list.add(l)
                }
                doc.update("cart",list).addOnSuccessListener {
                    Log.i("my_msg","Success user cart added to db")
                }.addOnFailureListener{
                    Log.i("my_msg","Error in adding user cart to db")
                }
//                doc.update("cart", FieldValue.arrayUnion(HomeScreenActivity.clickedFood!!.foodUID)).addOnSuccessListener {
//                    Log.i("my_msg","Success user cart added to db")
//                }.addOnFailureListener{
//                    Log.i("my_msg","Error in adding user cart to db")
//                }
            }
        }
    }


    private fun setViews() {
        if(HomeScreenActivity.clickedFood != null){

            Glide.with(this).load(HomeScreenActivity.clickedFood!!.foodPic).into(foodPic)
            foodProvider.text = HomeScreenActivity.clickedFood!!.foodProviderName
            val ratings:Float ?= HomeScreenActivity.clickedFood!!.foodRatingPoint.toFloatOrNull()
            if(ratings != null){
                ratingBar.rating = ratings
            }
            foodRatingNum.text = "(${HomeScreenActivity.clickedFood!!.foodRating.size} ratings)"
            foodDesc.text = HomeScreenActivity.clickedFood!!.aboutFood
            foodNum.text = HomeScreenActivity.clickedFood!!.foodNumber.toString()
            totalCartCost.text = "₹${HomeScreenActivity.clickedFood!!.foodRate}"
            foodCost = HomeScreenActivity.clickedFood!!.foodRate.toIntOrNull()

            val suggestedArray = HomeScreenActivity.recommendedSnack.shuffled().take(10)
            val suggestedAdapter = Recent_Suggested_Adapter(suggestedArray)
            suggestedRecycler.adapter = suggestedAdapter
            suggestedRecycler.layoutManager =  LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

            if(HomeScreenActivity.clickedFood!!.foodRating.size < 4){
                viewMoreReviews.visibility = View.INVISIBLE
            }
            val reviewAdapter = ReviewAdapter(HomeScreenActivity.clickedFood!!.foodRating)
            reviewRecycler.adapter = reviewAdapter
            reviewRecycler.layoutManager = LinearLayoutManager(this)
            if(HomeScreenActivity.userCartMap.containsKey(HomeScreenActivity.clickedFood!!.foodUID)){
                Log.i("my_msg","Already in cart food = ${HomeScreenActivity.clickedFood!!.foodUID}")
                addToCartMsg.text = "Go to Cart"
            }
            else{
                Log.i("my_msg","New Food = ${HomeScreenActivity.clickedFood!!.foodUID}")
                addToCartMsg.text = "Add to Cart"
            }
        }
    }

    private fun init_parameters() {
        foodPic = findViewById(R.id.foodClickedImage)
        foodProvider = findViewById(R.id.foodClickedProviderName)
        foodRatingNum = findViewById(R.id.numRatings)
        ratingBar = findViewById(R.id.ratingBar)
        foodMinus = findViewById(R.id.foodMinus)
        foodPlus = findViewById(R.id.foodPlus)
        foodNum = findViewById(R.id.foodNum)
        foodDesc = findViewById(R.id.aboutFood)
        totalCartCost= findViewById(R.id.food_item_price)
        addToCart = findViewById(R.id.addToCart)
        suggestedRecycler = findViewById(R.id.suggestionFoodRecycler)
        reviewRecycler = findViewById(R.id.reviewRecycler_1)
        viewMoreReviews = findViewById(R.id.viewAllReviews)
        addToCartMsg = findViewById(R.id.add2cartMsg)
    }
}


//    Glide.with(this).asBitmap().load(HomeScreenActivity.clickedFood!!.foodPic)
//        .into(object : BitmapImageViewTarget(foodBlurPuc) {
//            override fun onResourceReady(
//                bitmap: Bitmap,
//                transition: Transition<in Bitmap>?
//            ) {
//                super.onResourceReady(bitmap, transition)
//                val bitmapFromImageView = foodBlurPuc.getDrawable().toBitmap()
//                val blurredBitmap: Bitmap = BlurUtils.blur(this@Food_detailed_Activity,bitmapFromImageView,25F)
//                foodBlurPuc.setImageBitmap(blurredBitmap)
//            }
//        })