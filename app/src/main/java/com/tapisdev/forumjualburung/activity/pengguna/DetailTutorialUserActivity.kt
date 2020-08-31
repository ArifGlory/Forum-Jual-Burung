package com.tapisdev.forumjualburung.activity.pengguna

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Informasi
import com.tapisdev.forumjualburung.model.Tutorial
import kotlinx.android.synthetic.main.activity_detail_informasi_user.*
import kotlinx.android.synthetic.main.activity_detail_informasi_user.ivBack
import kotlinx.android.synthetic.main.activity_detail_tutorial_user.*
import kotlinx.android.synthetic.main.activity_show_video.*

class DetailTutorialUserActivity : BaseActivity() {

    lateinit var tutorial : Tutorial
    lateinit var i : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tutorial_user)

        i = intent
        tutorial = i.getSerializableExtra("tutorial") as Tutorial

        ivBack.setOnClickListener {
            onBackPressed()
        }

        updateUI()
    }

    fun updateUI(){
        tvTutorial.setText(tutorial.nama)
        tvDeskripsiTutorial.setText(tutorial.deskripsi)

        if (tutorial.tipe.equals("Video")){
            ivTutorial.visibility = View.GONE
            yt_player.visibility  = View.VISIBLE

            lifecycle.addObserver(yt_player)
            yt_player.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(tutorial.urlVideo.toString(), 0f)
                    //yt_player.enterFullScreen()
                }
            })
        }else{
            yt_player.visibility = View.GONE

            if (!tutorial.foto.equals("")){
                Glide.with(this)
                    .load(tutorial.foto)
                    .into(ivTutorial)
            }else{
                ivTutorial.setImageResource(R.drawable.ic_placeholder)
            }
        }

    }
}
