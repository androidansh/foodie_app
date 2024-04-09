package com.example.food_order_app.home_screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
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

@SuppressLint("StaticFieldLeak")
class HomeFrag : Fragment() {
    private lateinit var parentRecycler: RecyclerView
    private lateinit var constraint:ConstraintLayout
    private lateinit var et:TextView

    companion object {
        @JvmStatic
        lateinit var parentAdapter: ParentRecyclerAdapter

        //lateinit var featuredAdapter:FeaturedFoodAdapter
        lateinit var parentFoodList: ArrayList<ParentAdapterParameter>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        parentRecycler = view.findViewById(R.id.parentFoodRecycler)
        constraint = view.findViewById(R.id.constraint1)
        parentFoodList = ArrayList()
        parentAdapter = ParentRecyclerAdapter(parentFoodList)
        parentRecycler.layoutManager = LinearLayoutManager(context)
        parentRecycler.setHasFixedSize(true)
        et = view.findViewById(R.id.eett1)
        et.setOnClickListener {
            context?.startActivity(Intent(HomeScreenActivity.myContext,SearchActivity::class.java))
        }

//        val a:ArrayList<FoodCategoryModel> = ArrayList()
//        a.add(FoodCategoryModel("",""))
//        parentFoodList.add(ParentAdapterParameter(ArrayList(),a,
//            LinearLayoutManager(context),"search"
//        ))

        if(!HomeScreenActivity.isItemSettedInHomeFrag){
            Toast.makeText(HomeScreenActivity.myContext,"no item got",Toast.LENGTH_SHORT).show()
            getFeaturedData()
        }
        else{
            Toast.makeText(HomeScreenActivity.myContext,"Item Already got",Toast.LENGTH_SHORT).show()
            parentFoodList.add(
                ParentAdapterParameter(
                    ArrayList(),
                    HomeScreenActivity.featuredSnack,
                    GridLayoutManager(
                        HomeScreenActivity.myContext,
                        2,
                        GridLayoutManager.HORIZONTAL,
                        false
                    ), "food-category"
                )
            )
            parentAdapter = ParentRecyclerAdapter(parentFoodList)
            parentRecycler.adapter = parentAdapter
            parentRecycler.layoutManager = LinearLayoutManager(context)
            parentRecycler.setHasFixedSize(true)
            parentFoodList.add(
                ParentAdapterParameter(
                    HomeScreenActivity.recommendedSnack,
                    ArrayList(),
                    LinearLayoutManager(context), "recommended"
                )
            )
            parentAdapter.notifyDataSetChanged()
            parentRecycler.visibility = View.VISIBLE
            constraint.visibility = View.GONE
        }


        return view
    }

    private fun loadHomeFragData() {
        /*
        use this when all the data is coming from firebase
         */
        val featured = CoroutineScope(Dispatchers.Default).async {
            getFeaturedData()
        }

        val recommended = CoroutineScope(Dispatchers.Default).async {
            featured.await()
            getRecommendedDataFormDB()
        }
//        val loadRecommend = CoroutineScope(Dispatchers.Default).async {
//            recommended.await()
//            LoadRecommend()
//        }

    }

    private fun getFeaturedData() {
        //Log.i("my_msg", "Data started from db")
        if (HomeScreenActivity.featuredSnack.size == 0) {
            val db = FirebaseFirestore.getInstance()
            val collRef = db.collection("Food-Category")
            collRef.get().addOnSuccessListener {
                for (i in it.documents) {
                    val food: FoodCategoryModel? = i.toObject(FoodCategoryModel::class.java)
                    if (food != null) {
                        HomeScreenActivity.featuredSnack.add(food)
                    } else {
                        Log.i("my_msg", "Category is empty")
                    }
                }
                parentFoodList.add(
                    ParentAdapterParameter(
                        ArrayList(),
                        HomeScreenActivity.featuredSnack,
                        GridLayoutManager(
                            HomeScreenActivity.myContext,
                            2,
                            GridLayoutManager.HORIZONTAL,
                            false
                        ), "food-category"
                    )
                )
                parentAdapter = ParentRecyclerAdapter(parentFoodList)
                parentRecycler.adapter = parentAdapter
                parentRecycler.layoutManager = LinearLayoutManager(context)
                parentRecycler.setHasFixedSize(true)
                getRecommendedDataFormDB()
            }.addOnFailureListener {
                Log.i("my_msg", "Error in loading food category :: ${it.localizedMessage}")
                it.stackTrace
            }
        } else {
            parentFoodList.add(
                ParentAdapterParameter(
                    ArrayList(),
                    HomeScreenActivity.featuredSnack,
                    GridLayoutManager(
                        HomeScreenActivity.myContext,
                        2,
                        GridLayoutManager.HORIZONTAL,
                        false
                    ), "food-category"
                )
            )
            parentAdapter = ParentRecyclerAdapter(parentFoodList)
            parentRecycler.adapter = parentAdapter
            parentRecycler.layoutManager = LinearLayoutManager(context)
            parentRecycler.setHasFixedSize(true)
            getRecommendedDataFormDB()
        }
    }

    private fun getRecommendedDataFormDB() {
        if (HomeScreenActivity.recommendedSnack.size == 0) {
            val db = FirebaseFirestore.getInstance()
            val job = CoroutineScope(Dispatchers.IO).async {
                for (i in HomeScreenActivity.featuredSnack) {
                    val collRef = db.collection(i.foodCategoryName)
                    collRef.get().addOnSuccessListener {
                        for (j in it.documents) {
                            val id = j["food_id"]
                            val doc = db.collection("all_food").document(id.toString())
                            //Log.i("my_msg","geeting data of ${j["food_id"]}")
                            doc.get().addOnSuccessListener {data ->

                                val food: FoodItemsData? = data.toObject(FoodItemsData::class.java)
                                if (food != null) {
                                    HomeScreenActivity.recommendedSnack.add(food)
                                }
                                if(HomeScreenActivity.recommendedSnack.size == 2){
                                    Log.i("my_msg","Only two food got")
                                    parentFoodList.add(
                                        ParentAdapterParameter(
                                            HomeScreenActivity.recommendedSnack,
                                            ArrayList(),
                                            LinearLayoutManager(context), "recommended"
                                        )
                                    )
                                    parentRecycler.visibility = View.VISIBLE
                                    constraint.visibility = View.GONE
                                    parentAdapter.notifyDataSetChanged()
                                }

                            }.addOnFailureListener {data ->
                                Log.i("my_msg", "Error in getting data from food db${data.localizedMessage}")
                            }

                        }
                    }.addOnFailureListener {
                        Log.i("my_msg", "Error in data from specific collection${it.localizedMessage}")
                    }
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({
//                parentFoodList.add(
//                    ParentAdapterParameter(
//                        HomeScreenActivity.recommendedSnack,
//                        ArrayList(),
//                        LinearLayoutManager(context), "recommended"
//                    )
//                )
                parentAdapter.notifyDataSetChanged()
                Log.i("my_msg", "All food got")
            }, 1000)
            HomeScreenActivity.isItemSettedInHomeFrag = true

        }
        else{
            parentFoodList.add(
                ParentAdapterParameter(
                    HomeScreenActivity.recommendedSnack,
                    ArrayList(),
                    LinearLayoutManager(context), "recommended"
                )
            )
            parentAdapter.notifyDataSetChanged()
            HomeScreenActivity.isItemSettedInHomeFrag = true
            parentRecycler.visibility = View.VISIBLE
            constraint.visibility = View.GONE
        }
    }
//    private fun LoadRecommend(){
//        parentFoodList.add(
//            ParentAdapterParameter(
//                HomeScreenActivity.recommendedSnack,
//                ArrayList(),
//                LinearLayoutManager(context), "recommended"
//            )
//        )
//        parentAdapter.notifyDataSetChanged()
//        Log.i("my_msg", "All data got.")
//
//    }

    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // this function is called before text is edited
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // this function is called when text is edited
            Log.i("my_msg",s.toString())
            //("text is edited and onTextChangedListener is called.")
        }

        override fun afterTextChanged(s: Editable) {
            // this function is called after text is edited
        }
    }

}