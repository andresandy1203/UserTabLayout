package com.example.userpagetablayout.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.userpagetablayout.R
import com.example.userpagetablayout.databinding.ActivityAddNewSongBinding
import com.example.userpagetablayout.models.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_new_song.*
import kotlinx.android.synthetic.main.activity_register.selected_photo_imageview
import java.util.*

class add_new_song : AppCompatActivity() {

    companion object {
        var binding: ActivityAddNewSongBinding? = null
    }
    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //DataBinding
        @Suppress("UNUSED_VARIABLE")
        binding = DataBindingUtil.setContentView<ActivityAddNewSongBinding>(
            this,
            R.layout.activity_add_new_song
        )

        //Add click listener to register button to add a new song
        binding?.buttonRegisterSong?.setOnClickListener {
            performAddSong()
        }

        //Add click listener to allow to add the image of the song
        binding?.buttonAddSongImageRegister?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        //Set key listeners
        binding?.EdittextSongnameRegister?.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }
        binding?.EdittextArtistNameRegister?.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }

    }

    //Get the bitMap from the data of the selected image from the ACTION_PICK intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            //Change the displayed image to the selected image's bitmap
            binding?.selectedPhotoImageview?.setImageBitmap(bitmap)

            binding?.buttonAddSongImageRegister?.alpha = 0f

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

    //Check if the User has selected an image
    private fun performAddSong() {
        if (selectedPhotoUri != null) {
            uploadImagetoFirebase()
        } else {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show()
            return
        }


    }

    //Get the reference from firebase and upload the Uri of the selected image
    private fun uploadImagetoFirebase() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/albumImages/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveSongToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {

            }
    }

    //Save the song to the Firebase Database, collecting the data from user's input
    private fun saveSongToFirebaseDatabase(ImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("songList/$uid").push()
        val songname = binding?.EdittextSongnameRegister?.text.toString()
        val songArtist = binding?.EdittextArtistNameRegister?.text.toString()
        val webUrl = "https://music.youtube.com/search?q=$songArtist+$songname"

        //Create a "Song" class variable from the input
        val song = Song(ref.key!!, ImageUrl, songArtist, songname, webUrl)

        //Update the database and go back to the User Page activity
        ref.setValue(song).addOnSuccessListener {
            Toast.makeText(this, "Song Saved", Toast.LENGTH_SHORT).show()
            Log.d("New song", "Saved song")
            val intent = Intent(this, UserPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)


        }
            .addOnFailureListener {

            }
    }

}