package com.example.food_order_app.signUp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.food_order_app.R
import com.example.food_order_app.home_screen.HomeScreenActivity
import com.example.food_order_app.model.FoodieUser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Runnable
import java.util.Locale


class UserNameFrag : Fragment() {

    private lateinit var fName:EditText
    private lateinit var lName:EditText
    private lateinit var addName:Button
    private lateinit var foodUser:FoodieUser
    private lateinit var client:FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_name, container, false)

        fName = view.findViewById(R.id.signUpFName)
        lName = view.findViewById(R.id.signUpLName)
        addName = view.findViewById(R.id.AddName)
        client = LocationServices.getFusedLocationProviderClient(SignUpActivity.myContext)


        addName.setOnClickListener {
            val f_name:String = fName.text.toString().trim()
            val l_name:String = lName.text.toString().trim()
            if(f_name.isNotEmpty()){
                foodUser = FoodieUser()
                foodUser.userUid = SignUpActivity.userUID
//                foodUser.userUid = "Zp3ra2u0H2Qze9X43ceolizCI103"
                foodUser.userName = "$f_name $l_name"
                getLocationOfUser()
                Thread(running()).start()
                startActivity(Intent(SignUpActivity.myContext, HomeScreenActivity::class.java))
            }
        }


        return view
    }

    private fun getLocationOfUser() {

        if (ContextCompat.checkSelfPermission(SignUpActivity.myContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            client.lastLocation.addOnSuccessListener {

                val geocoder = Geocoder(SignUpActivity.myContext, Locale.getDefault())
                val address: MutableList<Address>? = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                if(address!= null){
                    foodUser.address = address[0].getAddressLine(0)
                    Log.i("my_msg","Location1 = ${address[0].getAddressLine(0)}")
                }


            }.addOnFailureListener{
                Log.i("my_msg",it.localizedMessage.toString())

            }
        }
        else{
            val permissionStr = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            activity?.let { ActivityCompat.requestPermissions(it,permissionStr,100) }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100){

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (ActivityCompat.checkSelfPermission(SignUpActivity.myContext,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(SignUpActivity.myContext,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    client.lastLocation.addOnSuccessListener {

                        val geocoder = Geocoder(SignUpActivity.myContext, Locale.getDefault())
                        val address: MutableList<Address>? = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        if(address!= null){
                            Log.i("my_msg","Address = ${address.toString()}")
                            foodUser.address = address[0].getAddressLine(0)
                        }

                    }.addOnFailureListener{
                        Log.i("my_msg",it.localizedMessage.toString())

                    }
                }
            }else {

                Toast.makeText(SignUpActivity.myContext,"Please provide the required permission",Toast.LENGTH_SHORT).show();
            }
        }
    }



    private fun saveData2DB(){
        foodUser.phone = SignUpActivity.phoneNum
        Log.i("my_msg","Thread is started")
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val docRef:DocumentReference = db.collection("FoodieUser").document(SignUpActivity.userUID)
        docRef.set(foodUser).addOnSuccessListener {
            Log.i("my_msg","Success in adding data")
            Toast.makeText(context,"Success in adding data to db",Toast.LENGTH_LONG).show()

        }.addOnFailureListener{

            Toast.makeText(context,"Error in adding data to db",Toast.LENGTH_LONG).show()
            Log.i("my_msg",it.message.toString())
        }
    }

    inner class running:Runnable{
        override fun run() {
            saveData2DB()
        }

    }

}