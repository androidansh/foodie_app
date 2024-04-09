package com.example.food_order_app.home_screen

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.food_order_app.adapter.found_food_adapter
import com.example.food_order_app.databinding.ActivitySearchBinding
import com.example.food_order_app.model.FoodItemsData
import java.util.Locale
import java.util.regex.Pattern

class SearchActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySearchBinding
    private lateinit var adapter:found_food_adapter

    private var found_food_array:ArrayList<FoodItemsData> = ArrayList()

    companion object
    {
        lateinit var search_context:Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        search_context = applicationContext

        adapter = found_food_adapter(found_food_array)
        binding.foundFoodRecycler.adapter = adapter
        binding.foundFoodRecycler.layoutManager = LinearLayoutManager(this)



        binding.searchActivityBar.addTextChangedListener(textWatcher)
        binding.searchActivityBar.findFocus()

    }


    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // this function is called before text is edited
            //Log.i("my_msg","before = $s")
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // this function is called when text is edited
            //Log.i("my_msg",s.toString())
            if(found_food_array.size != 0){
                found_food_array.removeAll(found_food_array.toSet())
            }
            val pattern = Pattern.compile("^${s.toString().toLowerCase(Locale.ROOT)}")
            for(i in HomeScreenActivity.search_food_key_word.keys){
                for(j in HomeScreenActivity.search_food_key_word.get(i)!!){
                    val matcher = pattern.matcher(j)
                    if (matcher.find()) {
                        found_food_array.add(HomeScreenActivity.search_all_food_map[i]!!)
                        break
                    }
                }
            }

            adapter.notifyDataSetChanged()
            Log.i("my_msg","found products = ${found_food_array.size}")



        }

        override fun afterTextChanged(s: Editable) {
           // Log.i("my_msg"," After = $s")
        }
    }


}