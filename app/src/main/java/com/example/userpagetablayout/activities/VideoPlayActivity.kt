package com.example.userpagetablayout.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.userpagetablayout.R
import com.example.userpagetablayout.fragments.viewpagers.MusicFragment
import com.example.userpagetablayout.fragments.viewpagers.VideosFragment
import com.example.userpagetablayout.models.Song
import com.example.userpagetablayout.models.Video
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_music_play.*
import kotlinx.android.synthetic.main.activity_video_play.*

class VideoPlayActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    var video: Video? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_video_play)
        YoutubeVideoView.initialize("AIzaSyARKh8YVeVLW_rDPwdskyBq6EsAgsg76TY", this)


        video = intent.getParcelableExtra(VideosFragment.VIDEO_LINK_KEY)


//        webview_musiclink.settings.javaScriptEnabled = true
//        webview_musiclink.settings.loadWithOverviewMode = true
//        webview_musiclink.settings.useWideViewPort = true
//
//        if (video != null) {
//            webview_musiclink.loadUrl(video!!.videoUrl)
//        }


    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        youtubePlayer: YouTubePlayer?,
        wasRestored: Boolean
    ) {
        youtubePlayer?.setPlayerStateChangeListener(playerStateChangeListener)
        youtubePlayer?.setPlaybackEventListener(playbackEventListener)
        if(!wasRestored){
            youtubePlayer?.loadVideo(video!!.videoUrl)
        }
    }

    private val playerStateChangeListener=object:YouTubePlayer.PlayerStateChangeListener{
        override fun onAdStarted() {
            Toast.makeText(this@VideoPlayActivity,"Ad playing", Toast.LENGTH_SHORT).show()
        }

        override fun onLoading() {

        }

        override fun onVideoStarted() {
            Toast.makeText(this@VideoPlayActivity, "Video has started", Toast.LENGTH_SHORT).show()
        }

        override fun onLoaded(p0: String?) {

        }

        override fun onVideoEnded() {
            Toast.makeText(this@VideoPlayActivity, "Video has ended", Toast.LENGTH_SHORT).show()
        }

        override fun onError(p0: YouTubePlayer.ErrorReason?) {

        }
    }

    private val playbackEventListener=object:YouTubePlayer.PlaybackEventListener{
        override fun onSeekTo(p0: Int) {

        }

        override fun onBuffering(p0: Boolean) {

        }

        override fun onPlaying() {
            Toast.makeText(this@VideoPlayActivity, "Video playing", Toast.LENGTH_SHORT).show()
        }

        override fun onStopped() {
           // Toast.makeText(this@VideoPlayActivity, "Video stopped", Toast.LENGTH_SHORT).show()
        }

        override fun onPaused() {
            Toast.makeText(this@VideoPlayActivity, "Video has pasued", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider?,
        youTubeInitializationResult:YouTubeInitializationResult?
    ) {
        val REQUEST_CODE = 0

        if (youTubeInitializationResult?.isUserRecoverableError == true) {
            youTubeInitializationResult.getErrorDialog(this, REQUEST_CODE).show()
        } else {
            val errorMessage = "There was an error initializing the YoutubePlayer ($youTubeInitializationResult)"
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }


}