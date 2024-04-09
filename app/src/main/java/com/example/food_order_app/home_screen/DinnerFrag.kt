package com.example.food_order_app.home_screen

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food_order_app.R
import com.example.food_order_app.adapter.ParentRecyclerAdapter
import com.example.food_order_app.model.FoodCategoryModel
import com.example.food_order_app.model.FoodItemsData
import com.example.food_order_app.model.ParentAdapterParameter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay

class DinnerFrag : Fragment() {

    private lateinit var parentRecycler:RecyclerView
    private lateinit var parentAdapter:ParentRecyclerAdapter
    private var parentList = ArrayList<ParentAdapterParameter>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dinner, container, false)

        parentRecycler = view.findViewById(R.id.parentFoodRecyclerDinner)
        parentList =  ArrayList()
        parentAdapter = ParentRecyclerAdapter(parentList)
        //makeRecycler()
        loadDataFromDB()
        return view;
    }


    private fun loadDataFromDB(){
        val def1 = CoroutineScope(Dispatchers.Default).async {
            getSubMenu()

            "Sub menu Data loaded."
        }
        val def2 = CoroutineScope(Dispatchers.Default).async {
//  Wait for Coroutine 1 to complete
            val isSubMenuLoaded = def1.await()
            delay(500)
            Log.i("my_msg","Submenu loaded :: $isSubMenuLoaded")
            getMustTryData()
        }
//  Wait for Coroutine 2 to complete (optional)
        CoroutineScope(Dispatchers.Default).async {
            val isMustTryLoaded = def2.await()
            Log.i("my_msg","Submenu loaded :: $isMustTryLoaded")
        }
//  Ensure all coroutines are completed before exiting
        CoroutineScope(Dispatchers.Default).coroutineContext.cancelChildren()

    }

    private fun getSubMenu(){
        if(HomeScreenActivity.dinnerSubMenu.size == 0){
            val db =  FirebaseFirestore.getInstance()
            val collRef = db.collection("Dinner-Sub-Menu")
            collRef.get().addOnSuccessListener {
                for(i in it.documents){
                    val food:FoodCategoryModel ?= i.toObject(FoodCategoryModel::class.java)
                    if(food != null){
                        HomeScreenActivity.dinnerSubMenu.add(food)
                    }
                }
                parentList.add(ParentAdapterParameter(ArrayList(),HomeScreenActivity.dinnerSubMenu,GridLayoutManager(context,3),"subMenu"))
                parentAdapter.notifyDataSetChanged()
                parentRecycler.adapter = parentAdapter
                parentRecycler.layoutManager = LinearLayoutManager(context)
            }.addOnFailureListener {
                Log.i("my_msg","Error in loading sub menu : ${it.localizedMessage}")
                it.stackTrace
            }
        }
        else{
            parentList.add(ParentAdapterParameter(ArrayList(),HomeScreenActivity.dinnerSubMenu,GridLayoutManager(context,3),"subMenu"))
            parentAdapter = ParentRecyclerAdapter(parentList)
            parentRecycler.adapter = parentAdapter
            parentRecycler.layoutManager = LinearLayoutManager(context)
        }

    }

    private fun getMustTryData(){
        if(HomeScreenActivity.dinnerMustTry.size == 0){
            val db =  FirebaseFirestore.getInstance()
            val collRef = db.collection("Dinner-Must-Try")
            collRef.get().addOnSuccessListener {
                for(i in it.documents){
                    val food:FoodCategoryModel ?= i.toObject(FoodCategoryModel::class.java)
                    if(food != null){
                        HomeScreenActivity.dinnerMustTry.add(food)
                    }
                }
                parentList.add(ParentAdapterParameter(ArrayList(),HomeScreenActivity.dinnerMustTry,
                    LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false),"mustTry"))
                parentAdapter.notifyDataSetChanged()
            }.addOnFailureListener {
                Log.i("my_msg","Error in loading sub menu : ${it.localizedMessage}")
                it.stackTrace
            }
        }else{
            parentList.add(ParentAdapterParameter(ArrayList(),HomeScreenActivity.dinnerMustTry,
                LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false),"mustTry"))
            parentAdapter.notifyDataSetChanged()
        }

    }


}