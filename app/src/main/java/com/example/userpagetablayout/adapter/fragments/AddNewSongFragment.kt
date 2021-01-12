package com.example.userpagetablayout.adapter.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.userpagetablayout.R
import com.example.userpagetablayout.view.UserPageActivity
import com.example.userpagetablayout.databinding.FragmentAddNewSongBinding
import com.example.userpagetablayout.model.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import java.util.*

class AddNewSongFragment : Fragment() {

    companion object {
        var binding: FragmentAddNewSongBinding? = null
    }

    var selectedPhotoUri: Uri? = null

    @Suppress("UNUSED_VARIABLE")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate<FragmentAddNewSongBinding>(
            inflater,
            R.layout.fragment_add_new_song,
            container,
            false
        )
        //Add click listener to register button to add a new song
        binding?.buttonRegisterSong?.setOnClickListener {
            performAddSong()
        }

        //Add click listener to allow to add the image of the song
        binding?.buttonAddSongImageRegister?.setOnClickListener {
            //initiate crop image activity
            CropImage.activity().setFixAspectRatio(true).setAspectRatio(150, 150)
                .start(requireContext(), this)

        }

        //Set key listeners
        binding?.EdittextSongnameRegister?.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }
        binding?.EdittextArtistNameRegister?.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }
        return binding?.root
    }


    //Get the bitMap from the data of the selected image from the ACTION_PICK intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Create bitmap and Uri from cropped image
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                val resultUri = result.uri
                selectedPhotoUri = resultUri
                val bitmap =
                    MediaStore.Images.Media.getBitmap(
                        activity?.contentResolver,
                        selectedPhotoUri
                    )
                binding?.selectedPhotoImageview?.setImageBitmap(bitmap)

                binding?.buttonAddSongImageRegister?.alpha = 0f

            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    //Handle key event to hide after input is done
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
            Toast.makeText(activity, "Please upload an image", Toast.LENGTH_SHORT).show()
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
        val ref = FirebaseDatabase.getInstance().getReference("songList/$uid").push()
        val songname = binding?.EdittextSongnameRegister?.text.toString()
        val songArtist = binding?.EdittextArtistNameRegister?.text.toString()
        val webUrl = "https://music.youtube.com/search?q=$songArtist+$songname"

        //Create a "Song" class variable from the input
        val song = Song(ref.key!!, ImageUrl, songArtist, songname, webUrl)

        //Update the database and go back to the User Page activity
        ref.setValue(song).addOnSuccessListener {
            Toast.makeText(activity, "Song Saved", Toast.LENGTH_SHORT).show()
            Log.d("New song", "Saved song")
            val intent = Intent(activity, UserPageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)


        }
            .addOnFailureListener {

            }
    }

}