package com.example.food_order_app.cart_n_place_order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.food_order_app.R
import com.example.food_order_app.TrackOrderService
import com.example.food_order_app.coroutine_tasks.ThreadTasks
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.FoodieUser
import com.example.food_order_app.model.OrderFoodDB
import com.example.food_order_app.model.OrderFood_ID_Num
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
    private var tax:Int = 0

    private lateinit var userName:TextInputEditText
    private lateinit var userAddress:TextInputEditText
    private lateinit var userPhone:TextInputEditText
    private lateinit var cartCostTextView:TextView
    private lateinit var extraCostTextView:TextView
    private lateinit var totalCostTextView:TextView
    private lateinit var payMoney: Button
    private lateinit var trackOrder: Button
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
        trackOrder = findViewById(R.id.TrackOrder)
        trackOrder.visibility = View.INVISIBLE
        back = findViewById(R.id.orderBack)

        back.setOnClickListener { finish() }
        setData()
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
//            CoroutineScope(Dispatchers.Default).launch {
//
//
//                val userID = FirebaseAuth.getInstance().currentUser?.uid
//                if(userID != null){
//                    val docRef = FirebaseFirestore.getInstance().collection("FoodieUser").document(userID)
//                    docRef.set(user!!).addOnSuccessListener {
//                        val pref = getSharedPreferences("FoodieUser", MODE_PRIVATE)
//                        val edit = pref.edit()
//                        val gson = Gson()
//                        val jsonStr:String = gson.toJson(user)
//                        edit.putString("userData",jsonStr)
//                        edit.apply()
//                    }.addOnFailureListener{
//                        Log.i("my_msg","Error in setting data to db.")
//                    }
//                }
//            }
            HomeScreenActivity.foodieUser.address = userAddress.text.toString()
            HomeScreenActivity.foodieUser.phone = userPhone.text.toString()
            HomeScreenActivity.foodieUser.userName = userName.text.toString()
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
             orderRequest.put("prefill.contact",HomeScreenActivity.foodieUser.phone)
            co.open(this,orderRequest)

        }
        catch (e:Exception){
            Log.i("my_msg","Error in starting razorpay checkout : ${e.printStackTrace()}")
        }


        // val order: Order = instance.orders.create(orderRequest)
    }

//    private fun loadUserData(){
//        val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return
//        val docRef = FirebaseFirestore.getInstance().collection("FoodieUser").document(userID)
//        docRef.get().addOnSuccessListener {
//            user = it.toObject(FoodieUser::class.java)
//            if(user != null){
//                setData()
//            }
//        }.addOnFailureListener {
//            Log.i("my_msg","Error in getting data of user from db.")
//        }
//    }

    private fun setData(){
        userName.setText(HomeScreenActivity.foodieUser.userName)
        userAddress.setText(HomeScreenActivity.foodieUser.address)
        userPhone.setText(HomeScreenActivity.foodieUser.phone)
        cartCostTextView.text = "₹ $totalCartCost"
        extraCostTextView.text = "₹ $tax"
        totalCostTextView.text = "₹ ${totalCartCost + tax}"
    }

    @SuppressLint("SimpleDateFormat")
    override fun onPaymentSuccess(s: String?) {
        Log.i("my_msg","Success = $s")
        payMoney.visibility = View.INVISIBLE
        trackOrder.visibility = View.VISIBLE
        Toast.makeText(applicationContext,"Payment Success!!",Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            // to put cart item to order list

            val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val currTime: String = SimpleDateFormat("HH:mm").format(Date())

            var doc = FirebaseFirestore.getInstance().collection("FoodieUser").document(FirebaseAuth.getInstance().currentUser!!.uid)
            val order = OrderFoodDB(ArrayList(), currentDate)
            order.date = currentDate
            order.uniqueID = s!!.substring(7)
            order.time = currTime
            order.totalPrice = (totalCartCost + tax).toString()

            for(i in CartActivity.recentCart.keys){
                val obj = OrderFood_ID_Num()
                obj.id = i
                obj.num = CartActivity.recentCart.get(i)?.foodNumber.toString()
                order.food.add(obj)

                HomeScreenActivity.userCartMap.remove(i)
                CartActivity.foodArray.remove(CartActivity.recentCart.get(i))
            }
           // HomeScreenActivity.userOrders.add(order)
            HomeScreenActivity.foodieUser.orders.add(order)
// adding orders to user db and then syncing data
            doc.update("orders",FieldValue.arrayUnion(order)).addOnSuccessListener {
                Log.i("my_msg","Order added to db of ${order.date}")
            }.addOnFailureListener {
                Log.i("my_msg","Error in adding order from cart to db of user")
            }
            doc = FirebaseFirestore.getInstance().collection("FoodieUser").document(FirebaseAuth.getInstance().currentUser!!.uid)


// removing food from cart
//            for( i in CartActivity.recentCart.keys){
//                HomeScreenActivity.userCartMap.remove(i)
//                CartActivity.foodArray.remove(CartActivity.recentCart.get(i))
//            }

// update cart activity ui for changes in food cart data
            val handler = Handler(Looper.getMainLooper())
            handler.post{
                CartActivity.cartTotal.text = "0"
                CartActivity.foodAdapter.notifyDataSetChanged()
            }
// updating new cart data to user db and then syncing user data along with cart
            HomeScreenActivity.foodieUser.orders.add(order)
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            doc = FirebaseFirestore.getInstance().collection("FoodieUser").document(userId!!)
            val list:ArrayList<String> = ArrayList()
            for( l in HomeScreenActivity.userCartMap.keys){
                list.add(l)
            }
            doc.update("cart",list).addOnSuccessListener {
                Log.i("my_msg","Success user cart added to db")
            }.addOnFailureListener{
                Log.i("my_msg","Error in adding user cart to db")
            }
            ThreadTasks.syncUserData()
// starting service
            Intent(applicationContext, TrackOrderService::class.java).also {
                it.action = TrackOrderService.Actions.START.toString()
                it.putExtra("id",order.uniqueID)
                applicationContext?.startService(it)
            }
        }

    }

    override fun onPaymentError(i: Int, s: String?) {
        Log.i("my_msg","Error = $s")
        Toast.makeText(applicationContext,"Payment Error!!",Toast.LENGTH_SHORT).show()
    }

}