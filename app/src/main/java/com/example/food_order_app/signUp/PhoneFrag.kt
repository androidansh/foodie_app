package com.example.food_order_app.signUp

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import com.example.food_order_app.R
import com.google.android.material.snackbar.Snackbar

class PhoneFrag : Fragment() {

    private lateinit var phone:EditText
    private lateinit var proceed:Button



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_phone, container, false)

        phone = view.findViewById(R.id.signupPhone)
        proceed = view.findViewById(R.id.signupProceed)

        proceed.setOnClickListener {
            if(phone.text.toString().trim().length !=10){

                Snackbar.make(it,"Invalid Phone number",Snackbar.LENGTH_SHORT).show()
            }
            else{
                Log.i("my_msg","My Msg")
                val snackBar:Snackbar = Snackbar.make(view, "Okk",Snackbar.LENGTH_SHORT)
                    .setAction("Action",null)
                snackBar.view.setBackgroundColor(resources.getColor(R.color.bgColor))
                snackBar.show()
                SignUpActivity.phoneNum = "+91"+phone.text.toString().trim()
                println(SignUpActivity.phoneNum)

                val ft1 = activity?.supportFragmentManager?.beginTransaction()
                ft1?.replace(R.id.signUpFrame,OtpFrag())
                ft1?.commitNow()
            }
        }
        return view
    }

}