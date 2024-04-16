package com.example.food_order_app.home_screen

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.example.food_order_app.R
import com.example.food_order_app.cart_n_place_order.CartActivity
import com.example.food_order_app.coroutine_tasks.ThreadTasks
import com.example.food_order_app.model.CartFood
import com.example.food_order_app.model.FoodCategoryModel
import com.example.food_order_app.model.FoodItemsData
import com.example.food_order_app.model.FoodieUser
import com.example.food_order_app.model.OrderFoodDB
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class HomeScreenActivity : AppCompatActivity() {
    private lateinit var relative1:RelativeLayout
    private lateinit var relative2:RelativeLayout
    private lateinit var relative3:RelativeLayout
    private lateinit var dinnerBar:ImageView
    private lateinit var dinnerPic:ImageView
    private lateinit var scooterBar:ImageView
    private lateinit var scooterPic:ImageView
    private lateinit var walletBar:ImageView
    private lateinit var walletPic:ImageView
    private lateinit var scooterText:TextView
    private lateinit var dinnerText:TextView
    private lateinit var walletTExt:TextView
    companion object{

        lateinit var pendingIntent:PendingIntent

        lateinit var myContext: Context
        var featuredSnack:ArrayList<FoodCategoryModel> = ArrayList()
        var dinnerSubMenu:ArrayList<FoodCategoryModel> = ArrayList()
        var dinnerMustTry:ArrayList<FoodCategoryModel> = ArrayList()
        var recommendedSnack:ArrayList<FoodItemsData> = ArrayList()
        var recommendedDinner:ArrayList<FoodItemsData> = ArrayList()
        var clickedFood:CartFood ?= null
        lateinit var windowFrame: Window
        var isItemSettedInHomeFrag:Boolean = false
        lateinit var userCartMap:HashMap<String,CartFood>
        lateinit var foodieUser:FoodieUser
        var search_all_food_map:HashMap<String,FoodItemsData> = HashMap()
        var search_food_key_word:HashMap<String,ArrayList<String>> = HashMap()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen_activity)
        myContext = applicationContext
        ThreadTasks.context = myContext
//        CoroutineScope(Dispatchers.Main).launch{
//            ThreadTasks.syncUserData()
//        }
        val intent: Intent = Intent(this, CartActivity::class.java)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("type","NotifyIntent")
        pendingIntent = PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val userPref = getSharedPreferences("FoodieUser", MODE_PRIVATE)
        if(userPref.getString("user_data","") == ""){
            CoroutineScope(Dispatchers.IO).launch{
                ThreadTasks.syncUserData()
            }
        }else{
//            CoroutineScope(Dispatchers.IO).launch{
//                ThreadTasks.syncUserData()
//            }
            val gson = Gson() // getting user data stored in shared pref
            val str = userPref.getString("user_data","")
            if(str != ""){
                foodieUser = gson.fromJson(str,FoodieUser::class.java)
                //userOrders = foodieUser.orders
                foodieUser.orders.sort()
            }
            val pref = getSharedPreferences("UserCartData", MODE_PRIVATE) // getting user cart data from shared pref
            val jsonStr = pref.getString("cartFood","")
            val type = object : TypeToken<HashMap<String,CartFood>>() {}.type
            userCartMap = if(jsonStr == ""){
                HashMap()
            }
            else{
                Log.i("my_msg","Data = $jsonStr")
                gson.fromJson(jsonStr,type)
            }

        }

        windowFrame = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)


        relative1 = findViewById(R.id.relative1)
        relative2 = findViewById(R.id.relative2)
        relative3 = findViewById(R.id.relative3)

        scooterBar = findViewById(R.id.scooter_bar)
        scooterPic = findViewById(R.id.scooter_pic)
        scooterText = findViewById(R.id.scooterText)
        dinnerBar = findViewById(R.id.dinner_bar)
        dinnerPic = findViewById(R.id.dinner_pic)
        dinnerText = findViewById(R.id.dinnerText)
        walletBar = findViewById(R.id.money_bar)
        walletPic = findViewById(R.id.money_pic)
        walletTExt = findViewById(R.id.walletText)

        scooterBar.visibility = View.VISIBLE
        scooterPic.setBackgroundResource(R.drawable.scooter_delivery)
        scooterText.setTextColor(getColor(R.color.headingColor))
        val fragTransaction:FragmentTransaction = supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.homeFrame,HomeFrag())
        fragTransaction.commit()
        window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)

        relative1.setOnClickListener {
            val fragTransaction1:FragmentTransaction = supportFragmentManager.beginTransaction()
            fragTransaction1.replace(R.id.homeFrame,HomeFrag())
            fragTransaction1.commit()
            window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)

            scooterBar.visibility = View.VISIBLE
            scooterPic.setBackgroundResource(R.drawable.scooter_delivery)
            scooterText.setTextColor(getColor(R.color.headingColor))

            dinnerBar.visibility= View.INVISIBLE
            dinnerPic.setBackgroundResource(R.drawable.dinner_gray)
            dinnerText.setTextColor(getColor(R.color.textColor))

            walletBar.visibility= View.INVISIBLE
            walletPic.setBackgroundResource(R.drawable.wallet_gray)
            walletTExt.setTextColor(getColor(R.color.textColor))
        }

        relative2.setOnClickListener {

            val fragTransaction1:FragmentTransaction = supportFragmentManager.beginTransaction()
            fragTransaction1.replace(R.id.homeFrame,DinnerFrag())
            fragTransaction1.commit()
            window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)
            scooterBar.visibility = View.INVISIBLE
            scooterPic.setBackgroundResource(R.drawable.scooter_delivery_gray)

            dinnerBar.visibility= View.VISIBLE
            dinnerPic.setBackgroundResource(R.drawable.dinner)
            walletBar.visibility= View.INVISIBLE
            walletPic.setBackgroundResource(R.drawable.wallet_gray)

            scooterText.setTextColor(getColor(R.color.textColor))
            dinnerText.setTextColor(getColor(R.color.headingColor))
            walletTExt.setTextColor(getColor(R.color.textColor))
        }
        relative3.setOnClickListener {

            val fragTransaction1:FragmentTransaction = supportFragmentManager.beginTransaction()
            fragTransaction1.replace(R.id.homeFrame,AccountFrag())
            fragTransaction1.commit()
            window.statusBarColor = ContextCompat.getColor(this, R.color.acc_frag_bg_color)
            scooterBar.visibility = View.INVISIBLE
            scooterPic.setBackgroundResource(R.drawable.scooter_delivery_gray)
            dinnerBar.visibility= View.INVISIBLE
            dinnerPic.setBackgroundResource(R.drawable.dinner_gray)
            walletBar.visibility= View.VISIBLE
            walletPic.setBackgroundResource(R.drawable.wallet)
            scooterText.setTextColor(getColor(R.color.textColor))
            dinnerText.setTextColor(getColor(R.color.textColor))
            walletTExt.setTextColor(getColor(R.color.headingColor))
        }

        get_search_data()

        // creating coroutines
       // manageCoroutine()

    }

    private fun get_search_data(){
        CoroutineScope(Dispatchers.IO).launch {
            val db = FirebaseFirestore.getInstance()
            val coll = db.collection("all_food")

            coll.get().addOnSuccessListener {

                for(data in it.documents){
                    val food: FoodItemsData? = data.toObject(FoodItemsData::class.java)
                    if (food != null) {
                        search_all_food_map[food.foodUID] = food
                        val array:ArrayList<String> = ArrayList()
                        array.add(food.foodDesc1.toLowerCase())
                        array.add(food.foodDesc2.toLowerCase())
                        array.add(food.foodDesc3.toLowerCase())
                        array.add(food.foodCategory.toLowerCase())
                        array.add(food.foodProviderName.toLowerCase())
                        search_food_key_word[food.foodUID] = array
                    }
                }

            }.addOnFailureListener {
                Log.i("my_msh","Error in getting data for search bar")
                it.printStackTrace()
            }
        }
    }







    private fun manageCoroutine() {
        CoroutineScope(Dispatchers.IO).launch {
            // getting featured food
            val def1 = CoroutineScope(Dispatchers.IO).launch {
                ThreadTasks.getFeaturedFood()
            }



        }
    }
}