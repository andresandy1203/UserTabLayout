package com.example.userpagetablayout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.example.userpagetablayout.databinding.ActivityLoginBinding
import com.example.userpagetablayout.main.UserPageActivity
import com.example.userpagetablayout.model.User
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    //val userLogged= FirebaseAuth.getInstance().currentUser
    companion object {
        val TAG = "LoginActivity"
        var currentUser: User? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContentView(R.layout.activity_login)
        val binding = DataBindingUtil.setContentView<ActivityLoginBinding>(
            this,
            R.layout.activity_login
        )

        //Go back to register activity
        binding.gotoregister.setOnClickListener {
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }

        //Log the user in
        binding.loginButton.setOnClickListener {

            //Get user's input
            val email = binding.username.text.toString()
            val password = binding.password.text.toString()

            //Check the database
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d("Login", "Succesfully logged user ${it.result?.user?.uid}")

                    //Go to UserPage
                    val intent = Intent(this, UserPageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Login failed ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }


}