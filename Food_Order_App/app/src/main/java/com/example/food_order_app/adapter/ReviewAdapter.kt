package com.example.food_order_app.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.food_order_app.IndividualActivity.AddReviewActivity
import com.example.food_order_app.R
import com.example.food_order_app.model.ReviewClass
import com.google.firebase.auth.FirebaseAuth
import java.util.Collections

class ReviewAdapter(var reviews:ArrayList<ReviewClass>,val str:String) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    var userId = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rating.rating = reviews[position].reviewPoint.toFloat()
        holder.reviewer.text = reviews[position].reviewerName
        holder.reviewText.text = reviews[position].reviewText

//        if(str == "2"){
//            if(userId == reviews[position].reviewUID){
//                AddReviewActivity.canAddReview = false
//            }
//        }


    }


    override fun getItemCount(): Int {
        return reviews.size
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        var rating:RatingBar = itemView.findViewById(R.id.ratingBarReview)
        var reviewer:TextView = itemView.findViewById(R.id.reviewerName)
        var reviewText:TextView = itemView.findViewById(R.id.reviewText)
        var removeReview:ImageView = itemView.findViewById(R.id.removeReview)

    }
}