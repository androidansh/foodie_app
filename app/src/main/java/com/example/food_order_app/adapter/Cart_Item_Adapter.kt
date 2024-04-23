package com.example.food_order_app.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_order_app.cart_n_place_order.CartActivity
import com.example.food_order_app.IndividualActivity.Food_detailed_Activity
import com.example.food_order_app.R
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.CartFood
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Cart_Item_Adapter(private var foodArray:ArrayList<CartFood>) : RecyclerView.Adapter<Cart_Item_Adapter.Viewholder>() {

    lateinit var context:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        this.context = parent.context
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout,parent,false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val food = foodArray[position]
        Glide.with(context).load(food.foodPic).into(holder.foodPic)

        holder.foodPic.setOnClickListener {
            HomeScreenActivity.clickedFood = food
            context.startActivity(Intent(CartActivity.context, Food_detailed_Activity::class.java))
        }
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                val price:Int ?= food.foodRate.toIntOrNull()
                CartActivity.recentCart.put(food.foodUID,food)
                var totalPrice:Int ?= CartActivity.cartTotal.text.toString().toIntOrNull()
                if(price != null && totalPrice != null){
                    totalPrice += price * food.foodNumber
                    holder.foodCost2.text = "Cost : ${price*food.foodNumber}"
                    CartActivity.cartTotal.text = totalPrice.toString()
                    Log.i("my_msg","Food to order cart = ${food.foodUID} and cart size =  ${CartActivity.recentCart.size}")

                }
            }
            else{
                val price:Int ?= food.foodRate.toIntOrNull()
                CartActivity.recentCart.remove(food.foodUID)
                Log.i("my_msg","Food removed from order cart = ${food.foodUID} and cart size =  ${CartActivity.recentCart.size}")
                var totalPrice:Int ?= CartActivity.cartTotal.text.toString().toIntOrNull()
                if(price != null && totalPrice != null){
                    totalPrice -= price
                    holder.foodCost2.text = "Cost : ${price*food.foodNumber}"
                    CartActivity.cartTotal.text = totalPrice.toString()
                }
            }
        }


        holder.foodProvider.text = food.foodProviderName
        //holder.foodRatingPoint.text = food.foodRatingPoint.toString()
        holder.foodNum.text = food.foodNumber.toString()
        if(food.foodNumber == 1){
            holder.foodMinus.setBackgroundResource(R.drawable.bin2)
        }
        holder.foodCost.text = "Cost : ${food.foodRate} for one"
        val price:Int ?= food.foodRate.toIntOrNull()
        if(price != null){
            holder.foodCost2.text = "Cost : ${price*food.foodNumber}"
        }

        holder.foodMinus.setOnClickListener {
                food.foodNumber--
                if(food.foodNumber == 0){
                    showDialog(food,holder)
                    return@setOnClickListener
                }
                if(holder.checkBox.isChecked){
                    val price:Int ?= food.foodRate.toIntOrNull()
                    var totalPrice:Int ?= CartActivity.cartTotal.text.toString().toIntOrNull()
                    if(price != null && totalPrice != null){
                        totalPrice -= price
                        holder.foodCost2.text = "Cost : ${price*food.foodNumber}"
                        CartActivity.cartTotal.text = totalPrice.toString()
                    }
                }
                holder.foodNum.text = food.foodNumber.toString()
                if(food.foodNumber == 1){
                    holder.foodMinus.setBackgroundResource(R.drawable.bin2)
            }

        }
        holder.foodPlus.setOnClickListener {
            if(food.foodNumber == 1){
                holder.foodMinus.setBackgroundResource(R.drawable.minus_dark)
            }
            food.foodNumber++
            holder.foodNum.text = food.foodNumber.toString()
            if(holder.checkBox.isChecked){
                val price:Int ?= food.foodRate.toIntOrNull()
                var totalPrice:Int ?= CartActivity.cartTotal.text.toString().toIntOrNull()
                if(price != null && totalPrice != null){
                    totalPrice += price
                    holder.foodCost2.text = "Cost : ${price*food.foodNumber}"
                    CartActivity.cartTotal.text = totalPrice.toString()

                }

            }
        }
    }

    private fun showDialog(food:CartFood,holder:Viewholder){

        val dialog:Dialog = Dialog(context,R.style.dialog_style)
        dialog.setContentView(R.layout.cart_remove_dialog_layout)
        dialog.window?.setBackgroundDrawableResource(R.drawable.white_round_corner_btn)
        val cancel: Button = dialog.findViewById(R.id.cancel_dialog)
        val confirm : Button = dialog.findViewById(R.id.confirm_dialog)
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        confirm.setOnClickListener {
            HomeScreenActivity.userCartMap.remove(food.foodUID)
            foodArray.remove(food)
            val price:Int ?= food.foodRate.toIntOrNull()
            var totalPrice:Int ?= CartActivity.cartTotal.text.toString().toIntOrNull()
            if(price != null && totalPrice != null){
                totalPrice -= price
                holder.foodCost2.text = "Cost : ${price*food.foodNumber}"
                CartActivity.cartTotal.text = totalPrice.toString()
                CartActivity.foodAdapter.notifyDataSetChanged()
            }
            dialog.dismiss()
            CoroutineScope(Dispatchers.IO).launch {

                val pref = context.getSharedPreferences("UserCartData", AppCompatActivity.MODE_PRIVATE)
                val gson = Gson()
                val jsonStr = gson.toJson(HomeScreenActivity.userCartMap)
                val edit = pref.edit()
                edit.putString("cartFood",jsonStr)
                edit.apply()

                val userId = FirebaseAuth.getInstance().currentUser?.uid
                val doc = FirebaseFirestore.getInstance().collection("FoodieUser").document(userId!!)
                val list:ArrayList<String> = ArrayList()
                for( l in HomeScreenActivity.userCartMap.keys){
                    list.add(l)
                }
                doc.update("cart",FieldValue.arrayRemove(food.foodUID)).addOnSuccessListener {
                    Log.i("my_msg","Success user cart added to db")
                }.addOnFailureListener{
                    Log.i("my_msg","Error in adding user cart to db")
                }
            }
        }
        dialog.show()
    }

    override fun getItemCount(): Int {
        return foodArray.size
    }

    class Viewholder(var itemview: View) : RecyclerView.ViewHolder(itemview){

        var foodPic:ImageView = itemview.findViewById(R.id.cartFoodImage)
        var foodMinus:ImageView = itemview.findViewById(R.id.foodMinusCart)
        var foodPlus:ImageView = itemview.findViewById(R.id.foodPlusCart)
        var foodNum:TextView = itemview.findViewById(R.id.foodNumCart)
        var foodProvider:TextView = itemview.findViewById(R.id.foodCartProvider)
        var checkBox:CheckBox = itemview.findViewById(R.id.checkBox)
        var foodCost:TextView = itemview.findViewById(R.id.foodCostCart)
        var foodCost2:TextView = itemview.findViewById(R.id.foodCostCart2)

    }
}