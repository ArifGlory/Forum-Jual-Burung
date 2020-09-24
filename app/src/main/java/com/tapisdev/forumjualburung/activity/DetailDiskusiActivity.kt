package com.tapisdev.forumjualburung.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.adapter.AdapterKomentar
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Diskusi
import com.tapisdev.forumjualburung.model.Komentar
import com.tapisdev.forumjualburung.model.UserPreference
import kotlinx.android.synthetic.main.activity_detail_diskusi.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailDiskusiActivity : BaseActivity() {

    lateinit var diskusi : Diskusi
    lateinit var i : Intent
    var TAG_GET = "getKomentar"
    var TAG_SIMPAN = "simpanKomentar"

    lateinit var adapter: AdapterKomentar
    var listKomentar = ArrayList<Komentar>()
    lateinit var komentar : Komentar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_diskusi)

        i = intent
        diskusi = i.getSerializableExtra("diskusi") as Diskusi
        mUserPref = UserPreference(this)

        adapter = AdapterKomentar(listKomentar)
        rvKomentar.setHasFixedSize(true)
        rvKomentar.layoutManager = LinearLayoutManager(this)
        rvKomentar.adapter = adapter

        tvAddKomentar.setOnClickListener {
            var komen = edKomentar.text.toString()

            if (komen.equals("") || komen.length == 0){
                showErrorMessage("Komentar belum diisi")
            }else{
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val currentDate = sdf.format(Date())

                komentar = Komentar("",
                    diskusi.diskusiId,
                    komen,
                    mUserPref.getName(),
                    auth.currentUser?.uid.toString(),
                    currentDate,
                    mUserPref.getFoto())
                saveKomentar()

            }
        }


        updateUI()
        getDataKomentar()
    }

    fun saveKomentar(){
        showLoading(this)
        komentarRef.document().set(komentar).addOnCompleteListener{
                task ->
            dismissLoading()
            if (task.isSuccessful){
                edKomentar.setText("")
                showSuccessMessage("Data  berhasil ditambahkan")
                getDataKomentar()
            }else{
                showLongErrorMessage("Penyimpanan data gagal")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
            }
        }
    }

    fun getDataKomentar(){
        komentarRef.get().addOnSuccessListener { result ->
            listKomentar.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
                var komentar : Komentar = document.toObject(Komentar::class.java)
                komentar.komentarId = document.id
                if (komentar.diskusiId.equals(diskusi.diskusiId)){
                    listKomentar.add(komentar)
                }
            }
            if (listKomentar.size == 0){
                animation_view_komentar.setAnimation(R.raw.empty_box)
                animation_view_komentar.playAnimation()
                animation_view_komentar.loop(false)
            }else{
                animation_view_komentar.visibility = View.INVISIBLE
            }
            Collections.reverse(listKomentar)
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET,"err : "+exception.message)
        }
    }

    fun updateUI(){
        tvJudulDiskusi.setText(diskusi.judul)
        tvDeskripsiDiskusi.setText(diskusi.deskripsi)
        if (diskusi.foto.equals("")){
            ivDiskusi.visibility = View.GONE
        }else{
            Glide.with(this)
                .load(diskusi.foto)
                .into(ivDiskusi)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataKomentar()
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
}
