package com.example.userpagetablayout.fragments.viewpagers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.userpagetablayout.R
import com.example.userpagetablayout.activities.UserPage
import com.example.userpagetablayout.activities.VideoPlayActivity
import com.example.userpagetablayout.activities.ZoomedImage
import com.example.userpagetablayout.databinding.FragmentVideosBinding
import com.example.userpagetablayout.fragments.viewPagerFragments.GalleryItem
import com.example.userpagetablayout.fragments.viewPagerFragments.VideoGalleryItem
import com.example.userpagetablayout.models.Video
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.gallery_item.*

class VideosFragment : Fragment() {
    companion object {
        val TAG = "videosfragment"
        val VIDEO_LINK_KEY = "VIDEO_LINK"
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
        val binding = DataBindingUtil.inflate<FragmentVideosBinding>(
            inflater,
            R.layout.fragment_videos,
            container,
            false
        )

        listenForImages()
        binding.RecyclerviewVideogalleryGridimages.adapter = adapter

        adapter.setOnItemLongClickListener { item, view ->
            val videoItem =item as VideoGalleryItem
            val popupMenu=PopupMenu(activity,view)
            popupMenu.inflate(R.menu.deletemenu)
            popupMenu.setOnMenuItemClickListener { item->
                when(item.itemId){
                R.id.deleteOption-> removeVideo(videoItem?.videoItem)
                }
                true
            }
            popupMenu.show()
            true  }

        adapter.setOnItemClickListener { item, view ->
            val videoItem = item as VideoGalleryItem
            val intent = Intent(activity, VideoPlayActivity::class.java)
            intent.putExtra(VIDEO_LINK_KEY, videoItem?.videoItem)
            startActivity(intent)

        }

        val manager = GridLayoutManager(activity, 3)
        binding.RecyclerviewVideogalleryGridimages.layoutManager = manager

        return binding.root

    }

    fun removeVideo(video: Video){
        val uid = FirebaseAuth.getInstance().uid
        val ref= FirebaseDatabase.getInstance().getReference("videoList/$uid/${video?.id}")
        ref.removeValue()
        Toast.makeText(activity, "VideoDeleted", Toast.LENGTH_SHORT).show()
        videosMap.values.remove(video)
        refreshRecyclerViewVideo()
    }

    val videosMap=HashMap<String, Video>()

    private fun refreshRecyclerViewVideo(){
        adapter.clear()
        videosMap.values.forEach {
            adapter.add(VideoGalleryItem(it))
        }
    }


    private fun listenForImages() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/videoList/$uid")

        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val video=snapshot.getValue(Video::class.java)?:return
                videosMap[snapshot.key!!]=video
                refreshRecyclerViewVideo()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val video=snapshot.getValue(Video::class.java)?:return
                videosMap[snapshot.key!!]=video
                refreshRecyclerViewVideo()
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {


            }
        })

    }
}