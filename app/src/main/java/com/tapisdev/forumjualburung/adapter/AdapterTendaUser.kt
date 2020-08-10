package com.tapisdev.forumjualburung.adapter

import android.app.Dialog
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
import com.tapisdev.forumjualburung.model.Cart
import com.tapisdev.forumjualburung.model.SharedVariable
import com.tapisdev.forumjualburung.model.Tenda
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_catering_user.view.ivCatering
import kotlinx.android.synthetic.main.row_catering_user.view.tvName
import kotlinx.android.synthetic.main.row_catering_user.view.tvPrice
import kotlinx.android.synthetic.main.row_tenda_user.view.*
import java.util.*
import kotlin.collections.ArrayList

class AdapterTendaUser(private val list:ArrayList<Tenda>) : RecyclerView.Adapter<AdapterTendaUser.Holder>(){
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_tenda_user,parent,false))
    }

    override fun getItemCount(): Int = list?.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.view.tvName.text = list?.get(position)?.nama
        holder.view.tvPrice.text = "Rp. "+list?.get(position)?.harga

        Glide.with(holder.view.ivCatering.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivCatering)

        holder.view.lineTendaUser.setOnClickListener {
            showDialog(holder,position)
        }


    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

    private fun showDialog(holder: AdapterTendaUser.Holder, position: Int) {
        val dialog = Dialog(holder.view.ivCatering.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dlg_confomation)
        val edJumlah = dialog.findViewById(R.id.edJumlah) as EditText
        val tvAdd = dialog.findViewById(R.id.tvAdd) as TextView
        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView


        tvAdd.setOnClickListener {
            if (edJumlah.text.toString().equals("") || edJumlah.text.toString().length == 0){
                holder.view.ivCatering.context?.let { Toasty.error(it, "Anda belum memasukkan jumlah pemesanan", Toast.LENGTH_SHORT, true).show() }
            }else{
                var jml = Integer.parseInt(edJumlah.text.toString())
                var cart : Cart = Cart(
                    UUID.randomUUID().toString(),
                    list?.get(position)?.nama,
                    list?.get(position)?.harga,
                    list?.get(position)?.foto,
                    list?.get(position)?.deksripsi,
                    list?.get(position)?.idAdmin,
                    auth.currentUser?.uid,
                    "tenda",
                    jml
                )
                SharedVariable.listCart.add(cart)
                SharedVariable.IdPenyediaCart = list.get(position).idAdmin.toString()
                dialog.dismiss()
                holder.view.ivCatering.context?.let { Toasty.success(it, "Berhasil menambahkan kedalam keranjang", Toast.LENGTH_SHORT, true).show() }
            }
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

}