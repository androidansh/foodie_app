package com.example.food_order_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.graphics.createBitmap
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.login.LoginSignUpActivity
import com.example.food_order_app.model.FoodCategoryModel
import com.example.food_order_app.model.FoodItemsData
import com.example.food_order_app.model.ReviewClass
import com.example.food_order_app.signUp.SignUpActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import java.sql.Time
import java.sql.Timestamp

class EntryActivity : AppCompatActivity() {
    lateinit var btn:Button
    private lateinit var itemList:ArrayList<FoodCategoryModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        itemList = ArrayList()

//        itemList.add(FoodCategoryModel("Family-Dinning","android.resource://" + packageName + "/" + R.drawable.family_dinning))
//        itemList.add(FoodCategoryModel("Coffee","android.resource://" + packageName + "/" + R.drawable.coffee))
//        itemList.add(FoodCategoryModel("Drinks","android.resource://" + packageName + "/" + R.drawable.drinks))
//        itemList.add(FoodCategoryModel("Premium-Places","android.resource://" + packageName + "/" + R.drawable.premium_places))
//        itemList.add(FoodCategoryModel("Outdoor-Dinning","android.resource://" + packageName + "/" + R.drawable.outdoor_dinning))
//        itemList.add(FoodCategoryModel("Desert","android.resource://" + packageName + "/" + R.drawable.desert))

        btn = findViewById(R.id.getStarted)
        btn.setOnClickListener {
//            addFoodCategory()
//            editDB()
            val pref:SharedPreferences = getSharedPreferences("LogIn", Context.MODE_PRIVATE)
            if(pref.getBoolean("isLoggedIn",false)){
                startActivity(Intent(this, HomeScreenActivity::class.java))
                val edit = pref.edit()
                edit.apply()
                finish()
            }
            else{
                startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            }
//            startActivity(Intent(this, HomeScreenActivity::class.java))
//            finish()
        }
    }


    private fun editDB(){
        val db = FirebaseFirestore.getInstance()
        val collect = "Pizza"
        val collRef = db.collection(collect)
        val foodCollection =  db.collection("all_food")
        collRef.get().addOnSuccessListener{ it ->
            for(i in it.documents){
                val food:FoodItemsData ?= i.toObject(FoodItemsData::class.java)
                if(food != null){
                    food.foodCategory = collect
                    foodCollection.add(food).addOnSuccessListener{ it1 ->
                        Log.i("my_msg","Success ${it1.id}")
                        var d = foodCollection.document(it1.id)
                        d.get().addOnSuccessListener { i3 ->

                            val food:FoodItemsData ?= i3.toObject(FoodItemsData::class.java)
                            if(food != null){
                                food.foodUID = i3.id
                                d.set(food).addOnSuccessListener {

                                    var map:HashMap<String,String> = HashMap()
                                    map["food_id"] = i3.id
                                    collRef.document(i.id).set(map).addOnSuccessListener {
                                        Log.i("my_msg","success 2")
                                    }.addOnFailureListener{
                                        Log.i("my_msg","Error in adding data to sandwich db")
                                    }

                                }.addOnFailureListener {
                                    it.printStackTrace()
                                }
                            }
                        }.addOnFailureListener{
                            it.printStackTrace()
                        }

                    }.addOnFailureListener{
                        Log.i("my_msg","Error in adding data to new db")
                    }

                }

            }

        }.addOnFailureListener{
            Log.i("my_msg","Error in editing data = ${it.localizedMessage}")
        }
    }


    private fun makeNewDB(){

    }

    private fun addFoodCategory(){
        val db = FirebaseFirestore.getInstance()
        val category = FoodItemsData()
        category.foodPic = "https://firebasestorage.googleapis.com/v0/b/foodie-food-shopping-app.appspot.com/o/foods%2FThali%2Fthali6.png?alt=media&token=fce5e7da-8ed3-446d-bce5-d849d82c3596"
        category.foodProviderName = "Desi Khana-khazana"
        category.foodRate = "350"
        category.foodDesc1 = "Veg"
        category.foodDesc2 = "Indian"
        category.foodDesc3 = "North-Indian"
        category.foodDeliveryTime = "30-35"
        category.aboutFood = "The Indian thali is a wholesome combination of colorful dishes served next to each other on a single plate. It is the perfect depiction of Indian cuisine."
        val docRef = db.collection("Dinner")
        docRef.add(category).addOnSuccessListener {
            Log.i("my_msg","success in adding data of ${category.foodProviderName} to db")
        }.addOnFailureListener {
            Log.i("my_msg","Error in adding data in db :: ${it.localizedMessage}" )
        }

    }

}