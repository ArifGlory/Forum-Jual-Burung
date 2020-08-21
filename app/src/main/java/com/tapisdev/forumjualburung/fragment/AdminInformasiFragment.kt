package com.tapisdev.forumjualburung.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.admin.AddInformasiActivity
import com.tapisdev.forumjualburung.adapter.AdapterInformasi
import com.tapisdev.forumjualburung.base.BaseFragment
import com.tapisdev.forumjualburung.model.Informasi
import kotlinx.android.synthetic.main.fragment_admin_informasi.*
import java.util.*
import kotlin.collections.ArrayList

class AdminInformasiFragment : BaseFragment() {

    lateinit var rvInformasi: RecyclerView
    lateinit var fab : FloatingActionButton
    var TAG_GET_INFORMASI = "getInformasi"
    lateinit var adapter: AdapterInformasi

    var listInformasi = ArrayList<Informasi>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_admin_informasi, container, false)
        rvInformasi = root.findViewById(R.id.rvInformasi)
        fab = root.findViewById(R.id.fab)

        adapter = AdapterInformasi(listInformasi)

        rvInformasi.setHasFixedSize(true)
        rvInformasi.layoutManager = LinearLayoutManager(activity)
        rvInformasi.adapter = adapter

        fab.setOnClickListener {
            val i = Intent(activity, AddInformasiActivity::class.java)
            startActivity(i)
        }


        getDataMyInformasi()
        return root
    }

    companion object {
        fun newInstance(): AdminInformasiFragment{
            val fragment = AdminInformasiFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    fun getDataMyInformasi(){
        informasiRef.get().addOnSuccessListener { result ->
            listInformasi.clear()
            for (document in result){
                Log.d(TAG_GET_INFORMASI, "Datanya : "+document.data)

                var informasi : Informasi = document.toObject(Informasi::class.java)
                informasi.informasiId = document.id
                listInformasi.add(informasi)
            }
            if (listInformasi.size == 0){
                animation_view.setAnimation(R.raw.empty_box)
                animation_view.playAnimation()
                animation_view.loop(false)
            }else{
                animation_view.visibility = View.INVISIBLE
            }
            Collections.reverse(listInformasi)
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_INFORMASI,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataMyInformasi()
    }

}
