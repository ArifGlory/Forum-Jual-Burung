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
import com.tapisdev.forumjualburung.activity.admin.AddTokoActivity
import com.tapisdev.forumjualburung.adapter.AdapterToko
import com.tapisdev.forumjualburung.base.BaseFragment
import com.tapisdev.forumjualburung.model.Toko
import kotlinx.android.synthetic.main.fragment_admin_tenda.*

class AdminTokoFragment : BaseFragment() {

    lateinit var rvToko: RecyclerView
    lateinit var fab : FloatingActionButton
    var TAG_GET_CATERING = "getCatering"
    lateinit var adapter:AdapterToko

    var listToko = ArrayList<Toko>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_admin_toko, container, false)
        rvToko = root.findViewById(R.id.rvToko)
        fab = root.findViewById(R.id.fab)

        adapter = AdapterToko(listToko)

        rvToko.setHasFixedSize(true)
        rvToko.layoutManager = LinearLayoutManager(activity)
        rvToko.adapter = adapter

        fab.setOnClickListener {
            val i = Intent(activity,AddTokoActivity::class.java)
            startActivity(i)
        }



        getDataMyToko()
        return root
    }

    companion object {
        fun newInstance(): AdminTokoFragment{
            val fragment = AdminTokoFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    fun getDataMyToko(){
        tokoRef.get().addOnSuccessListener { result ->
            listToko.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
                var toko : Toko = document.toObject(Toko::class.java)
                toko.tokoId = document.id
                listToko.add(toko)
            }
            if (listToko.size == 0){
                animation_view.setAnimation(R.raw.empty_box)
                animation_view.playAnimation()
                animation_view.loop(false)
            }else{
                animation_view.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_CATERING,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataMyToko()
    }

}
