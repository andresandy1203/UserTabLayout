package com.example.userpagetablayout.fragments.viewPagerFragments

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.example.userpagetablayout.R
import com.example.userpagetablayout.models.GalleryImage
import com.example.userpagetablayout.models.Song
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.gallery_item.view.*
import kotlinx.android.synthetic.main.song_row.view.*
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class GalleryItem(val imageItem: GalleryImage) : Item<GroupieViewHolder>() {
    companion object {
        val TAG = "galleryItem"
    }


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {



       // Picasso.get().load(imageItem.imageUrl).into(viewHolder.itemView.imageview_item)
       Glide.with(viewHolder.itemView.context).load(imageItem.imageUrl).into(viewHolder.itemView.imageview_item)

    }

    override fun getLayout(): Int {
        return R.layout.gallery_item
    }


}