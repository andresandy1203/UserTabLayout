package com.example.userpagetablayout.fragments.viewPagerFragments

import com.bumptech.glide.Glide
import com.example.userpagetablayout.R
import com.example.userpagetablayout.models.Video
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.gallery_item.view.*

class VideoGalleryItem(val videoItem: Video) : Item<GroupieViewHolder>() {
    companion object {
        val TAG = "VideogalleryItem"
    }


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        if(videoItem!=null){
            //Picasso.get().load(videoItem.imageUrl).into(viewHolder.itemView.imageview_item)
            Glide.with(viewHolder.itemView.context).load(videoItem.imageUrl).into(viewHolder.itemView.imageview_item)
        }



    }

    override fun getLayout(): Int {
        return R.layout.gallery_item
    }


}