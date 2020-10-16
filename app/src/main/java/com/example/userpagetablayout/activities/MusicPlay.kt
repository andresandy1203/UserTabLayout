package com.example.userpagetablayout.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.userpagetablayout.R
import com.example.userpagetablayout.fragments.viewpagers.MusicFragment
import com.example.userpagetablayout.models.Song
import kotlinx.android.synthetic.main.activity_music_play.*

class MusicPlay : AppCompatActivity() {

    var song: Song? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_play)

        song = intent.getParcelableExtra(MusicFragment.SONG_LINK_KEY)
        webview_musiclink.settings.javaScriptEnabled = true
        webview_musiclink.settings.loadWithOverviewMode = true
        webview_musiclink.settings.useWideViewPort = true

        if (song != null) {
            webview_musiclink.loadUrl(song!!.webUrl)
        }

    }
}