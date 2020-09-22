package com.tapisdev.forumjualburung.activity.pengguna

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.AddDiskusiActivity
import com.tapisdev.forumjualburung.adapter.AdapterDiskusi
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Diskusi
import kotlinx.android.synthetic.main.activity_list_diskusi.*
import java.util.*
import kotlin.collections.ArrayList

class ListDiskusiActivity : BaseActivity() {

    var TAG_GET = "getDiskusi"
    lateinit var adapter: AdapterDiskusi
    var listDiskusi = ArrayList<Diskusi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_diskusi)

        adapter = AdapterDiskusi(listDiskusi)
        rvDiskusiUser.setHasFixedSize(true)
        rvDiskusiUser.layoutManager = LinearLayoutManager(this)
        rvDiskusiUser.adapter = adapter

        ivAddDiskusi.setOnClickListener {
            val i = Intent(this,AddDiskusiActivity::class.java)
            startActivity(i)
        }

        getDataDiskusi()
    }

    fun getDataDiskusi(){
        diskusiRef.get().addOnSuccessListener { result ->
            listDiskusi.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
                var diskusi : Diskusi = document.toObject(Diskusi::class.java)
                diskusi.diskusiId = document.id
                listDiskusi.add(diskusi)
            }
            if (listDiskusi.size == 0){
                animation_view.setAnimation(R.raw.empty_box)
                animation_view.playAnimation()
                animation_view.loop(false)
            }else{
                animation_view.visibility = View.INVISIBLE
            }
            Collections.reverse(listDiskusi)
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataDiskusi()
    }
}
