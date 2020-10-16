package com.example.userpagetablayout.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.example.userpagetablayout.R
import com.example.userpagetablayout.databinding.ActivityUserPageBinding
import com.example.userpagetablayout.databinding.FragmentHomeBinding
import com.example.userpagetablayout.fragments.HomeFragment
import com.example.userpagetablayout.fragments.adapters.ViewPagerAdapter
import com.example.userpagetablayout.fragments.viewpagers.MusicFragment
import com.example.userpagetablayout.fragments.viewpagers.PhotosFragment
import com.example.userpagetablayout.fragments.viewpagers.VideosFragment
import com.example.userpagetablayout.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*


class UserPage : AppCompatActivity() {


    companion object {
        var currentUser: User?=null
        val TAG = "UserPage"
        var binding:ActivityUserPageBinding?=null
        var Bartitle="User Page"
    }
    var navController:NavController?=null

    //val loggedInUser=User(uid.toString(),uid?.displayName.toString(),uid?.photoUrl.toString())
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        //supportActionBar?.title="USER PAGE"

        //val navController=this.findNavController(R.id.myNavHostFragment)
        //NavigationUI.setupActionBarWithNavController(this,navController)
        super.onCreate(savedInstanceState)
        fetchCurrentUser()
        verifyUserIsLoggedIn()

        @Suppress("UNUSED_VARIABLE")
         binding = DataBindingUtil.setContentView<ActivityUserPageBinding>(
            this,
            R.layout.activity_user_page
        )

        drawerLayout = binding!!.drawerLayout
         navController = this.findNavController(R.id.myNavHostFragment)
        //NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        supportActionBar?.title= Bartitle
        NavigationUI.setupWithNavController(binding!!.navView, navController!!)


        //fillUI()

    }



    override fun onSupportNavigateUp(): Boolean {
         val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController!!, drawerLayout)
    }

    private fun verifyUserIsLoggedIn() {
        val uid = Firebase.auth.currentUser

        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

//    fun setActionBarTitle(title:String){
//        supportActionBar?.title="$title"
//    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currentUser=p0.getValue(User::class.java)
                Log.d("LatesMessages", "Current user ${currentUser?.username}")
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item?.itemId){
//            R.id.menu_settings->{
//                val navController = this.findNavController(R.id.myNavHostFragment)
//                navController.navigate(R.id.action_homeFragment_to_settings)
//                //this.findNavController().navigate(R.id.action_homeFragment_to_settings)
//
//            }
//
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.navmenu,menu)
//        return super.onCreateOptionsMenu(menu)
//    }

}