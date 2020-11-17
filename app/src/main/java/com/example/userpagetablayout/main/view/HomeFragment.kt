package com.example.userpagetablayout.main.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.userpagetablayout.R
import com.example.userpagetablayout.SplashActivity
import com.example.userpagetablayout.databinding.FragmentHomeBinding
import com.example.userpagetablayout.main.YoutubeSearchActivity
import com.example.userpagetablayout.main.adapter.ViewPagerAdapter
import com.example.userpagetablayout.model.GalleryImage
import com.example.userpagetablayout.model.User
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class HomeFragment : Fragment() {

    var adapter: ViewPagerAdapter? = null
    var selectedPhotoUri: Uri? = null

    companion object {
        val TAG = "homefragment"
        var currentUser: User? = null
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )

        //Set up functions
        fetchCurrentUser(binding)
        setUpTabs(binding)

        //set up click  listener on add button depending on the current tab
        binding.buttonAddAction.setOnClickListener {
            val position = binding.viewPager.currentItem

            when (position) {
                0 -> {
                    val navController = activity?.findNavController(R.id.myNavHostFragment)
                    navController?.navigate(R.id.action_homeFragment_to_add_new_song)

                    Log.d(TAG, "we at music")
                }

                1 -> {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 0)
                    Log.d(TAG, "we at photos")
                }

                2 -> {
                    Log.d(TAG, "we at videos")
                    val intent1 = Intent(activity, YoutubeSearchActivity::class.java)
                    startActivity(intent1)
                }
            }

        }

        setHasOptionsMenu(true)

        return binding.root

    }


    //Set up menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        activity?.menuInflater?.inflate(R.menu.navmenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //Set up menu options actions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_settings -> {
                val navController = activity?.findNavController(R.id.myNavHostFragment)
                navController?.navigate(R.id.action_homeFragment_to_settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //Get the bitMap from the data of the selected image from the ACTION_PICK intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            uploadImagetoFirebase()
        }

    }



    //Setup the tabs
    private fun setUpTabs(binding: FragmentHomeBinding) {

        //Set up adapter
        adapter = ViewPagerAdapter(this)
        adapter!!.addFragment(MusicFragment(), "Music")
        adapter!!.addFragment(PhotosFragment(), "Photos")
        adapter!!.addFragment(VideosFragment(), "Videos")

        //Set up view pager and tabs
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.Tabs, binding.viewPager) { tab, position -> tab }.attach()

        binding.Tabs.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_library_music_24)
        binding.Tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_photo_library_24)
        binding.Tabs.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_video_library_24)

    }

    //Get the reference from firebase and upload the Uri of the selected image
    private fun uploadImagetoFirebase() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/albumImages/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveImageToFirebaseDatabase(it.toString())
                }
            }
    }

    //Save the image to the Firebase Database
    private fun saveImageToFirebaseDatabase(ImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("imageList/$uid").push()
        val image = GalleryImage(ref.key!!, ImageUrl)
        ref.setValue(image).addOnSuccessListener {
            Log.d(TAG, "Saved image")

        }
    }

    //Get current user info and fill the UI with the data collected
    private fun fetchCurrentUser(binding: FragmentHomeBinding) {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d(TAG, "Current user ${currentUser?.username}")
                binding.textViewUsername.text = "${currentUser?.username}"
                Glide.with(this@HomeFragment).load(currentUser?.profileImageUrl)
                    .into(binding.imageViewUserimage)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        Log.d(TAG, "Current user now ${currentUser?.username}")
    }


}