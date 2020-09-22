package com.tapisdev.forumjualburung.activity.pengguna

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.adapter.AdapterBurung
import com.tapisdev.forumjualburung.adapter.AdapterInformasi
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Burung
import kotlinx.android.synthetic.main.activity_list_burung_user.*
import java.util.*
import kotlin.collections.ArrayList

class ListBurungUserActivity : BaseActivity() {

    var TAG_GET = "getBurung"
    lateinit var adapter: AdapterBurung
    var listBurung = ArrayList<Burung>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_burung_user)

        adapter = AdapterBurung(listBurung)
        rvBurungUser.setHasFixedSize(true)
        rvBurungUser.layoutManager = GridLayoutManager(this, 2)
        rvBurungUser.adapter = adapter

        getDataBurung()
    }

    fun getDataBurung(){
        burungRef.get().addOnSuccessListener { result ->
            listBurung.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
                var burung : Burung = document.toObject(Burung::class.java)
                burung.burungId = document.id
                listBurung.add(burung)
            }
            if (listBurung.size == 0){
                animation_view.setAnimation(R.raw.empty_box)
                animation_view.playAnimation()
                animation_view.loop(false)
            }else{
                animation_view.visibility = View.INVISIBLE
            }
            Collections.reverse(listBurung)
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataBurung()
    }
}
