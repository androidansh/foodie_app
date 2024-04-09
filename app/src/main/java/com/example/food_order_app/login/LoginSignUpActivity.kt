package com.example.food_order_app.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.food_order_app.R
import com.example.food_order_app.signUp.SignUpActivity

class LoginSignUpActivity : AppCompatActivity() {
    lateinit var signUp:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)

        signUp = findViewById(R.id.signUpText)
        signUp.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }
    }
}