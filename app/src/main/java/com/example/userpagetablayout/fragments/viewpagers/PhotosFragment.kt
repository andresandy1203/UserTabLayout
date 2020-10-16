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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    @SuppressLint()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentPhotosBinding>(
            inflater,
            R.layout.fragment_photos,
            container,
            false
        )

        listenForImages()
        binding.RecyclerviewGalleryGridimages.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            val imageItem = item as GalleryItem
            val intent = Intent(activity, ZoomedImage::class.java)
            intent.putExtra(IMAGE_LINK_KEY, imageItem?.imageItem)
            startActivity(intent)
        }
        val manager = GridLayoutManager(activity, 3)
        binding.RecyclerviewGalleryGridimages.layoutManager = manager



        return binding.root

    }

    var selectedPhotoUri:Uri?=null

//    private fun uploadImagetoFirebase(){
//        if(selectedPhotoUri==null)return
//        val filename=UUID.randomUUID().toString()
//        val ref=FirebaseStorage.getInstance().getReference("/albumImages/$filename")
//        ref.putFile(selectedPhotoUri!!)
//            .addOnSuccessListener {
//                ref.downloadUrl.addOnSuccessListener {
//                    saveImageToFirebaseDatabase(it.toString())
//                }
//            }
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(requestCode==0 && resultCode== Activity.RESULT_OK && data != null){
//            selectedPhotoUri = data.data
//            uploadImagetoFirebase()
//
//            // val bitmapDrawable = BitmapDrawable(bitmap)
//            //selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
//        }
//    }

//    private fun saveImageToFirebaseDatabase(ImageUrl: String){
////        val uid = FirebaseAuth.getInstance().uid
////       val ref=FirebaseDatabase.getInstance().getReference("imagesList/$uid")
////        val image=GalleryImage(ImageUrl)
////        ref.setValue(image).addOnSuccessListener {
////            Log.d(TAG, "Saved image")
////
////        }
////    }

    val imageMap=HashMap<String, GalleryImage>()

    private fun refreshRecyclerImages(){
        adapter.clear()
        imageMap.values.forEach {
            adapter.add(GalleryItem(it))
        }
    }

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

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })

//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                snapshot.children.forEach {
//                    val image = it.getValue(GalleryImage::class.java)
//
//                    //Log.d(MusicFragment.TAG,"song album:${song?.imageUrl}")
//                    if (image != null) {
//                        adapter.add(GalleryItem(image))
//                    }
//                }
//            }
//        })
    }


}






