package com.example.userpagetablayout.fragments.viewpagers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.userpagetablayout.R
import com.example.userpagetablayout.activities.EditSongDetails
import com.example.userpagetablayout.activities.MusicPlay
import com.example.userpagetablayout.activities.UserPage
import com.example.userpagetablayout.databinding.FragmentMusicBinding
import com.example.userpagetablayout.fragments.viewPagerFragments.SongRow
import com.example.userpagetablayout.helper.SwipeHelper

import com.example.userpagetablayout.models.Song
import com.example.userpagetablayout.models.Video
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_music.*

class MusicFragment : Fragment() {
    companion object {
        val TAG = "musicfragment"
        val SONG_LINK_KEY = "SONG_LINK"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    val adapter = GroupAdapter<GroupieViewHolder>()


    @SuppressLint()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentMusicBinding>(
            inflater,
            R.layout.fragment_music,
            container,
            false
        )



        listenForSongs()
        binding.recyclerviewSongs.adapter = adapter
        binding.recyclerviewSongs.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter.setOnItemClickListener { item, view ->
            val songItem = item as SongRow
            val intent = Intent(activity, MusicPlay::class.java)
            intent.putExtra(SONG_LINK_KEY, songItem?.songItem)
            startActivity(intent)
        }

        val itemTouchHelper = ItemTouchHelper(object:SwipeHelper(binding.recyclerviewSongs){
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                var buttons=listOf<UnderlayButton>()
                val deleteButton = deleteButton(position)
                val editButton = editButton(position)
                buttons= listOf(deleteButton,editButton)
                return buttons
            }

        })

        itemTouchHelper.attachToRecyclerView(binding.recyclerviewSongs)


//        button_add.setOnClickListener {
//            val intent=Intent(Intent.ACTION_PICK)
//            intent.type="image/*"
//            startActivityForResult(intent,0)
//        }
        // binding.recyclerviewSongs.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        return binding.root


    }

    private fun deleteButton(position: Int):SwipeHelper.UnderlayButton{
        return SwipeHelper.UnderlayButton(requireActivity(), "Delete", 14.0f, android.R.color.holo_red_light,
            object:SwipeHelper.UnderlayButtonClickListener{
                override fun onClick() {
                   val item = adapter.getItem(position)
                    val songItem=item as SongRow
                    removeSong(songItem.songItem)
                    //Toast.makeText(activity,"Delete Touched",Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun editButton(position: Int):SwipeHelper.UnderlayButton{
        return SwipeHelper.UnderlayButton(requireActivity(), "Edit", 14.0f, android.R.color.holo_blue_light,
            object:SwipeHelper.UnderlayButtonClickListener{
                override fun onClick() {
                    val item = adapter.getItem(position)
                    val songItem=item as SongRow
                    val intent = Intent(activity, EditSongDetails::class.java)
                    intent.putExtra(SONG_LINK_KEY, songItem?.songItem)
                    startActivity(intent)
                    //val navController = activity?.findNavController(R.id.myNavHostFragment)
                    //navController?.navigate(R.id.action_homeFragment_to_editSong.)
                    //Toast.makeText(activity,"Edit Touched",Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun removeSong(song: Song){
        val uid = FirebaseAuth.getInstance().uid
        val ref= FirebaseDatabase.getInstance().getReference("songList/$uid/${song?.id}")
        ref.removeValue()
        Toast.makeText(activity, "Song Deleted", Toast.LENGTH_SHORT).show()
        SongsMap.values.remove(song)
        refreshRecyclerSongs()
    }


    val SongsMap=HashMap<String, Song>()

    private fun refreshRecyclerSongs(){

        adapter.clear()
        SongsMap.values.forEach {
            adapter.add(SongRow(it))
        }
    }

    var selectedPhotoUri: Uri?=null


    private fun uploadImagetoDataBase(){

    }

    private fun listenForSongs() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/songList/$uid")

        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val Song=snapshot.getValue(Song::class.java)?:return
                SongsMap[snapshot.key!!]=Song
                refreshRecyclerSongs()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val Song=snapshot.getValue(Song::class.java)?:return
                SongsMap[snapshot.key!!]=Song
                refreshRecyclerSongs()
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
//                    val song = it.getValue(Song::class.java)
//                    Log.d(TAG, "song title:${song?.songName}")
//                    Log.d(TAG, "song artist:${song?.songArtist}")
//                    Log.d(TAG, "song album:${song?.albumUrl}")
//                    if (song != null) {
//                        adapter.add(SongRow(song))
//                    }
//                }
//            }
//        })

//
    }


}