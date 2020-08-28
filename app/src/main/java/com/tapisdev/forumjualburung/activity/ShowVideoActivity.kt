package com.tapisdev.forumjualburung.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.tapisdev.forumjualburung.R
import kotlinx.android.synthetic.main.activity_show_video.*


class ShowVideoActivity : AppCompatActivity() {

    lateinit var i : Intent
    var idVideo = "none"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_video)

        i = intent
        idVideo = i.getStringExtra("idVideo")

        lifecycle.addObserver(youtube_player_view)

        youtube_player_view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(idVideo, 0f)
                youtube_player_view.enterFullScreen()
            }
        })
    }
}
