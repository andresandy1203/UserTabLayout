package com.example.userpagetablayout.main.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.userpagetablayout.R
import com.example.userpagetablayout.main.EditSongDetailsActivity
import com.example.userpagetablayout.main.MusicPlayActivity
import com.example.userpagetablayout.databinding.FragmentMusicBinding
import com.example.userpagetablayout.main.model.SongRow
import com.example.userpagetablayout.util.SwipeHelper

import com.example.userpagetablayout.model.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class MusicFragment : Fragment() {
    companion object {
        val TAG = "musicfragment"
        val SONG_LINK_KEY = "SONG_LINK"

    }

    //Set up adapter
    val adapter = GroupAdapter<GroupieViewHolder>()

    //HashMap for the recycler view of songs
    val SongsMap = HashMap<String, Song>()

    @SuppressLint()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        //Inflate the layout for this fragment
        //Data Binding
        val binding = DataBindingUtil.inflate<FragmentMusicBinding>(
            inflater,
            R.layout.fragment_music,
            container,
            false
        )

        //Get the songs data
        listenForSongs()

        //set up recycler view's adapter
        binding.recyclerviewSongs.adapter = adapter
        binding.recyclerviewSongs.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )

        //Click listener for the recycler view items
        adapter.setOnItemClickListener { item, view ->
            //Sending the data of the selected song to the new activity
            val songItem = item as SongRow
            val intent = Intent(activity, MusicPlayActivity::class.java)
            intent.putExtra(SONG_LINK_KEY, songItem?.songItem)
            startActivity(intent)
        }

        //Create the options in the Swipe buttons
        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(binding.recyclerviewSongs) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                var buttons = listOf<UnderlayButton>()
                val deleteButton = deleteButton(position)
                val editButton = editButton(position)
                buttons = listOf(deleteButton, editButton)
                return buttons
            }

        })

        //Attach the Swipe helper into the recycler view items
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewSongs)

        return binding.root


    }

    //Create the delete button for the Swipe Helper
    private fun deleteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(requireActivity(),
            "Delete",
            14.0f,
            android.R.color.holo_red_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                //on click it will call the removeSong function, sending the selected song data
                override fun onClick() {
                    val item = adapter.getItem(position)
                    val songItem = item as SongRow
                    removeSong(songItem.songItem)
                }
            })
    }

    //Create the edit button for the Swipe Helper
    private fun editButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(requireActivity(),
            "Edit",
            14.0f,
            android.R.color.holo_blue_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                //on click go to the edit activity
                override fun onClick() {
                    //Send the data of the selected song to the new activity
                    val item = adapter.getItem(position)
                    val songItem = item as SongRow
                    val intent = Intent(activity, EditSongDetailsActivity::class.java)
                    intent.putExtra(SONG_LINK_KEY, songItem?.songItem)
                    startActivity(intent)
                }
            })
    }

    //Remove song from the Firebase Database function
    fun removeSong(song: Song) {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("songList/$uid/${song?.id}")
        ref.removeValue()
        Toast.makeText(activity, "Song Deleted", Toast.LENGTH_SHORT).show()
        SongsMap.values.remove(song)
        refreshRecyclerSongs()
    }

    //Refresh when the list of songs change
    private fun refreshRecyclerSongs() {
        adapter.clear()
        SongsMap.values.forEach {
            adapter.add(SongRow(it))
        }
    }

    //Load songs from Firebase database
    private fun listenForSongs() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/songList/$uid")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val Song = snapshot.getValue(Song::class.java) ?: return
                SongsMap[snapshot.key!!] = Song
                refreshRecyclerSongs()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val Song = snapshot.getValue(Song::class.java) ?: return
                SongsMap[snapshot.key!!] = Song
                refreshRecyclerSongs()
            }

            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })

    }


}