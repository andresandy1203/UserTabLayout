package com.example.userpagetablayout.main.model

import com.bumptech.glide.Glide
import com.example.userpagetablayout.R
import com.example.userpagetablayout.model.Song
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.song_row.view.*

class SongRow(val songItem: Song) : Item<GroupieViewHolder>() {


    companion object {
        val TAG = "songrow"

    }

    //Bind the View Holder to the Song Row UI
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.artistname_textview.text = songItem.songArtist
        viewHolder.itemView.songname_textView.text = songItem.songName

        Glide.with(viewHolder.itemView.context).load(songItem.albumUrl)
            .into(viewHolder.itemView.imageView_album)

    }

    override fun getLayout(): Int {
        return R.layout.song_row
    }
}