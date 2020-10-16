package com.example.userpagetablayout.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.userpagetablayout.R
import com.example.userpagetablayout.databinding.FragmentEditSongBinding
import com.example.userpagetablayout.fragments.Settings
import com.example.userpagetablayout.models.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class EditSong(val songItem:Song) : Fragment() {
    // TODO: Rename and change types of parameters


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    companion object {
        var binding: FragmentEditSongBinding? = null
        var selectedPhotoUri: Uri? = null
        var newImage: Boolean = false
        var newImageUrl: String? = null

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentEditSongBinding>(
            inflater,
            R.layout.fragment_edit_song,
            container,
            false
        )

        setSongData()
        setHasOptionsMenu(true)
        binding?.buttonEditSong?.setOnClickListener {
            performSave()
        }
        binding?.selectedPhotoImageview?.setOnClickListener {
            newpic()
        }
        binding?.buttonAddSongImageEdit?.setOnClickListener {
            newpic()
        }

        return binding?.root
    }


    private fun setSongData(){

        binding?.EdittextSongnameEdit?.hint=songItem.songName
        binding?.EdittextArtistNameEdit?.hint=songItem.songArtist
        binding?.selectedPhotoImageview?.let{
            Glide.with(this@EditSong).load(songItem.albumUrl).into(it)
        }



    }
    private fun performSave(){
        val EditSongName = binding?.EdittextSongnameEdit?.text.toString()
        val EditArtistName = binding?.EdittextArtistNameEdit?.text.toString()
        val uid = FirebaseAuth.getInstance().uid

        var songName=songItem?.songName
        var songArtist=songItem?.songArtist

        if(EditSongName.isNotEmpty()){
            val ref=FirebaseDatabase.getInstance().getReference("songList/$uid/${songItem.id}/songName")
            ref.setValue(EditSongName)
            songName=EditSongName
        }
        if(EditArtistName.isNotEmpty()){
            val ref=FirebaseDatabase.getInstance().getReference("songList/$uid/${songItem.id}/songArtist")
            ref.setValue(EditArtistName)
            songArtist=EditArtistName
        }
        if(newImage==true){
            val ref=FirebaseDatabase.getInstance().getReference("songList/$uid/${songItem.id}/albumUrl")
            ref.setValue(newImageUrl)
        }

        val ref=FirebaseDatabase.getInstance().getReference("songList/$uid/${songItem.id}/webUrl")
        ref.setValue("https://music.youtube.com/search?q=$songArtist+$songName")

        val intent = Intent(activity, UserPage::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        Toast.makeText(activity, "Changes were saved", Toast.LENGTH_SHORT).show()
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
                    newImage = true
                    newImageUrl = it.toString()
                }
            }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        activity?.menuInflater?.inflate(R.menu.go_back_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.goHome_settings -> {
               // val navController = activity?.findNavController(R.id.myNavHostFragment)
                //navController?.navigate(R.id.action_editSong_to_homeFragment)
                //this.findNavController().navigate(R.id.action_homeFragment_to_settings)

            }
            R.id.sign_out -> {
                val intent = Intent(activity, RegisterActivity::class.java)
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
            val bitmap =
                MediaStore.Images.Media.getBitmap(activity?.contentResolver,
                    selectedPhotoUri
                )
            binding?.selectedPhotoImageview?.setImageBitmap(bitmap)

            uploadImageToFireBase()

        }
    }

}
