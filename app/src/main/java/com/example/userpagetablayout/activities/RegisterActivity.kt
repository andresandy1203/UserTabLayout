package com.example.userpagetablayout.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.userpagetablayout.R
import com.example.userpagetablayout.databinding.ActivityEditSongDetailsBinding
import com.example.userpagetablayout.databinding.ActivityRegisterBinding
import com.example.userpagetablayout.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    companion object {
        var binding: ActivityRegisterBinding? = null
        var selectedPhotoUri: Uri? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        @Suppress("UNUSED_VARIABLE")
        //Data binding
        binding = DataBindingUtil.setContentView<ActivityRegisterBinding>(
            this,
            R.layout.activity_register
        )

        //Set Click Listeners
        binding?.buttonRegisterUser?.setOnClickListener {
            performRegister()
        }
        binding?.gotologinscreen?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding?.buttonAddImageRegister?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        //Set key listeners
        binding?.EdittextEmailRegister?.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }
        binding?.passwordRegister?.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }
        binding?.EdittextUsernameRegister?.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }
    }

    //Get the bitMap from the data of the selected image from the ACTION_PICK intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            binding?.selectedPhotoImageview?.setImageBitmap(bitmap)

            binding?.buttonAddImageRegister?.alpha = 0f
        }
    }

    //Handle key event to hide after input is done
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }

    //Regisster the user into Firebase
    private fun performRegister() {
        //Get user input
        val email = binding?.EdittextEmailRegister?.text.toString()
        val password = binding?.passwordRegister?.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email and password", Toast.LENGTH_SHORT)
                .show()
            return
        }

        //Create the user
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create user:${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }


    }

    //Get the reference from firebase and upload the Uri of the selected image
    private fun uploadImageToFirebaseStorage() {

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }.addOnFailureListener {

            }

    }

    //Save the User into the firebase database
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        //Create the variable from User class
        val user = User(uid, binding?.EdittextUsernameRegister?.text.toString(), profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                //Initiate the User Page Activity
                val intent = Intent(this, UserPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {

            }

    }
}