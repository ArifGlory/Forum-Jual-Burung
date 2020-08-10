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
import com.tapisdev.forumjualburung.model.Toko
import kotlinx.android.synthetic.main.row_toko.view.*
import java.io.Serializable

class AdapterToko(private val list:ArrayList<Toko>) : RecyclerView.Adapter<AdapterToko.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_toko,parent,false))
    }

    override fun getItemCount(): Int = list?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.view.tvName.text = list?.get(position)?.nama
        holder.view.tvAlamat.text = list?.get(position)?.alamat

        Glide.with(holder.view.ivToko.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivToko)

        holder.view.lineToko.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
            val i = Intent(holder.view.lineToko.context, DetailTokoActivity::class.java)
            i.putExtra("toko",list.get(position) as Serializable)
            holder.view.lineToko.context.startActivity(i)
        }

    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}