package com.example.userpagetablayout.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

import com.example.userpagetablayout.R
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

        setContentView(R.layout.activity_login)
        gotoregister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        login_button.setOnClickListener {
            val email = username.text.toString()
            val password = password.text.toString()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d("Login", "Succesfully logged user ${it.result?.user?.uid}")
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