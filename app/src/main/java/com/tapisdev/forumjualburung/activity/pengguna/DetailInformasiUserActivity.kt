package com.tapisdev.forumjualburung.activity.pengguna

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Burung
import com.tapisdev.forumjualburung.model.Informasi
import kotlinx.android.synthetic.main.activity_detail_burung_user.*
import kotlinx.android.synthetic.main.activity_detail_informasi_user.*

class DetailInformasiUserActivity : BaseActivity() {

    lateinit var informasi : Informasi
    lateinit var i : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_informasi_user)

        i = intent
        informasi = i.getSerializableExtra("informasi") as Informasi

        ivBack.setOnClickListener {
            onBackPressed()
        }

        updateUI()
    }

    fun updateUI(){
        tvJudul.setText(informasi.judul)
        tvDeskripsiInformasi.setText(informasi.deskripsi)

        if (!informasi.foto.equals("")){
            Glide.with(this)
                .load(informasi.foto)
                .into(ivInformasi)
        }else{
            ivInformasi.setImageResource(R.drawable.ic_placeholder)
        }

    }
}
