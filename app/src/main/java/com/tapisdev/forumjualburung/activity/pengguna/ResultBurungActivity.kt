package com.tapisdev.forumjualburung.activity.pengguna

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.adapter.AdapterBurung
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Burung
import kotlinx.android.synthetic.main.activity_list_burung_user.*
import kotlinx.android.synthetic.main.activity_result_burung.*
import java.util.*
import kotlin.collections.ArrayList

class ResultBurungActivity : BaseActivity() {

    lateinit var keyword : String
    lateinit var i : Intent

    var TAG_GET = "getBurung"
    lateinit var adapter: AdapterBurung
    var listBurung = ArrayList<Burung>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_burung)
        i = intent
        keyword = i.getStringExtra("keyword")

        adapter = AdapterBurung(listBurung)
        rvResultBurung.setHasFixedSize(true)
        rvResultBurung.layoutManager = GridLayoutManager(this, 2)
        rvResultBurung.adapter = adapter

        getDataBurung()

    }

    fun getDataBurung(){
        burungRef.get().addOnSuccessListener { result ->
            listBurung.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
                var burung : Burung = document.toObject(Burung::class.java)
                var namaBurung = burung.nama
                namaBurung = namaBurung?.toLowerCase()
                keyword = keyword.toLowerCase()

                if (namaBurung!!.contains(keyword)){
                    burung.burungId = document.id
                    listBurung.add(burung)
                }
            }
            if (listBurung.size == 0){
                animation_view_result.setAnimation(R.raw.empty_box)
                animation_view_result.playAnimation()
                animation_view_result.loop(false)
            }else{
                animation_view_result.visibility = View.INVISIBLE
            }
            Collections.reverse(listBurung)
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET,"err : "+exception.message)
        }
    }
}
