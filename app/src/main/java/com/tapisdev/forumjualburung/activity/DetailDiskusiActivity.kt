package com.tapisdev.forumjualburung.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Diskusi
import kotlinx.android.synthetic.main.activity_detail_diskusi.*

class DetailDiskusiActivity : BaseActivity() {

    lateinit var diskusi : Diskusi
    lateinit var i : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_diskusi)

        i = intent
        diskusi = i.getSerializableExtra("diskusi") as Diskusi
    }

    fun updateUI(){
        tvJudulDiskusi.setText(diskusi.judul)
        tvDeskripsiDiskusi.setText(diskusi.deskripsi)
        if (diskusi.foto.equals("")){
            ivDiskusi.visibility = View.GONE
        }else{
            Glide.with(this)
                .load(diskusi.foto)
                .into(ivDiskusi)
        }
    }
}
