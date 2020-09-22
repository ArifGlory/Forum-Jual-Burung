package com.tapisdev.forumjualburung.activity.pengguna

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.adapter.AdapterInformasi
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Informasi
import kotlinx.android.synthetic.main.activity_list_informasi_user.*
import java.util.*
import kotlin.collections.ArrayList

class ListInformasiUserActivity : BaseActivity() {

    var TAG_GET = "getInformasi"
    lateinit var adapter: AdapterInformasi
    var listInformasi = ArrayList<Informasi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_informasi_user)

        adapter = AdapterInformasi(listInformasi)
        rvInformasiUser.setHasFixedSize(true)
        rvInformasiUser.layoutManager = GridLayoutManager(this, 2)
        rvInformasiUser.adapter = adapter

        getDataInformasi()
    }

    fun getDataInformasi(){
        informasiRef.get().addOnSuccessListener { result ->
            listInformasi.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
                var informasi : Informasi = document.toObject(Informasi::class.java)
                informasi.informasiId = document.id
                listInformasi.add(informasi)
            }
            if (listInformasi.size == 0){
                animation_view.setAnimation(R.raw.empty_box)
                animation_view.playAnimation()
                animation_view.loop(false)
            }else{
                animation_view.visibility = View.INVISIBLE
            }
            Collections.reverse(listInformasi)
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataInformasi()
    }
}
