package com.example.userpagetablayout.main.model

import com.bumptech.glide.Glide
import com.example.userpagetablayout.R
import com.example.userpagetablayout.model.GalleryImage
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.gallery_item.view.*

class GalleryItem(val imageItem: GalleryImage) : Item<GroupieViewHolder>() {
    companion object {
        val TAG = "galleryItem"
    }

    //Bind the viewHolder to the Gallery Item UI
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        Glide.with(viewHolder.itemView.context).load(imageItem.imageUrl)
            .into(viewHolder.itemView.imageview_item)

    }

    override fun getLayout(): Int {
        return R.layout.gallery_item
    }


}