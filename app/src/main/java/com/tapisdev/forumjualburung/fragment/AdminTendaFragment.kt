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
import com.tapisdev.forumjualburung.activity.admin.AddTendaActivity
import com.tapisdev.forumjualburung.adapter.AdapterTenda
import com.tapisdev.forumjualburung.base.BaseFragment
import com.tapisdev.forumjualburung.model.Tenda
import kotlinx.android.synthetic.main.fragment_admin_tenda.*

class AdminTendaFragment : BaseFragment() {

    lateinit var rvTenda: RecyclerView
    lateinit var fab : FloatingActionButton
    var TAG_GET_TENDA = "getTenda"
    lateinit var adapter: AdapterTenda

    var listTenda = ArrayList<Tenda>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_admin_tenda, container, false)
        rvTenda = root.findViewById(R.id.rvTenda)
        fab = root.findViewById(R.id.fab)

        adapter = AdapterTenda(listTenda)

        rvTenda.setHasFixedSize(true)
        rvTenda.layoutManager = LinearLayoutManager(activity)
        rvTenda.adapter = adapter

        fab.setOnClickListener {
            val i = Intent(activity, AddTendaActivity::class.java)
            startActivity(i)
        }


        getDataMyTenda()
        return root
    }

    companion object {
        fun newInstance(): AdminTendaFragment{
            val fragment = AdminTendaFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    fun getDataMyTenda(){
        tendaRef.get().addOnSuccessListener { result ->
            listTenda.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                Log.d(TAG_GET_TENDA, "Datanya : "+document.data)

                var tenda : Tenda = document.toObject(Tenda::class.java)
                tenda.tendaId = document.id
                if (tenda.idAdmin.equals(auth.currentUser?.uid)){
                    listTenda.add(tenda)
                }
            }
            if (listTenda.size == 0){
                animation_view.setAnimation(R.raw.empty_box)
                animation_view.playAnimation()
                animation_view.loop(false)
            }else{
                animation_view.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_TENDA,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataMyTenda()
    }

}
