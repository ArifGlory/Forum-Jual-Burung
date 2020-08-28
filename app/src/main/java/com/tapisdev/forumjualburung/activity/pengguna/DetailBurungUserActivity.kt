package com.tapisdev.forumjualburung.activity.pengguna

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Burung
import kotlinx.android.synthetic.main.activity_detail_burung_user.*
import kotlinx.android.synthetic.main.activity_detail_toko_user.*

class DetailBurungUserActivity : BaseActivity() {

    lateinit var burung : Burung
    lateinit var i : Intent

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_burung_user)

        i = intent
        burung = i.getSerializableExtra("burung") as Burung

        updateUI()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun updateUI(){
        tvNamaBurung.setText(burung.nama)
        tvPriceBurung.setText("Rp. "+burung.harga)
        tvKondisiBurung.setText("Kondisi Burung "+burung.kondisi)

        if (burung.kondisi.equals("Sakit")){
            tvKondisiBurung.backgroundTintList = resources.getColorStateList(R.color.red_btn_bg_color)
        }

        if (!burung.foto.equals("")){
            Glide.with(this)
                .load(burung.foto)
                .into(ivBurung)
        }else{
            ivBurung.setImageResource(R.drawable.ic_placeholder)
        }

    }
}
