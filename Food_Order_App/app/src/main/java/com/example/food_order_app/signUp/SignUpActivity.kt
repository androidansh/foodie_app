package com.example.food_order_app.signUp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentTransaction
import com.example.food_order_app.R

class SignUpActivity : AppCompatActivity() {

    companion object{
        var phoneNum:String = ""
        var userUID:String = ""
        lateinit var myContext:Context
    }
    private lateinit var ft:FragmentTransaction


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        myContext = applicationContext

        ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.signUpFrame,PhoneFrag())
        ft.commitNow()

    }
}