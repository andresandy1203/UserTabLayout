package com.example.userpagetablayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.userpagetablayout.main.UserPageActivity

//Splash activity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        //Splash function
        Handler().postDelayed({
            val intent = Intent(this@MainActivity, UserPageActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}