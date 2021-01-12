package com.example.userpagetablayout.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.userpagetablayout.R
import com.example.userpagetablayout.databinding.ActivityEditSongDetailsBinding
import com.example.userpagetablayout.adapter.fragments.MusicFragment
import com.example.userpagetablayout.model.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*

class EditSongDetailsActivity() : AppCompatActivity() {
    companion object {
        var binding: ActivityEditSongDetailsBinding? = null
        var selectedPhotoUri: Uri? = null
        var newImage: Boolean = false
        var newImageUrl: String? = null
        var song: Song? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Edit Song"

        @Suppress("UNUSED_VARIABLE")
        //Data binding
        binding = DataBindingUtil.setContentView<ActivityEditSongDetailsBinding>(
            this,
            R.layout.activity_edit_song_details
        )

        //Get the data of the previously selected song to be edited from the previous activity
        song = intent.getParcelableExtra(MusicFragment.SONG_LINK_KEY)

        //Fill the UI with the data of the song
        setSongData()

        //Add click listeners
        binding?.buttonEditSong?.setOnClickListener {
            performSave()
        }
        binding?.selectedPhotoImageview?.setOnClickListener {
            newpic()
        }
        binding?.buttonAddSongImageEdit?.setOnClickListener {
            newpic()
        }

        //Set key listeners
        binding?.EdittextSongnameEdit?.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }
        binding?.EdittextArtistNameEdit?.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }

    }

    //Create Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.go_back_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Initialize menu's actions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.goHome_settings -> {
                val intent = Intent(this, UserPageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //this.findNavController().navigate(R.id.action_homeFragment_to_settings)

            }
            R.id.sign_out -> {
                val intent = Intent(this, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    //Get the bitMap from the data of the selected image from the ACTION_PICK intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Create bitmap and Uri from cropped image
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                val resultUri = result.uri
                selectedPhotoUri = resultUri
                val bitmap =
                    MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        selectedPhotoUri
                    )
                //Variable to helpcheck if the user selected a new image
                newImage = true
                binding?.selectedPhotoImageview?.setImageBitmap(bitmap)

                uploadImageToFireBase()

            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
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

    //Fill the UI with the song's current data
    private fun setSongData() {

        binding?.EdittextSongnameEdit?.hint = song?.songName
        binding?.EdittextArtistNameEdit?.hint = song?.songArtist
        binding?.selectedPhotoImageview?.let {
            Glide.with(this).load(song?.albumUrl).into(it)
        }


    }

    //Create the intent for selection of image when either the image or button is clicked
    private fun newpic() {
        //Initiate crop image activity
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setFixAspectRatio(true)
            .setAspectRatio(150, 150).start(this)

    }

    //Get the reference from firebase and upload the Uri of the selected image
    private fun uploadImageToFireBase() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/albumImages/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    newImageUrl = it.toString()
                }
            }

    }

    //Save the changes made
    private fun performSave() {

        //Get Data from UI
        val EditSongName = binding?.EdittextSongnameEdit?.text.toString()
        val EditArtistName = binding?.EdittextArtistNameEdit?.text.toString()

        //GEt current user's reference
        val uid = FirebaseAuth.getInstance().uid

        //Set the data assuming the name and artist have not been changed
        var songName = song?.songName
        var songArtist = song?.songArtist

        //Update song name if the user entered a new song name
        if (EditSongName.isNotEmpty()) {
            val ref =
                FirebaseDatabase.getInstance().getReference("songList/$uid/${song?.id}/songName")
            ref.setValue(EditSongName)
            songName = EditSongName
        }

        //Update song artist if the user entered a new song artist
        if (EditArtistName.isNotEmpty()) {
            val ref =
                FirebaseDatabase.getInstance().getReference("songList/$uid/${song?.id}/songArtist")
            ref.setValue(EditArtistName)
            songArtist = EditArtistName
        }

        //Update the image if the user selected any image
        if (newImage == true) {
            val ref =
                FirebaseDatabase.getInstance().getReference("songList/$uid/${song?.id}/albumUrl")
            ref.setValue(newImageUrl)
        }

        //Update the webUrl property of the Song using the variables "songName" & "SongArtist"
        // which are initialized as the previous song data, and are only updated if the user made any new input
        val ref = FirebaseDatabase.getInstance().getReference("songList/$uid/${song?.id}/webUrl")
        ref.setValue("https://music.youtube.com/search?q=$songArtist+$songName")

        //Go back to the UserPage activity
        val intent = Intent(this, UserPageActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        Toast.makeText(this, "Changes were saved", Toast.LENGTH_SHORT).show()
        startActivity(intent)

    }


}