package com.tapisdev.forumjualburung.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.admin.DetailInformasiActivity
import com.tapisdev.forumjualburung.activity.pengguna.DetailInformasiUserActivity
import com.tapisdev.forumjualburung.model.Informasi
import com.tapisdev.forumjualburung.model.UserPreference
import kotlinx.android.synthetic.main.row_informasi.view.*
import java.io.Serializable

class AdapterInformasi(private val list:ArrayList<Informasi>) : RecyclerView.Adapter<AdapterInformasi.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_informasi,parent,false))
    }

    lateinit var mUserPref : UserPreference

    override fun getItemCount(): Int = list?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        mUserPref = UserPreference(holder.view.lineInformasi.context)

        holder.view.tvCateringName.text = list?.get(position)?.judul
        holder.view.tvDeskripsi.text = list?.get(position)?.deskripsi

        Glide.with(holder.view.ivFoodCart.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivFoodCart)

        holder.view.lineInformasi.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
            if (mUserPref.getJenisUser().equals("admin")){
                val i = Intent(holder.view.lineInformasi.context, DetailInformasiActivity::class.java)
                i.putExtra("informasi",list.get(position) as Serializable)
                holder.view.lineInformasi.context.startActivity(i)
            }else{
                val i = Intent(holder.view.lineInformasi.context, DetailInformasiUserActivity::class.java)
                i.putExtra("informasi",list.get(position) as Serializable)
                holder.view.lineInformasi.context.startActivity(i)
            }

        }

    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}