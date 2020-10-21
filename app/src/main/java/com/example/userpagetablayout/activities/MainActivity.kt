package com.example.userpagetablayout.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.userpagetablayout.R

//Splash activity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        //Splash function
        Handler().postDelayed({
            val intent = Intent(this@MainActivity, UserPage::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}