package com.example.food_order_app.signUp

import android.content.Context
import android.content.SharedPreferences
import android.icu.text.Normalizer2
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.chaos.view.PinView
import com.example.food_order_app.R
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit


class OtpFrag : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var pinView: PinView
    private lateinit var chkOTP: Button
    private var otpID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_otp, container, false)
        auth = FirebaseAuth.getInstance()
        pinView = view.findViewById(R.id.pinView)
        chkOTP = view.findViewById(R.id.chkOTP)
        pinView.requestFocus()

        val options = this.activity?.let {
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(SignUpActivity.phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(it)
                .setCallbacks(callbacks)
                .build()
        }
        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        chkOTP.setOnClickListener {
            if (pinView.text.toString().length != 6) {
                Toast.makeText(context, "Invalid OTP", Toast.LENGTH_LONG).show()
            } else {
                val credential: PhoneAuthCredential =
                    PhoneAuthProvider.getCredential(otpID, pinView.text.toString())
                signInWithPhoneAuthCredential(credential)
            }
        }




        return view

    }

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {


        }

        override fun onVerificationFailed(e: FirebaseException) {


            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.i("my_msg","Invalid credentials  ${e.printStackTrace()}")

            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.i("my_msg","Daily quota finished  ${e.printStackTrace()}")

            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {

                // reCAPTCHA verification attempted with null Activity
                Log.i("my_msg","Captcha problem  ${e.printStackTrace()}")
            }


        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            otpID = verificationId

        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnSuccessListener {
            Toast.makeText(context, "User Created", Toast.LENGTH_LONG).show()
            val str = auth.uid
            if (str != null) {
                Log.i("my_msg",str)
                SignUpActivity.userUID = str
            }else{
                SignUpActivity.userUID = ""
            }

            val pref: SharedPreferences? =
                context?.getSharedPreferences("LogIn", Context.MODE_PRIVATE)
            val edit = pref?.edit()
            edit?.putBoolean("isLoggedIn", true)
            edit?.putString("UID", auth.uid)
            edit?.apply()

            val ft1 = activity?.supportFragmentManager?.beginTransaction()
            ft1?.replace(R.id.signUpFrame,UserNameFrag())
            ft1?.commitNow()

        }.addOnFailureListener {
            Log.i("my_msg", "Error in creating user")
            Log.i("my_msg", it.stackTraceToString())
            Toast.makeText(context, "Error :: " + it.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

}