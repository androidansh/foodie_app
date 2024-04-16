package com.example.food_order_app.coroutine_tasks

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.food_order_app.home_screen.HomeFrag
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.CartFood
import com.example.food_order_app.model.FoodCategoryModel
import com.example.food_order_app.model.FoodItemsData
import com.example.food_order_app.model.FoodieUser
import com.example.food_order_app.model.ParentAdapterParameter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ThreadTasks {



    companion object{
        var context: Context ?= null
        @JvmStatic
        suspend fun getFeaturedFood(){
            Log.i("my_msg","Getting data from db....")
            val db = FirebaseFirestore.getInstance();
            val collRef = db.collection("Food-Category");
            collRef.get().addOnSuccessListener {
                for(i in it.documents){
                    val food = i.toObject(FoodCategoryModel::class.java)
                    if(food != null){
                        HomeScreenActivity.featuredSnack.add(food)
                        Log.i("my_msg","Category :: ${food.foodCategoryPic}")
                    }
                    else{
                        Log.i("my_msg","Category is empty")
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    Log.i("my_msg","Data set modified")
                    HomeFrag.parentFoodList.add(
                            ParentAdapterParameter(
                            ArrayList(),
                            HomeScreenActivity.featuredSnack,
                            GridLayoutManager(HomeScreenActivity.myContext,2,GridLayoutManager.HORIZONTAL,false)
                            ,"recommend")
                    )
                    HomeFrag.parentAdapter.notifyDataSetChanged()
//                    HomeFrag.featuredAdapter.notifyDataSetChanged()
                }
            }.addOnFailureListener {
                Log.i("my_msg","Error in loading food category :: ${it.localizedMessage}")
                it.stackTrace
            }

        }

        suspend fun syncUserData(){
            Log.i("my_msg","Sync started")
            val userId:String ?= FirebaseAuth.getInstance().uid
            if(userId != null){
                val docRef = FirebaseFirestore.getInstance().collection("FoodieUser").document(userId)
                docRef.get().addOnSuccessListener {
                    val user:FoodieUser ?= it.toObject(FoodieUser::class.java)
                    if(user != null){
                        HomeScreenActivity.foodieUser = user
                        HomeScreenActivity.foodieUser.orders.sort()
                        var userPref = context?.getSharedPreferences("FoodieUser",AppCompatActivity.MODE_PRIVATE)
                        val gson = Gson()
                        val jsonStr = gson.toJson(HomeScreenActivity.foodieUser)
                        var edit = userPref?.edit()
                        edit?.putString("user_data",jsonStr)
                        edit?.apply()

                        val map:HashMap<String, CartFood> = HashMap()
                        for(id in HomeScreenActivity.foodieUser.cart){
                            val doc = FirebaseFirestore.getInstance().collection("all_food").document(id)
                            doc.get().addOnSuccessListener {
                                val food1:FoodItemsData ?= it.toObject(FoodItemsData::class.java)
                                if(food1 != null){
                                    val food = CartFood(
                                        food1.foodPic,food1.foodProviderName,
                                        food1.foodDesc1,food1.foodDesc2,
                                        food1.foodDesc3,food1.foodRate,
                                        food1.foodDeliveryTime,food1.foodDistance,
                                        food1.foodRating,food1.foodRatingPoint,
                                        food1.aboutFood,food1.foodUID,
                                        food1.foodBuyers,1
                                    )
                                    map.put(id,food)
                                }
                            }.addOnFailureListener {
                                Log.i("my_msg","Error in syncing user cart data in Thread Class = ${it.printStackTrace()}")
                            }
                        }
                        HomeScreenActivity.userCartMap = map
                        userPref = context?.getSharedPreferences("UserCartData",AppCompatActivity.MODE_PRIVATE)
                        val json = gson.toJson(HomeScreenActivity.userCartMap)
                        edit = userPref?.edit()
                        edit?.putString("cartFood",json)
                        edit?.apply()
                    }
                }.addOnFailureListener {
                    Log.i("my_msg","Error in syncing user data in Thread Class = ${it.printStackTrace()}")
                }
            }
            Log.i("my_msg","Sync ended")
        }

    }


}