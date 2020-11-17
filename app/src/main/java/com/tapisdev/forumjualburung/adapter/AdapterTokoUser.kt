package com.tapisdev.forumjualburung.adapter

import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.admin.DetailBurungActivity
import com.tapisdev.forumjualburung.activity.pengguna.DetailTokoUserActivity
import com.tapisdev.forumjualburung.model.Cart
import com.tapisdev.forumjualburung.model.Toko
import com.tapisdev.forumjualburung.model.SharedVariable
import com.tapisdev.forumjualburung.model.UserModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_toko.view.*
import kotlinx.android.synthetic.main.row_toko_user.view.*
import kotlinx.android.synthetic.main.row_toko_user.view.ivToko
import kotlinx.android.synthetic.main.row_toko_user.view.lineToko
import kotlinx.android.synthetic.main.row_toko_user.view.tvAlamat
import kotlinx.android.synthetic.main.row_toko_user.view.tvName
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class AdapterTokoUser(private val list:ArrayList<UserModel>) : RecyclerView.Adapter<AdapterTokoUser.Holder>(){
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_toko_user,parent,false))
    }

    override fun getItemCount(): Int = list?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.view.tvName.text = list?.get(position)?.name
        holder.view.tvAlamat.text =list?.get(position)?.alamat

        Glide.with(holder.view.ivToko.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivToko)

       holder.view.lineToko.setOnClickListener {
           val i = Intent(holder.view.lineToko.context, DetailTokoUserActivity::class.java)
           i.putExtra("toko",list.get(position) as Serializable)
           holder.view.lineToko.context.startActivity(i)
       }

    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)


}