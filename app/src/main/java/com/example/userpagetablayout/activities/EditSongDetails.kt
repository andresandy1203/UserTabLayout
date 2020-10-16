package com.example.userpagetablayout.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.userpagetablayout.R
import com.example.userpagetablayout.databinding.ActivityEditSongDetailsBinding
import com.example.userpagetablayout.fragments.viewpagers.MusicFragment
import com.example.userpagetablayout.models.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class EditSongDetails() : AppCompatActivity() {
    companion object{
        var binding:ActivityEditSongDetailsBinding?=null
        var selectedPhotoUri: Uri? = null
        var newImage: Boolean = false
        var newImageUrl: String? = null
        var song: Song? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title="Edit Song"
        @Suppress("UNUSED_VARIABLE")
        binding = DataBindingUtil.setContentView<ActivityEditSongDetailsBinding>(
            this,
            R.layout.activity_edit_song_details
        )
        song = intent.getParcelableExtra(MusicFragment.SONG_LINK_KEY)
        setSongData()
        binding?.buttonEditSong?.setOnClickListener {
            performSave()
        }
        binding?.selectedPhotoImageview?.setOnClickListener {
            newpic()
        }
        binding?.buttonAddSongImageEdit?.setOnClickListener {
            newpic()
        }



    }

    private fun setSongData(){

        binding?.EdittextSongnameEdit?.hint=song?.songName
        binding?.EdittextArtistNameEdit?.hint=song?.songArtist
        binding?.selectedPhotoImageview?.let{
            Glide.with(this).load(song?.albumUrl).into(it)
        }



    }
    private fun performSave(){
        val EditSongName = binding?.EdittextSongnameEdit?.text.toString()
        val EditArtistName = binding?.EdittextArtistNameEdit?.text.toString()
        val uid = FirebaseAuth.getInstance().uid

        var songName=song?.songName
        var songArtist=song?.songArtist

        if(EditSongName.isNotEmpty()){
            val ref= FirebaseDatabase.getInstance().getReference("songList/$uid/${song?.id}/songName")
            ref.setValue(EditSongName)
            songName=EditSongName
        }
        if(EditArtistName.isNotEmpty()){
            val ref= FirebaseDatabase.getInstance().getReference("songList/$uid/${song?.id}/songArtist")
            ref.setValue(EditArtistName)
            songArtist=EditArtistName
        }
        if(newImage ==true){
            val ref= FirebaseDatabase.getInstance().getReference("songList/$uid/${song?.id}/albumUrl")
            ref.setValue(newImageUrl)
        }

        val ref= FirebaseDatabase.getInstance().getReference("songList/$uid/${song?.id}/webUrl")
        ref.setValue("https://music.youtube.com/search?q=$songArtist+$songName")

        val intent = Intent(this, UserPage::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        Toast.makeText(this, "Changes were saved", Toast.LENGTH_SHORT).show()
        startActivity(intent)

    }

    private fun newpic(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

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

    override fun onCreateOptionsMenu(menu: Menu?) : Boolean {

        menuInflater.inflate(R.menu.go_back_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.goHome_settings -> {
                val intent = Intent(this, UserPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //this.findNavController().navigate(R.id.action_homeFragment_to_settings)

            }
            R.id.sign_out -> {
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            newImage = true
            binding?.selectedPhotoImageview?.setImageBitmap(bitmap)


            uploadImageToFireBase()


        }
    }
}