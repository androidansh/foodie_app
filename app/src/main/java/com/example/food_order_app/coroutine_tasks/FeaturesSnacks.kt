package com.example.food_order_app.coroutine_tasks

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.example.food_order_app.home_screen.HomeFrag
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.FoodCategoryModel
import com.example.food_order_app.model.ParentAdapterParameter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeaturesSnacks {

    companion object{
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

    }


}