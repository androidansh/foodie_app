package com.example.food_order_app.IndividualActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.food_order_app.R
import com.example.food_order_app.adapter.Recent_Suggested_Adapter
import com.example.food_order_app.adapter.ReviewAdapter
import com.example.food_order_app.cart_n_place_order.CartActivity
import com.example.food_order_app.coroutine_tasks.ThreadTasks
import com.example.food_order_app.databinding.ActivityFoodDetailedBinding
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@SuppressLint("SetTextI18n")
class Food_detailed_Activity : AppCompatActivity() {
    private var foodCost: Int? = 0
    private lateinit var windowFrame: Window
    private lateinit var binding: ActivityFoodDetailedBinding

    companion object {
        lateinit var context1: Context
        lateinit var reviewAdapter :ReviewAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        windowFrame = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)
        context1 = applicationContext
        setViews()
        binding.foodMinus.setOnClickListener {
            var foodCount: Int? = binding.foodNum.text.toString().toIntOrNull()
            if (foodCount != null && foodCount > 1 && foodCost != null) {
                foodCount--
                binding.foodNum.text = foodCount.toString()
                binding.foodItemPrice.text = "₹${(foodCost!! * foodCount)}"
            }
        }
        binding.viewAllReviews.setOnClickListener {
            startActivity(Intent(HomeScreenActivity.myContext,AddReviewActivity::class.java))
        }
//        val array = HomeScreenActivity.recommendedSnack.take(10)
//        Log.i("my_msg", array.toString())

        binding.foodPlus.setOnClickListener {
            var foodCount: Int? = binding.foodNum.text.toString().toIntOrNull()
            if (foodCount != null && foodCost != null) {
                foodCount++
                binding.foodNum.text = foodCount.toString()
                binding.foodItemPrice.text = "₹${(foodCost!! * foodCount)}"
            }
        }


        binding.addToCart.setOnClickListener {
            if (binding.add2cartMsg.text == "Go to Cart") {
                startActivity(Intent(context1, CartActivity::class.java))
                return@setOnClickListener
            }
            val cartFood = HomeScreenActivity.clickedFood!!.copy()
            val foodCount: Int? = binding.foodNum.text.toString().toIntOrNull()
            if (foodCount != null) {
                cartFood.foodNumber = foodCount
            }

            HomeScreenActivity.userCartMap[HomeScreenActivity.clickedFood!!.foodUID] = cartFood
            binding.add2cartMsg.text = "Go to Cart"
//            Log.i("my_msg","Total number of food in cart = ${HomeScreenActivity.userCartMap.size}")
            Toast.makeText(this, "Food added to caret.", Toast.LENGTH_LONG).show()

            CoroutineScope(Dispatchers.IO).launch {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                val doc =
                    FirebaseFirestore.getInstance().collection("FoodieUser").document(userId!!)
                val list: ArrayList<String> = ArrayList()
                for (l in HomeScreenActivity.userCartMap.keys) {
                    list.add(l)
                }
                doc.update("cart", list).addOnSuccessListener {
                    Log.i("my_msg", "Success user cart added to db")
                }.addOnFailureListener {
                    Log.i("my_msg", "Error in adding user cart to db")
                }
                ThreadTasks.syncUserData()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        reviewAdapter.notifyDataSetChanged()
        val ratings: Float? = HomeScreenActivity.clickedFood!!.foodRatingPoint.toFloatOrNull()
        if (ratings != null) {
            binding.ratingBar.rating = ratings
        }
        binding.numRatings.text ="(${HomeScreenActivity.clickedFood!!.foodRating.size} ratings)"

    }


    private fun setViews() {
        if (HomeScreenActivity.clickedFood != null) {

            Glide.with(this).load(HomeScreenActivity.clickedFood!!.foodPic)
                .into(binding.foodClickedImage)
            binding.foodClickedProviderName.text = HomeScreenActivity.clickedFood!!.foodProviderName
            val ratings: Float? = HomeScreenActivity.clickedFood!!.foodRatingPoint.toFloatOrNull()
            if (ratings != null) {
                binding.ratingBar.rating = ratings
            }
            binding.numRatings.text =
                "(${HomeScreenActivity.clickedFood!!.foodRating.size} ratings)"
            binding.aboutFood.text = HomeScreenActivity.clickedFood!!.aboutFood
            binding.foodNum.text = HomeScreenActivity.clickedFood!!.foodNumber.toString()
            binding.foodItemPrice.text = "₹${HomeScreenActivity.clickedFood!!.foodRate}"
            foodCost = HomeScreenActivity.clickedFood!!.foodRate.toIntOrNull()

            val suggestedArray = HomeScreenActivity.recommendedSnack.shuffled().take(10)
            val suggestedAdapter = Recent_Suggested_Adapter(suggestedArray)
            binding.suggestionFoodRecycler.adapter = suggestedAdapter
            binding.suggestionFoodRecycler.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

//            if (HomeScreenActivity.clickedFood!!.foodRating.size < 4) {
//                binding.viewAllReviews.visibility = View.VISIBLE
//            }
            reviewAdapter = ReviewAdapter(HomeScreenActivity.clickedFood!!.foodRating,"1")
            binding.reviewRecycler1.adapter = reviewAdapter
            binding.reviewRecycler1.layoutManager = LinearLayoutManager(this)
            if (HomeScreenActivity.userCartMap.containsKey(HomeScreenActivity.clickedFood!!.foodUID)) {
                Log.i(
                    "my_msg",
                    "Already in cart food = ${HomeScreenActivity.clickedFood!!.foodUID}"
                )
                binding.add2cartMsg.text = "Go to Cart"
            } else {
                Log.i("my_msg", "New Food = ${HomeScreenActivity.clickedFood!!.foodUID}")
                binding.add2cartMsg.text = "Add to Cart"
            }
        }
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