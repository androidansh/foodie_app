package com.example.food_order_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import com.example.food_order_app.adapter.RecommendedFoodAdapter
import com.example.food_order_app.home_screen.HomeFrag
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.FoodItemsData
import com.example.food_order_app.model.ParentAdapterParameter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FoodCategoryActivity : AppCompatActivity() {

    private lateinit var recycler:RecyclerView
    private lateinit var adapater:RecommendedFoodAdapter
    private lateinit var list:ArrayList<FoodItemsData>
    private var categoryText:String = ""
    private lateinit var cons:ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_category)

        recycler = findViewById(R.id.categoryRecycler)
        list = ArrayList()


        cons = findViewById(R.id.constraint)

        categoryText = intent.getStringExtra("category")!!
        getData()
        HomeScreenActivity.windowFrame = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)
    }

    fun getData(){
        val db = FirebaseFirestore.getInstance()
        val job = CoroutineScope(Dispatchers.IO).launch {
            val collRef = db.collection(categoryText)
            collRef.get().addOnSuccessListener {


                for (j in it.documents) {
                    val doc = db.collection("all_food").document(j["food_id"].toString())
                    doc.get().addOnSuccessListener {data->
                        val food: FoodItemsData? = data.toObject(FoodItemsData::class.java)
                        if (food != null) {
                            list.add(food)
                        }
                    }.addOnFailureListener {
                        Log.i("my_msg", "Error in ${it.localizedMessage}")
                    }

                }
            }.addOnFailureListener {
                Log.i("my_msg", "Error in ${it.localizedMessage}")
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            adapater = RecommendedFoodAdapter(list)
            recycler.adapter = adapater
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.visibility = View.VISIBLE
            cons.visibility = View.GONE
        }, 1000)

        Log.i("my_msg", "All data got for $categoryText.")
    }
}