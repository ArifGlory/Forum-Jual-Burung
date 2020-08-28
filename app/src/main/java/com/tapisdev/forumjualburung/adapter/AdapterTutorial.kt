package com.tapisdev.forumjualburung.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.admin.DetailBurungActivity
import com.tapisdev.forumjualburung.activity.admin.DetailTokoActivity
import com.tapisdev.forumjualburung.activity.admin.DetailTutorialActivity
import com.tapisdev.forumjualburung.model.Burung
import com.tapisdev.forumjualburung.model.Toko
import com.tapisdev.forumjualburung.model.Tutorial
import kotlinx.android.synthetic.main.row_burung.view.*
import kotlinx.android.synthetic.main.row_toko.view.*
import kotlinx.android.synthetic.main.row_toko.view.lineToko
import kotlinx.android.synthetic.main.row_tutorial.view.*
import java.io.Serializable

class AdapterTutorial(private val list:ArrayList<Tutorial>) : RecyclerView.Adapter<AdapterTutorial.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_tutorial,parent,false))
    }

    override fun getItemCount(): Int = list?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.view.tvNama.text = list?.get(position)?.nama
        holder.view.tvDeskripsi.text = list?.get(position)?.deskripsi
        holder.view.tvTipe.text = "Tutorial berbentuk "+list?.get(position)?.tipe

        Glide.with(holder.view.ivTutorial.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivTutorial)

        holder.view.lineToko.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
            val i = Intent(holder.view.lineToko.context, DetailTutorialActivity::class.java)
            i.putExtra("tutorial",list.get(position) as Serializable)
            holder.view.lineToko.context.startActivity(i)
        }

    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}