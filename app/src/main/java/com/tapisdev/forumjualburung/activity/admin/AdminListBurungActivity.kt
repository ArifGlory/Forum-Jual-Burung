package com.tapisdev.forumjualburung.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.adapter.AdapterBurung
import com.tapisdev.forumjualburung.adapter.AdapterToko
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Burung
import com.tapisdev.forumjualburung.model.Toko
import kotlinx.android.synthetic.main.activity_admin_list_burung.*
import kotlinx.android.synthetic.main.fragment_admin_informasi.*
import kotlinx.android.synthetic.main.fragment_admin_informasi.animation_view

class AdminListBurungActivity : BaseActivity() {

    lateinit var tokoId : String
    var TAG_GET_BURUNG = "getBurung"
    lateinit var adapter: AdapterBurung

    var listBurung = ArrayList<Burung>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_burung)

        tokoId = intent.getStringExtra("tokoId")

        adapter = AdapterBurung(listBurung)
        rvBurung.setHasFixedSize(true)
        rvBurung.layoutManager = LinearLayoutManager(this)
        rvBurung.adapter = adapter

        addBurung.setOnClickListener {
            val i = Intent(this,AddBurungActivity::class.java)
            i.putExtra("tokoId",tokoId)
            startActivity(i)
        }

        getDataMyBurung()
    }

    fun getDataMyBurung(){
        burungRef.get().addOnSuccessListener { result ->
            listBurung.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
                var burung : Burung = document.toObject(Burung::class.java)
                burung.burungId = document.id
                if (burung.idToko.equals(tokoId)){
                    listBurung.add(burung)
                }
            }
            if (listBurung.size == 0){
                animation_view.setAnimation(R.raw.empty_box)
                animation_view.playAnimation()
                animation_view.loop(false)
            }else{
                animation_view.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_BURUNG,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataMyBurung()
    }
}
