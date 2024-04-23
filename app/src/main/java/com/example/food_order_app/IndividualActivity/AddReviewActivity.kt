package com.example.food_order_app.IndividualActivity

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_order_app.R
import com.example.food_order_app.adapter.ReviewAdapter
import com.example.food_order_app.coroutine_tasks.ThreadTasks
import com.example.food_order_app.databinding.ActivityAddReviewBinding
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.CartFood
import com.example.food_order_app.model.FoodItemsData
import com.example.food_order_app.model.ReviewClass
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections

class AddReviewActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddReviewBinding
    private lateinit var reviewList:ArrayList<ReviewClass>

    private lateinit var context:Context
    private var toSwapPos:Int = 0
    var canAddReview:Boolean = true

    companion object{
        lateinit var adapter: ReviewAdapter

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = applicationContext
        reviewList = HomeScreenActivity.clickedFood?.foodRating ?: ArrayList()

        adapter = ReviewAdapter(reviewList,"2")
        binding.reviewActivityRecycler.adapter = adapter
        binding.reviewActivityRecycler.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.IO).launch {
        // make sure to always launch after initialising adapter
        // it makes sure that if there exists user review then show it at top
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            for(i in 0 until reviewList.size){
                if(reviewList[i].reviewUID == userId){
                    toSwapPos = i
                    canAddReview = false
                    break
                }
            }
            if(toSwapPos != 0){
                Log.i("my_msg","At position = $toSwapPos")
                Collections.swap(reviewList, toSwapPos,0)
                adapter.notifyDataSetChanged()
            }
        }


        binding.addReviewFab.setOnClickListener {
            Log.i("my_msg","Is user review added = ${canAddReview}")
            if(canAddReview){
                addReview(this)

            }else{
                editReview(this)
            }


        }

    }

    private fun editReview(context: AddReviewActivity) {
        val reviewFood:ReviewClass = reviewList[0]
        val dialog = Dialog(context,R.style.dialog_style)
        dialog.setContentView(R.layout.edit_review)
        dialog.window?.setBackgroundDrawableResource(R.drawable.white_round_corner_btn)

        val cancel: Button = dialog.findViewById(R.id.cancelEditReview)
        val confirm : Button = dialog.findViewById(R.id.confirmEditReview)
        val reviewText: TextInputEditText = dialog.findViewById(R.id.editReviewTextData)
        val deleteReview:ImageView = dialog.findViewById(R.id.deleteReview)
        var review_point: Int
        val reviewBar:RatingBar = dialog.findViewById(R.id.editRatingBar)

        // setting default value for views
        reviewText.setText(reviewFood.reviewText)
        review_point = reviewFood.reviewPoint
        reviewBar.progress = review_point

        reviewBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if(rating < 1.0f){
                reviewBar.rating = 1.0F
                review_point = 1
            }
            else{
                review_point = rating.toInt()
            }
        }

        cancel.setOnClickListener { dialog.dismiss() }

        confirm.setOnClickListener {
            dialog.dismiss()
            if (reviewText.text.toString().isEmpty()){
                reviewText.error = "empty review can not be posted."
                return@setOnClickListener
            }
            val review = ReviewClass()
            Food_detailed_Activity.h = "edit"
            review.reviewText = reviewText.text.toString()
            review.reviewPoint = review_point
            review.reviewerName = HomeScreenActivity.foodieUser.userName
            review.reviewUID = HomeScreenActivity.foodieUser.userUid
            HomeScreenActivity.clickedFood!!.foodRating[0] = review
            Food_detailed_Activity.reviewAdapter.notifyItemChanged(0)
            reviewList[0] = review
            // replacing old review with new one
            var c = 0F
            for(i in HomeScreenActivity.clickedFood!!.foodRating){
            // can be optimised if the review arraylist become so large
                c += i.reviewPoint
            }
            c /= HomeScreenActivity.clickedFood!!.foodRating.size
            HomeScreenActivity.clickedFood!!.foodRatingPoint = c.toString()
            adapter.notifyItemChanged(0)
            CoroutineScope(Dispatchers.IO).launch {
                updateReviewInDB()
            }

        }

        deleteReview.setOnClickListener {
           // reviewList.removeAt(0)
            HomeScreenActivity.clickedFood!!.foodRating.remove(reviewFood)
            adapter.notifyItemRangeRemoved(0,1)
            Food_detailed_Activity.reviewAdapter.notifyItemRangeRemoved(0,1)

            var c = 0F
            Food_detailed_Activity.h = "delete"
            for(i in HomeScreenActivity.clickedFood!!.foodRating){
            // can be optimised if the review arraylist become so large
                c += i.reviewPoint
            }
            c /= (HomeScreenActivity.clickedFood!!.foodRating.size)
            HomeScreenActivity.clickedFood!!.foodRatingPoint = c.toString()
            CoroutineScope(Dispatchers.IO).launch {
                updateReviewInDB()
            }

            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addReview(context: AddReviewActivity) {
        val review = ReviewClass()
        val dialog = Dialog(context,R.style.dialog_style)
        dialog.setContentView(R.layout.add_review_dialog_layout)
        dialog.window?.setBackgroundDrawableResource(R.drawable.white_round_corner_btn)

        var review_point = 1
        val cancel: Button = dialog.findViewById(R.id.cancelReview)
        val confirm : Button = dialog.findViewById(R.id.confirmReview)
        val reviewText: TextInputEditText = dialog.findViewById(R.id.reviewTextData)

        val reviewBar:RatingBar = dialog.findViewById(R.id.ratingBar1)
        reviewBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if(rating < 1.0f){
                reviewBar.rating = 1.0F
                review_point = 1
            }
            else{
                review_point = rating.toInt()
            }
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        confirm.setOnClickListener {
            if (reviewText.text.toString().isEmpty()){
                reviewText.error = "empty review can not be posted."
                return@setOnClickListener
            }
            review.reviewText = reviewText.text.toString()
            Log.i("my+msg","Review Point = $review_point")
            review.reviewPoint = review_point
            review.reviewerName = HomeScreenActivity.foodieUser.userName
            review.reviewUID = HomeScreenActivity.foodieUser.userUid
            HomeScreenActivity.clickedFood!!.foodRating.add(review)
            Food_detailed_Activity.h = "edit"

            var c:Float = 0F;
            for(i in HomeScreenActivity.clickedFood!!.foodRating){
                c += i.reviewPoint
            }
            c /= HomeScreenActivity.clickedFood!!.foodRating.size
            HomeScreenActivity.clickedFood!!.foodRatingPoint = c.toString()
            adapter.notifyDataSetChanged()

            dialog.dismiss()
            CoroutineScope(Dispatchers.IO).launch {
                updateReviewInDB()
            }
        }
        dialog.show()
    }

    private fun updateReviewInDB(){
//        Log.i("my_msg","Sending updated review to db of ${HomeScreenActivity.clickedFood!!.foodUID}")
        val doc = FirebaseFirestore.getInstance().collection("all_food").document(HomeScreenActivity.clickedFood!!.foodUID)
        doc.update("foodRating",HomeScreenActivity.clickedFood!!.foodRating).addOnSuccessListener {
            doc.update("foodRatingPoint",HomeScreenActivity.clickedFood!!.foodRatingPoint).addOnSuccessListener {
                doc.get().addOnSuccessListener {
                    val food:FoodItemsData ?= it.toObject(FoodItemsData::class.java)
                    if(food != null){
                        val cartFood = CartFood()
                        cartFood.foodPic = food.foodPic
                        cartFood.foodProviderName = food.foodProviderName
                        cartFood.foodDesc1 = food.foodDesc1
                        cartFood.foodDesc2 = food.foodDesc2
                        cartFood.foodDesc3 = food.foodDesc3
                        cartFood.foodRate = food.foodRate
                        cartFood.foodDeliveryTime = food.foodDeliveryTime
                        cartFood.foodDistance = food.foodDistance
                        cartFood.foodDistance = food.foodDistance
                        cartFood.foodRating = food.foodRating
                        cartFood.foodRatingPoint = food.foodRatingPoint
                        cartFood.aboutFood = food.aboutFood
                        cartFood.foodUID = food.foodUID
                        cartFood.foodBuyers = food.foodBuyers
                        HomeScreenActivity.clickedFood = cartFood
                        //adapter.notifyDataSetChanged()
                        Food_detailed_Activity.reviewAdapter.notifyDataSetChanged()
                    }
                }.addOnFailureListener {
                    Log.i("my_msg","Error in updating food")
                }
            }.addOnFailureListener {
                Log.i("my_msg","Error in updating rating point of food")
            }
        }.addOnFailureListener {
            Log.i("my_msg","Error in updating review of food")
        }
        Log.i("my_msg","No of review after thread = ${HomeScreenActivity.clickedFood!!.foodRating.size}")
        Log.i("my_msg","Success in Sending updated review to db of ${HomeScreenActivity.clickedFood!!.foodUID}")

    }
}