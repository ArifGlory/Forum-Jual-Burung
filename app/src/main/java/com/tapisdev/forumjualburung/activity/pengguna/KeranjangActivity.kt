package com.tapisdev.forumjualburung.activity.pengguna

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.adapter.AdapterKeranjang
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.SharedVariable
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.android.synthetic.main.activity_keranjang.animation_view

class KeranjangActivity : BaseActivity() {

    lateinit var adapter: AdapterKeranjang

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)

        adapter = AdapterKeranjang(SharedVariable.listCart)

        rvKeranjang.setHasFixedSize(true)
        rvKeranjang.layoutManager = LinearLayoutManager(this)
        rvKeranjang.adapter = adapter

        if (SharedVariable.listCart.size == 0){
            animation_view.setAnimation(R.raw.empty_box)
            animation_view.playAnimation()
            animation_view.loop(false)
        }else{
            animation_view.visibility = View.INVISIBLE
            adapter.notifyDataSetChanged()
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
        tvGoToMakeOrder.setOnClickListener {
            val i = Intent(applicationContext, KonfirmasiPesanActivity::class.java)
            startActivity(i)
        }
        countTotal()
    }

    fun countTotal(){
        if(SharedVariable.listCart.size == 0){
            tvPriceTotal.setText("Total : Rp. 0")
            tvPriceTotal.visibility = View.INVISIBLE
            tvGoToMakeOrder.visibility = View.INVISIBLE
        }else{
            var total = 0;
            for (i in 0 until SharedVariable.listCart.size){
                Log.d("isiCart",""+SharedVariable.listCart.get(i).toString())
                var subtotal = SharedVariable.listCart.get(i).harga?.times(SharedVariable.listCart.get(i).jumlah!!)
                if (subtotal != null) {
                    total += subtotal
                }
            }
            SharedVariable.totalKeranjang = total
            tvPriceTotal.setText("Total : Rp. "+total)
            tvGoToMakeOrder.visibility = View.VISIBLE
        }
    }

    fun refreshList(){
        adapter.notifyDataSetChanged()
        countTotal()
    }
}
