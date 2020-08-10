package com.tapisdev.forumjualburung.activity.pengguna

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.adapter.MyPagerAdapter
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.SharedVariable
import com.tapisdev.forumjualburung.model.UserModel
import kotlinx.android.synthetic.main.activity_detail_catering_user.*

class DetailPenyediaActivity : BaseActivity() {

    lateinit var penyedia : UserModel
    lateinit var i : Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_catering_user)

        i = intent
        penyedia = i.getSerializableExtra("penyedia") as UserModel
        Log.d("selectedPenyedia",SharedVariable.selectedIdPenyedia)

        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        viewpager_main.adapter = fragmentAdapter
        tabs_main.setupWithViewPager(viewpager_main)


        ivBack.setOnClickListener {
            onBackPressed()
        }
        ivCart.setOnClickListener {
            val i = Intent(this,KeranjangActivity::class.java)
            startActivity(i)
        }

        updateUI()

    }

    fun updateUI(){
        tvName.setText(penyedia.name)
        if (!penyedia.foto.equals("")){
            Glide.with(this)
                .load(penyedia.foto)
                .into(ivPenyedia)
        }else{
            ivPenyedia.setImageResource(R.drawable.ic_placeholder)
        }

    }

}
