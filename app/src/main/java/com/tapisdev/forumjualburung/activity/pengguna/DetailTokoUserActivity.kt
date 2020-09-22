package com.tapisdev.forumjualburung.activity.pengguna

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.LokasiTokoActivity
import com.tapisdev.forumjualburung.adapter.AdapterBurung
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Burung
import com.tapisdev.forumjualburung.model.Toko
import kotlinx.android.synthetic.main.activity_detail_toko_user.*

class DetailTokoUserActivity : BaseActivity() {

    lateinit var toko : Toko
    lateinit var i : Intent

    var TAG_GET_BURUNG = "getBurung"
    lateinit var adapter: AdapterBurung

    var listBurung = ArrayList<Burung>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_toko_user)

        i = intent
        toko = i.getSerializableExtra("toko") as Toko

        adapter = AdapterBurung(listBurung)
        rvBurungUser.setHasFixedSize(true)
        rvBurungUser.layoutManager = LinearLayoutManager(this)
        rvBurungUser.adapter = adapter


        ivBack.setOnClickListener {
            onBackPressed()
        }
        tvAlamat.setOnClickListener {
            val i = Intent(this,LokasiTokoActivity::class.java)
            i.putExtra("latlon",toko.latlon)
            startActivity(i)
        }


        updateUI()
        getDataMyBurung()
    }

    fun updateUI(){
        tvName.setText(toko.nama)
        tvAlamat.setText(toko.alamat)
        if (!toko.foto.equals("")){
            Glide.with(this)
                .load(toko.foto)
                .into(ivToko)
        }else{
            ivToko.setImageResource(R.drawable.ic_placeholder)
        }

    }

    fun getDataMyBurung(){
        burungRef.get().addOnSuccessListener { result ->
            listBurung.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
                var burung : Burung = document.toObject(Burung::class.java)
                burung.burungId = document.id
                if (burung.idToko.equals(toko.tokoId)){
                    listBurung.add(burung)
                }
            }
            if (listBurung.size == 0){
                animation_view_toko.setAnimation(R.raw.empty_box)
                animation_view_toko.playAnimation()
                animation_view_toko.loop(false)
            }else{
                animation_view_toko.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_BURUNG,"err : "+exception.message)
        }
    }

}
