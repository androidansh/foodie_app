package com.example.food_order_app

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.food_order_app.cart_n_place_order.My_OrdersActivity
import com.example.food_order_app.coroutine_tasks.ThreadTasks
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.FoodieUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackOrderService: Service() {

    private var uniqueId:String ?= null
    override fun onBind(intent: Intent?): IBinder? {
        return  null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        uniqueId = intent!!.getStringExtra("id")
        when(intent.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }


        return super.onStartCommand(intent, flags, startId)
    }

    enum class Actions{
        START,STOP
    }

    @SuppressLint("ForegroundServiceType")
    private fun start(){
        Log.i("my_msg","Service started")
        val notification = NotificationCompat.Builder(this,"order")
            .setSmallIcon(R.drawable.foodie_logo_3)
            .setContentTitle("OrderPlaced")
            .setContentText("Start tracking your order!")
            .setContentIntent(HomeScreenActivity.pendingIntent)
            .build()
        CoroutineScope(Dispatchers.Default).launch {
            var time:Int = 120;
            startForeground(1,notification)
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            while(time != 80){
                time--
                if(time == 100){
                    Log.i("my_msg","Time = 30 sec ")
                    val midNotification = NotificationCompat.Builder(this@TrackOrderService,"food")
                        .setSmallIcon(R.drawable.foodie_logo_3)
                        .setContentText("Food arrived at our facility")
                        .setContentTitle("Your food is ready to go!.")
                        .setContentIntent(HomeScreenActivity.pendingIntent)
                        .build()
                    nm.notify(1,midNotification)
                }
                delay(1000)
            }
            // after two minutes close the service and notification
            Log.i("my_msg","Service ended")
            // notification code

            val endNotification = NotificationCompat.Builder(this@TrackOrderService,"food")
                .setSmallIcon(R.drawable.foodie_logo_3)
                .setContentText("Your order is delivered.")
                .setContentTitle("Knock! Knock!")
                .setContentIntent(HomeScreenActivity.pendingIntent)
                .build()
            nm.notify(2,endNotification)
            stopSelf()
            var userOrder = HomeScreenActivity.foodieUser.orders
            for(i in userOrder){
                if(i.uniqueID == uniqueId){
                    i.delivered = true
                }
            }
            val doc = FirebaseFirestore.getInstance().collection("FoodieUser").document(FirebaseAuth.getInstance().currentUser!!.uid)
            doc.update("orders",userOrder).addOnSuccessListener {
                doc.get().addOnSuccessListener {
                    val user:FoodieUser ?= it.toObject(FoodieUser::class.java)
                    if(user != null){
                        HomeScreenActivity.foodieUser = user
                        if(My_OrdersActivity.orderArray.size != 0){
                            My_OrdersActivity.orderArray.removeAll(My_OrdersActivity.orderArray)
                        }
                        for( data in HomeScreenActivity.foodieUser.orders){
                            My_OrdersActivity.orderArray.add(data)
                        }
                        My_OrdersActivity.orderArray.sort()
                        My_OrdersActivity.adapter.notifyDataSetChanged()
                // syncing user data
                        CoroutineScope(Dispatchers.IO).launch {
                            ThreadTasks.syncUserData()
                        }
//                        val doc = FirebaseFirestore.getInstance().collection("FoodieUser").document(FirebaseAuth.getInstance().currentUser!!.uid)
//                        doc.get().addOnSuccessListener {
//                            val user: FoodieUser?= it.toObject(FoodieUser::class.java)
//                            if(user != null){
//                                HomeScreenActivity.foodieUser = user
//                                //HomeScreenActivity.userOrders = HomeScreenActivity.foodieUser.orders
//                            }
//                        }.addOnFailureListener {
//                            Log.i("my_msg","Error in getting user data from db in track order service")
//                        }
//                        val userPref = getSharedPreferences("FoodieUser", MODE_PRIVATE)
//                        val gson = Gson()
//                        val jsonStr = gson.toJson(HomeScreenActivity.foodieUser)
//                        val edit = userPref?.edit()
//                        edit?.putString("user_data",jsonStr)
//                        edit?.apply()
                    }
                }.addOnFailureListener {
                    Log.i("my_msg","Error in getting user data from db in track order service")
                }
            }.addOnFailureListener {
                Log.i("my_msg","Error in setting order data in db in track order service")
            }
        }
    }

}