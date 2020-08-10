package com.tapisdev.forumjualburung.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.admin.DetailCateringActivity
import com.tapisdev.forumjualburung.model.Catering
import kotlinx.android.synthetic.main.row_catering.view.*
import java.io.Serializable

class AdapterCatering(private val list:ArrayList<Catering>) : RecyclerView.Adapter<AdapterCatering.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_catering,parent,false))
    }

    override fun getItemCount(): Int = list?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.view.tvCateringName.text = list?.get(position)?.nama
        holder.view.tvDeskripsi.text = list?.get(position)?.deksripsi

        Glide.with(holder.view.ivFoodCart.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivFoodCart)

        holder.view.lineCatering.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
            val i = Intent(holder.view.lineCatering.context,DetailCateringActivity::class.java)
            i.putExtra("catering",list.get(position) as Serializable)
            holder.view.lineCatering.context.startActivity(i)
        }

    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}