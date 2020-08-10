package com.tapisdev.Tendatenda.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.adapter.AdapterTendaUser
import com.tapisdev.forumjualburung.base.BaseFragment
import com.tapisdev.forumjualburung.model.SharedVariable
import com.tapisdev.forumjualburung.model.Tenda
import kotlinx.android.synthetic.main.fragment_admin_tenda.*

class TabTendaFragment : BaseFragment() {

    lateinit var rvTenda: RecyclerView
    lateinit var fab : FloatingActionButton
    var TAG_GET_Tenda = "getTenda"
    lateinit var adapter: AdapterTendaUser

    var listTenda = ArrayList<Tenda>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_tab_tenda, container, false)
        rvTenda = root.findViewById(R.id.rvTenda)

        adapter = AdapterTendaUser(listTenda)

        rvTenda.setHasFixedSize(true)
        rvTenda.layoutManager = GridLayoutManager(activity, 2)
        rvTenda.adapter = adapter


        getDataMyTenda()
        return root
    }

    companion object {
        fun newInstance(): TabTendaFragment{
            val fragment = TabTendaFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    fun getDataMyTenda(){
        tendaRef.get().addOnSuccessListener { result ->
            listTenda.clear()
            //Log.d(TAG_GET_Tenda," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Tenda, "Datanya : "+document.data)
                var tenda : Tenda = document.toObject(Tenda::class.java)
                tenda.tendaId = document.id
                if (tenda.idAdmin.equals(SharedVariable.selectedIdPenyedia)){
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
            Log.d(TAG_GET_Tenda,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataMyTenda()
    }

}
