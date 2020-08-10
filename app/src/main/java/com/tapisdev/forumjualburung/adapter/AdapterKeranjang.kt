package com.tapisdev.forumjualburung.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.pengguna.KeranjangActivity
import com.tapisdev.forumjualburung.model.Cart
import kotlinx.android.synthetic.main.row_catering_user.view.tvName
import kotlinx.android.synthetic.main.row_catering_user.view.tvPrice
import kotlinx.android.synthetic.main.row_keranjang.view.*


class AdapterKeranjang(private val list:ArrayList<Cart>) : RecyclerView.Adapter<AdapterKeranjang.Holder>(){
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_keranjang,parent,false))
    }

    override fun getItemCount(): Int = list?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.view.tvName.text = list?.get(position)?.nama
        holder.view.tvJenis.text = list?.get(position)?.jenis
        holder.view.tvPrice.text = "Rp. "+list?.get(position)?.harga+ " @"+list?.get(position).jumlah+" paket"

        Glide.with(holder.view.ivKeranjang.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivKeranjang)

        holder.view.ivDeleteCartItem.setOnClickListener {
            list?.removeAt(position)
            if (holder.view.ivKeranjang.context is KeranjangActivity) {
                (holder.view.ivKeranjang.context as KeranjangActivity).refreshList()
            }
        }


    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)


}