package com.tapisdev.forumjualburung.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.pengguna.DetailPenyediaActivity
import com.tapisdev.forumjualburung.model.SharedVariable
import com.tapisdev.forumjualburung.model.UserModel
import kotlinx.android.synthetic.main.row_toko_user.view.*
import java.io.Serializable

class AdapterPenyedia(private val list:ArrayList<UserModel>) : RecyclerView.Adapter<AdapterPenyedia.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_toko_user,parent,false))
    }

    override fun getItemCount(): Int = list?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.view.tvName.text = list?.get(position)?.name

        /*Glide.with(holder.view.ivCatering.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivCatering)

        holder.view.rlPenyedia.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
            SharedVariable.selectedIdPenyedia = list.get(position).uId

            val i = Intent(holder.view.rlPenyedia.context,DetailPenyediaActivity::class.java)
            i.putExtra("penyedia",list.get(position) as Serializable)
            holder.view.rlPenyedia.context.startActivity(i)
        }*/

    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}