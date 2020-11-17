package com.tapisdev.forumjualburung.activity.pengguna

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.adapter.AdapterTokoUser
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Toko
import com.tapisdev.forumjualburung.model.UserModel
import kotlinx.android.synthetic.main.activity_list_toko.*
import java.util.*
import kotlin.collections.ArrayList

class ListTokoActivity : BaseActivity() {

    var TAG_GET = "getToko"
    lateinit var adapter: AdapterTokoUser
    var listToko = ArrayList<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_toko)

        adapter = AdapterTokoUser(listToko)
        rvTokoUser.setHasFixedSize(true)
        rvTokoUser.layoutManager = GridLayoutManager(this, 2)
        rvTokoUser.adapter = adapter

        getDataToko()
    }

    fun getDataToko(){
        userRef.get().addOnSuccessListener { result ->
            listToko.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
                var toko : UserModel = document.toObject(UserModel::class.java)
                toko.uId = document.id
                if (toko.jenis.equals("penjual")){
                    listToko.add(toko)
                }
            }
            if (listToko.size == 0){
                animation_view.setAnimation(R.raw.empty_box)
                animation_view.playAnimation()
                animation_view.loop(false)
            }else{
                animation_view.visibility = View.INVISIBLE
            }
            Collections.reverse(listToko)
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataToko()
    }
}
