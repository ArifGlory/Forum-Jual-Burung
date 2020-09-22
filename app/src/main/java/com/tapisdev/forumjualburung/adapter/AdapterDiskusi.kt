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
import com.tapisdev.forumjualburung.activity.admin.DetailInformasiActivity
import com.tapisdev.forumjualburung.activity.pengguna.DetailBurungUserActivity
import com.tapisdev.forumjualburung.activity.pengguna.DetailInformasiUserActivity
import com.tapisdev.forumjualburung.model.Diskusi
import com.tapisdev.forumjualburung.model.Informasi
import com.tapisdev.forumjualburung.model.UserPreference
import kotlinx.android.synthetic.main.row_diskusi.view.*
import kotlinx.android.synthetic.main.row_informasi.view.*
import kotlinx.android.synthetic.main.row_informasi.view.tvDeskripsi
import kotlinx.android.synthetic.main.row_toko.view.*
import java.io.Serializable

class AdapterDiskusi(private val list:ArrayList<Diskusi>) : RecyclerView.Adapter<AdapterDiskusi.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_diskusi,parent,false))
    }

    lateinit var mUserPref : UserPreference

    override fun getItemCount(): Int = list?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        mUserPref = UserPreference(holder.view.lineDiskusi.context)

        holder.view.tvJudul.text = list?.get(position)?.judul
        holder.view.tvDeskripsiDiskusi.text = list?.get(position)?.deskripsi
        holder.view.tvTipePembuat.text = "Diskusi dimulai oleh "+list?.get(position)?.namaPembuat


        holder.view.lineDiskusi.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
            val i = Intent(holder.view.lineDiskusi.context, DetailDiskusiActivity::class.java)
            i.putExtra("diskusi",list.get(position) as Serializable)
            holder.view.lineToko.context.startActivity(i)
        }

    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}