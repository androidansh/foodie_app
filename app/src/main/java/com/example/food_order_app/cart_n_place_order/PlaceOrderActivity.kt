package com.example.food_order_app.cart_n_place_order

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.food_order_app.R
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.FoodieUser
import com.example.food_order_app.model.OrderFood
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale


class PlaceOrderActivity : AppCompatActivity(), PaymentResultListener {
    private var user:FoodieUser ?= null
    private var tax:Int = 0

    private lateinit var userName:TextInputEditText
    private lateinit var userAddress:TextInputEditText
    private lateinit var userPhone:TextInputEditText
    private lateinit var cartCostTextView:TextView
    private lateinit var extraCostTextView:TextView
    private lateinit var totalCostTextView:TextView
    private lateinit var payMoney: Button
    private lateinit var back:ImageView
    private var totalCartCost:Int = 0
    private var s = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)
        Checkout.preload(applicationContext)


//        s = this.getClass().getSimpleName()
        totalCartCost = intent.getIntExtra("totalCost",0)
        tax = if(totalCartCost < 500){
            20
        } else if(totalCartCost in 501..1000){
            30
        } else{
            40
        }

        userName = findViewById(R.id.orderUserName)
        userAddress = findViewById(R.id.orderUserAddress)
        userPhone = findViewById(R.id.orderUserPhone)
        cartCostTextView = findViewById(R.id.costOfCart)
        extraCostTextView = findViewById(R.id.extraCharges)
        totalCostTextView = findViewById(R.id.totalCharges)
        payMoney = findViewById(R.id.payMoney)
        back = findViewById(R.id.orderBack)


        loadUserData()

        back.setOnClickListener { finish() }

        payMoney.setOnClickListener {



            if(userName.text.toString() == ""){
                userName.error = "User Name cannot be empty"
                return@setOnClickListener
            }
            if(userAddress.text.toString() == ""){
                userAddress.error = "User address cannot be empty."
                return@setOnClickListener
            }
            if(userPhone.text.toString() == ""){
                userPhone.error = "User phone cannot be empty."
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.Default).launch {
                user?.address = userAddress.text.toString()
                user?.phone = userPhone.text.toString()
                user?.userName = userName.text.toString()

                val userID = FirebaseAuth.getInstance().currentUser?.uid
                if(userID != null){
                    val docRef = FirebaseFirestore.getInstance().collection("FoodieUser").document(userID)
                    docRef.set(user!!).addOnSuccessListener {
                        val pref = getSharedPreferences("FoodieUser", MODE_PRIVATE)
                        val edit = pref.edit()
                        val gson = Gson()
                        val jsonStr:String = gson.toJson(user)
                        edit.putString("userData",jsonStr)
                        edit.apply()
                    }.addOnFailureListener{
                        Log.i("my_msg","Error in setting data to db.")
                    }
                }
            }
            pay_money_to_foodie()
        }
    }

    private fun pay_money_to_foodie(){

        val co = Checkout()
        co.setFullScreenDisable(true)
        co.setKeyID("rzp_test_QG3LGzmw7dTFOG")
        try{
            val orderRequest = JSONObject()
            orderRequest.put("name","Foodie")
            orderRequest.put("description","Ref ID = @6611")
//        orderRequest.put("image","https://")
            orderRequest.put("theme.color","#3399cc")
            orderRequest.put("amount", (totalCartCost + tax) * 100) // amount in paise
            orderRequest.put("currency", "INR")
            // orderRequest.put("prefill.email","anshuman@gmail.com")
             orderRequest.put("prefill.contact",user?.phone)
            co.open(this,orderRequest)

        }
        catch (e:Exception){
            Log.i("my_msg","Error in starting razorpay checkout : ${e.printStackTrace()}")
        }


        // val order: Order = instance.orders.create(orderRequest)
    }

    private fun loadUserData(){
        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val docRef = FirebaseFirestore.getInstance().collection("FoodieUser").document(userID)
        docRef.get().addOnSuccessListener {
            user = it.toObject(FoodieUser::class.java)
            if(user != null){
                setData()
            }
        }.addOnFailureListener {
            Log.i("my_msg","Error in getting data of user from db.")
        }
    }

    private fun setData(){
        userName.setText(user?.userName)
        userAddress.setText(user?.address)
        userPhone.setText(user?.phone)
        cartCostTextView.text = "₹ $totalCartCost"
        extraCostTextView.text = "₹ $tax"
        totalCostTextView.text = "₹ ${totalCartCost + tax}"
    }

    override fun onPaymentSuccess(s: String?) {
        Log.i("my_msg","Success = $s")

        // to put cart item to order list
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        CoroutineScope(Dispatchers.IO).launch {
            val doc = FirebaseFirestore.getInstance().collection("FoodieUser").document(FirebaseAuth.getInstance().currentUser!!.uid)
            val order = OrderFood(ArrayList(), currentDate)
            order.date = currentDate
            for(i in CartActivity.recentCart.keys){
                order.food.add(i)
                Log.i("my_msg","Order added = $i")

            }
            HomeScreenActivity.userOrders.add(order)
            HomeScreenActivity.foodieUser.orders.add(order)

            doc.update("orders",FieldValue.arrayUnion(order)).addOnSuccessListener {
                Log.i("my_msg","Order added to db of ${order.date}")
            }.addOnFailureListener {
                Log.i("my_msg","Error in adding order from cart to db of user")
            }

            for( i in CartActivity.recentCart.keys){
                HomeScreenActivity.userCartMap.remove(i)
                CartActivity.foodArray.remove(CartActivity.recentCart.get(i))
            }


            val handler = Handler(Looper.getMainLooper())
            handler.post{
                CartActivity.cartTotal.text = "0"
                CartActivity.foodAdapter.notifyDataSetChanged()
            }
        }

        Toast.makeText(applicationContext,"Payment Success!!",Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(i: Int, s: String?) {
        Log.i("my_msg","Error = $s")
        Toast.makeText(applicationContext,"Payment Error!!",Toast.LENGTH_SHORT).show()
    }

}