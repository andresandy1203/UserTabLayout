package com.example.userpagetablayout.view

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.userpagetablayout.R
import com.example.userpagetablayout.databinding.ActivityVideoPlayBinding
import com.example.userpagetablayout.adapter.fragments.VideosFragment
import com.example.userpagetablayout.model.Video
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer

class VideoPlayActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    var video: Video? = null
    var binding: ActivityVideoPlayBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Data Binding
        @Suppress("UNUSED_VARIABLE")
        binding = DataBindingUtil.setContentView<ActivityVideoPlayBinding>(
            this,
            R.layout.activity_video_play
        )

        //Initialize API
        binding?.YoutubeVideoView?.initialize("YOUR API KEY", this)

        //Get video data from previous activity
        video = intent.getParcelableExtra(VideosFragment.VIDEO_LINK_KEY)

    }

    //Initialize Youtube Play View
    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        youtubePlayer: YouTubePlayer?,
        wasRestored: Boolean
    ) {
        youtubePlayer?.setPlayerStateChangeListener(playerStateChangeListener)
        youtubePlayer?.setPlaybackEventListener(playbackEventListener)
        if (!wasRestored) {
            youtubePlayer?.loadVideo(video!!.videoUrl)
        }
    }

    //Listen for error on initialization
    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider?,
        youTubeInitializationResult: YouTubeInitializationResult?
    ) {
        val REQUEST_CODE = 0

        if (youTubeInitializationResult?.isUserRecoverableError == true) {
            youTubeInitializationResult.getErrorDialog(this, REQUEST_CODE).show()
        } else {
            val errorMessage =
                "There was an error initializing the YoutubePlayer ($youTubeInitializationResult)"
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    //Control the actions taken with the Youtube Play View
    private val playerStateChangeListener = object : YouTubePlayer.PlayerStateChangeListener {
        override fun onAdStarted() {
            Toast.makeText(this@VideoPlayActivity, "Ad playing", Toast.LENGTH_SHORT).show()
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

    private val playbackEventListener = object : YouTubePlayer.PlaybackEventListener {
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


}