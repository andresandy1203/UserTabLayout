package com.example.userpagetablayout.fragments.viewpagers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.userpagetablayout.R
import com.example.userpagetablayout.activities.ZoomedImage
import com.example.userpagetablayout.databinding.FragmentPhotosBinding
import com.example.userpagetablayout.fragments.viewPagerFragments.GalleryItem
import com.example.userpagetablayout.fragments.viewPagerFragments.SongRow
import com.example.userpagetablayout.models.GalleryImage
import com.example.userpagetablayout.models.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_photos.*
import java.util.*

class PhotosFragment : Fragment() {
    companion object {
        val TAG = "photosfragment"
        val IMAGE_LINK_KEY = "IMAGE_LINK"
    }

    //Set up adapter
    val adapter = GroupAdapter<GroupieViewHolder>()
    //HashMap for the recycler view of images
    val imageMap=HashMap<String, GalleryImage>()

    @SuppressLint()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        //Data Binding
        val binding = DataBindingUtil.inflate<FragmentPhotosBinding>(
            inflater,
            R.layout.fragment_photos,
            container,
            false
        )

        //Get the images data
        listenForImages()

        //set up recycler view's adapter
        binding.RecyclerviewGalleryGridimages.adapter = adapter

        //Click listener for the recycler view items
        adapter.setOnItemClickListener { item, view ->
            //Sending the data of the selected image to the new activity
            val imageItem = item as GalleryItem
            val intent = Intent(activity, ZoomedImage::class.java)
            intent.putExtra(IMAGE_LINK_KEY, imageItem?.imageItem)
            startActivity(intent)
        }

        //Manage the grid layout
        val manager = GridLayoutManager(activity, 3)
        binding.RecyclerviewGalleryGridimages.layoutManager = manager

        return binding.root

    }

    //Refresh when the list of images change
    private fun refreshRecyclerImages(){
        adapter.clear()
        imageMap.values.forEach {
            adapter.add(GalleryItem(it))
        }
    }

    //Load images from Firebase database
    private fun listenForImages() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/imageList/$uid")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val Image=snapshot.getValue(GalleryImage::class.java)?:return
                imageMap[snapshot.key!!]=Image
                refreshRecyclerImages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val Image=snapshot.getValue(GalleryImage::class.java)?:return
                imageMap[snapshot.key!!]=Image
                refreshRecyclerImages()
            }

            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }


}






