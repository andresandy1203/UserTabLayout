package com.example.userpagetablayout.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.userpagetablayout.R
import com.example.userpagetablayout.databinding.ActivityLoginBinding
import com.example.userpagetablayout.databinding.ActivityMusicPlayBinding
import com.example.userpagetablayout.fragments.viewpagers.MusicFragment
import com.example.userpagetablayout.models.Song
import kotlinx.android.synthetic.main.activity_music_play.*

class MusicPlay : AppCompatActivity() {

    var song: Song? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMusicPlayBinding>(
            this,
            R.layout.activity_music_play
        )

        //Get the song data from th previous activity
        song = intent.getParcelableExtra(MusicFragment.SONG_LINK_KEY)

        //Enable the needed setttings
        binding.webviewMusiclink.settings.javaScriptEnabled = true
        binding.webviewMusiclink.settings.loadWithOverviewMode = true
        binding.webviewMusiclink.settings.useWideViewPort = true

        //Load the web url
        if (song != null) {
            binding.webviewMusiclink.loadUrl(song!!.webUrl)
        }

    }
}