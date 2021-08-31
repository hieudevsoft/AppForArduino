package com.devapp.appforarduino.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devapp.appforarduino.R
import com.devapp.appforarduino.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_home)

    }

    override fun onStart() {

        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }
}