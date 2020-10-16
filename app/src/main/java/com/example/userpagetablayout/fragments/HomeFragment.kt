package com.example.userpagetablayout.fragments

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
import com.example.userpagetablayout.activities.add_new_song
import com.example.userpagetablayout.databinding.ActivityZoomedImageBinding
import com.example.userpagetablayout.databinding.FragmentHomeBinding
import com.example.userpagetablayout.fragments.adapters.ViewPagerAdapter
import com.example.userpagetablayout.fragments.viewpagers.MusicFragment
import com.example.userpagetablayout.fragments.viewpagers.PhotosFragment
import com.example.userpagetablayout.fragments.viewpagers.VideosFragment
import com.example.userpagetablayout.models.GalleryImage
import com.example.userpagetablayout.models.User
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class HomeFragment : Fragment() {

    val userLogged = FirebaseAuth.getInstance().currentUser
//    val activity:UserPage= getActivity() as UserPage

    var DeleteFlag:Int?=0
    companion object {
        val TAG = "homefragment"
        var currentUser: User? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        //Log.d(TAG,"Now user ${activity.loggedInUser}")
        fetchCurrentUser(binding)
        setUpTabs(binding)



        binding.buttonAddImage.setOnClickListener {
           val position= binding.viewPager.currentItem

           // Log.d(TAG,"where we at $position")
            when(position){
                0->{
                    val intent=Intent(activity, add_new_song::class.java)
                    startActivity(intent)

                    Log.d(TAG,"we at music")
                }

                1->{
                    val intent= Intent(Intent.ACTION_PICK)
                    intent.type="image/*"
                    startActivityForResult(intent,0)
                    Log.d(TAG,"we at photos")}

                2->Log.d(TAG,"we at videos")

            }

//

        }

        setHasOptionsMenu(true)

        return binding.root

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu_settings->{
                val navController = activity?.findNavController(R.id.myNavHostFragment)
                navController?.navigate(R.id.action_homeFragment_to_settings)
                //this.findNavController().navigate(R.id.action_homeFragment_to_settings)

            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        activity?.menuInflater?.inflate(R.menu.navmenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode== Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            uploadImagetoFirebase()

            // val bitmapDrawable = BitmapDrawable(bitmap)
            //selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }

    }
    var adapter:ViewPagerAdapter?=null
    var selectedPhotoUri: Uri?=null

    private fun setUpTabs(binding: FragmentHomeBinding) {

        //val fragments = listOf(MusicFragment(),PhotosFragment(),VideosFragment())
        adapter = ViewPagerAdapter(this)
        adapter!!.addFragment(MusicFragment(), "Music")
        adapter!!.addFragment(PhotosFragment(), "Photos")
        adapter!!.addFragment(VideosFragment(), "Videos")

        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled=false
        TabLayoutMediator(binding.Tabs, binding.viewPager) { tab, position -> tab }.attach()


        binding.Tabs.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_library_music_24)
        binding.Tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_photo_library_24)
        binding.Tabs.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_video_library_24)

    }

        private fun uploadImagetoFirebase(){
        if(selectedPhotoUri==null)return
        val filename= UUID.randomUUID().toString()
        val ref= FirebaseStorage.getInstance().getReference("/albumImages/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveImageToFirebaseDatabase(it.toString())
                }
            }
    }


    private fun saveImageToFirebaseDatabase(ImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid
        val filename= UUID.randomUUID().toString()
       val ref=FirebaseDatabase.getInstance().getReference("imageList/$uid").push()
        val image= GalleryImage(ref.key!!, ImageUrl)
        ref.setValue(image).addOnSuccessListener {
            Log.d(TAG, "Saved image")

        }
       }

    private fun fetchCurrentUser(binding: FragmentHomeBinding) {


        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d(TAG, "Current user ${currentUser?.username}")
                binding.textViewUsername.text = "${currentUser?.username}"
                Glide.with(this@HomeFragment).load(currentUser?.profileImageUrl).into(binding.imageViewUserimage)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        Log.d(TAG, "Current user now ${currentUser?.username}")
    }


}