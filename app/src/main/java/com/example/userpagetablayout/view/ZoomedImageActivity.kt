package com.example.userpagetablayout.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.userpagetablayout.R
import com.example.userpagetablayout.databinding.ActivityZoomedImageBinding
import com.example.userpagetablayout.view.fragments.PhotosFragment
import com.example.userpagetablayout.model.GalleryImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ZoomedImageActivity : AppCompatActivity() {

    var galleryImage: GalleryImage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Data binding
        val binding = DataBindingUtil.setContentView<ActivityZoomedImageBinding>(
            this,
            R.layout.activity_zoomed_image
        )

        //Get Image data from previous activity
        galleryImage = intent.getParcelableExtra(PhotosFragment.IMAGE_LINK_KEY)

        //Load the image
        Glide.with(this).load(galleryImage?.imageUrl).into(binding.imageViewZoomed)

        //Set click listener
        binding.imageViewEraseButton.setOnClickListener {
            removeImage()
        }

    }

    //Remove the image from Firebase Database
    fun removeImage() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("imageList/$uid/${galleryImage?.id}")
        ref.removeValue()
        Toast.makeText(this, "ImageDeleted", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, UserPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }
}