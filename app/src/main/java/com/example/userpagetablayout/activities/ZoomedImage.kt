package com.example.userpagetablayout.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.userpagetablayout.R
import com.example.userpagetablayout.fragments.HomeFragment
import com.example.userpagetablayout.fragments.viewpagers.PhotosFragment
import com.example.userpagetablayout.models.GalleryImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_zoomed_image.*
import kotlinx.android.synthetic.main.gallery_item.view.*
import kotlinx.android.synthetic.main.song_row.view.*

class ZoomedImage : AppCompatActivity() {

    var galleryImage: GalleryImage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoomed_image)
        galleryImage = intent.getParcelableExtra(PhotosFragment.IMAGE_LINK_KEY)

        Glide.with(this).load(galleryImage?.imageUrl).into(imageViewZoomed)

        imageView_erase_button.setOnClickListener {
            removeImage()
        }
        //Picasso.get().load(galleryImage?.imageUrl).into(imageViewZoomed)
    }

    fun removeImage(){
        val uid = FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("imageList/$uid/${galleryImage?.id}")
        ref.removeValue()
        Toast.makeText(this, "ImageDeleted", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, UserPage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }
}