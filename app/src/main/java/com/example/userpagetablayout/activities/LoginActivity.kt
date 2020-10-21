package com.example.userpagetablayout.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.example.userpagetablayout.R
import com.example.userpagetablayout.databinding.ActivityEditSongDetailsBinding
import com.example.userpagetablayout.databinding.ActivityLoginBinding
import com.example.userpagetablayout.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

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
            val intent = Intent(this, RegisterActivity::class.java)
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
                    val intent = Intent(this, UserPage::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Login failed ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }


}