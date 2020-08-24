package com.tapisdev.forumjualburung.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.admin.DetailTokoActivity
import com.tapisdev.forumjualburung.model.Burung
import com.tapisdev.forumjualburung.model.Toko
import kotlinx.android.synthetic.main.row_burung.view.*
import kotlinx.android.synthetic.main.row_toko.view.*
import kotlinx.android.synthetic.main.row_toko.view.lineToko
import java.io.Serializable

class AdapterBurung(private val list:ArrayList<Burung>) : RecyclerView.Adapter<AdapterBurung.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_burung,parent,false))
    }

    override fun getItemCount(): Int = list?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.view.tvNamaBurung.text = list?.get(position)?.nama
        holder.view.tvHarga.text = "Rp. "+list?.get(position)?.harga
        holder.view.tvKondisi.text = "Kondisi "+list?.get(position)?.kondisi

        Glide.with(holder.view.ivBurung.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivBurung)

        holder.view.lineToko.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
           /* val i = Intent(holder.view.lineToko.context, DetailTokoActivity::class.java)
            i.putExtra("toko",list.get(position) as Serializable)
            holder.view.lineToko.context.startActivity(i)*/
        }

    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}