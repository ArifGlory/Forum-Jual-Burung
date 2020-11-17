package com.tapisdev.forumjualburung.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.DetailDiskusiActivity
import com.tapisdev.forumjualburung.activity.ProfilUserActivity
import com.tapisdev.forumjualburung.activity.admin.DetailInformasiActivity
import com.tapisdev.forumjualburung.activity.pengguna.DetailBurungUserActivity
import com.tapisdev.forumjualburung.activity.pengguna.DetailInformasiUserActivity
import com.tapisdev.forumjualburung.model.Diskusi
import com.tapisdev.forumjualburung.model.Informasi
import com.tapisdev.forumjualburung.model.Komentar
import com.tapisdev.forumjualburung.model.UserPreference
import kotlinx.android.synthetic.main.row_diskusi.view.*
import kotlinx.android.synthetic.main.row_informasi.view.*
import kotlinx.android.synthetic.main.row_informasi.view.tvDeskripsi
import kotlinx.android.synthetic.main.row_komentar.view.*
import kotlinx.android.synthetic.main.row_toko.view.*
import java.io.Serializable
import java.text.SimpleDateFormat

class AdapterKomentar(private val list:ArrayList<Komentar>) : RecyclerView.Adapter<AdapterKomentar.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_komentar,parent,false))
    }

    lateinit var mUserPref : UserPreference

    override fun getItemCount(): Int = list?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        mUserPref = UserPreference(holder.view.lineKomentar.context)
        var tanggal = list?.get(position)?.tanggal
        tanggal = convertDate(tanggal.toString())

        holder.view.tvNamaKomentar.text = list?.get(position)?.nama
        holder.view.tvIsiKomentar.text = list?.get(position)?.isiKomentar
        holder.view.tvTanggal.text = ""+tanggal

        Glide.with(holder.view.ivKomentar.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivKomentar)

        holder.view.lineKomentar.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
            val i = Intent(holder.view.lineKomentar.context, ProfilUserActivity::class.java)
            i.putExtra("iduser",list.get(position).idPembuat)
            holder.view.lineKomentar.context.startActivity(i)
        }

    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

    fun convertDate(tanggal : String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        //val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val output = formatter.format(parser.parse(tanggal))

        return output
    }
}