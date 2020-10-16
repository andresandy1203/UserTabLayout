package com.example.userpagetablayout.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.userpagetablayout.R
import com.example.userpagetablayout.models.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_new_song.*
import kotlinx.android.synthetic.main.activity_register.selected_photo_imageview
import java.util.*

class add_new_song : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_song)
        //DataBindingUtil.setContentView<ActivityUserPageBinding>



        button_registerSong.setOnClickListener {
            performAddSong()
        }
        button_addSongImage_register.setOnClickListener {
            val intent= Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }

    }
    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode== Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selected_photo_imageview.setImageBitmap(bitmap)

            button_addSongImage_register.alpha=0f

            // val bitmapDrawable = BitmapDrawable(bitmap)
            //selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }
    private fun uploadImagetoFirebase(){
        if(selectedPhotoUri==null)return
        val filename= UUID.randomUUID().toString()
        val ref= FirebaseStorage.getInstance().getReference("/albumImages/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveSongToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{

            }
    }


    private fun saveSongToFirebaseDatabase(ImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid
        val filename= UUID.randomUUID().toString()
        val ref= FirebaseDatabase.getInstance().getReference("songList/$uid").push()
        val songname = Edittext_songname_register.text.toString()
        val songArtist=Edittext_artistName_register.text.toString()
        val webUrl="https://music.youtube.com/search?q=$songArtist+$songname"

        val song= Song(ref.key!!,ImageUrl, songArtist, songname, webUrl)
        ref.setValue(song).addOnSuccessListener {
            Toast.makeText(this, "Song Saved", Toast.LENGTH_SHORT).show()
            Log.d("New song", "Saved song")
            val intent = Intent(this, UserPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)


        }
            .addOnFailureListener {

            }
    }
    private fun performAddSong(){
        if(selectedPhotoUri!=null){
            uploadImagetoFirebase()
        }
        else{
            Toast.makeText(this,"Please upload an image",Toast.LENGTH_SHORT).show()
            return
        }


    }

}