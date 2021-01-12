package com.example.userpagetablayout.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import com.example.userpagetablayout.R
import com.example.userpagetablayout.databinding.ActivityYoutubeSearchBinding
import com.example.userpagetablayout.model.Video
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern;

class YoutubeSearchActivity : AppCompatActivity() {

    var binding: ActivityYoutubeSearchBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityYoutubeSearchBinding>(
            this,
            R.layout.activity_youtube_search
        )

        //Enable the needed setttings
        binding?.videoSearch?.settings?.javaScriptEnabled = true
        binding?.videoSearch?.settings?.loadWithOverviewMode = true
        binding?.videoSearch?.settings?.useWideViewPort = true
        binding?.videoSearch?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
        }
        binding?.videoSearch?.loadUrl("https://www.youtube.com/")

        binding?.saveVideo?.setOnClickListener {
            saveVideoToFirebase()
        }
    }

    private fun saveVideoToFirebase() {
        val videoUrl = binding?.videoSearch?.url
        Log.d("YoutubeSearch", "$videoUrl")
        val videoID = getVideoId(videoUrl!!)
        Log.d("YoutubeSearch", "ID: $videoID")
        if (videoID == null) {
            Toast.makeText(this, "Please select a valid youtube video", Toast.LENGTH_SHORT).show()
        } else {
            //If a valid video ID was found it is saved to Firebase
            Toast.makeText(this, "Saving Video", Toast.LENGTH_SHORT).show()
            val videoImage = "https://img.youtube.com/vi/$videoID/0.jpg";
            val uid = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("videoList/$uid").push()
            val Video = Video(ref.key!!, videoImage, videoID)
            ref.setValue(Video).addOnSuccessListener {
                Toast.makeText(this, "Video Saved", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UserPageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
                .addOnFailureListener {
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun getVideoId(@NonNull videoUrl: String): String? {
        //Create patten to extract video ID
        val reg: String =
            "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)"
        val pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        val matcher = pattern.matcher(videoUrl)

        if (matcher.find()) {
            return matcher.group(1)
        } else {
            return null
        }

    }
}