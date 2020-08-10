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
import com.tapisdev.forumjualburung.activity.admin.AddCateringActivity
import com.tapisdev.forumjualburung.adapter.AdapterCatering
import com.tapisdev.forumjualburung.base.BaseFragment
import com.tapisdev.forumjualburung.model.Catering
import kotlinx.android.synthetic.main.fragment_admin_tenda.*

class AdminCateringFragment : BaseFragment() {

    lateinit var rvCatering: RecyclerView
    lateinit var fab : FloatingActionButton
    var TAG_GET_CATERING = "getCatering"
    lateinit var adapter:AdapterCatering

    var listCatering = ArrayList<Catering>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_admin_catering, container, false)
        rvCatering = root.findViewById(R.id.rvCatering)
        fab = root.findViewById(R.id.fab)

        adapter = AdapterCatering(listCatering)

        rvCatering.setHasFixedSize(true)
        rvCatering.layoutManager = LinearLayoutManager(activity)
        rvCatering.adapter = adapter

        fab.setOnClickListener {
            val i = Intent(activity,AddCateringActivity::class.java)
            startActivity(i)
        }



        getDataMyCatering()
        return root
    }

    companion object {
        fun newInstance(): AdminCateringFragment{
            val fragment = AdminCateringFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    fun getDataMyCatering(){
        cateringRef.get().addOnSuccessListener { result ->
            listCatering.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
                var catering : Catering = document.toObject(Catering::class.java)
                catering.cateringId = document.id
                if (catering.idAdmin.equals(auth.currentUser?.uid)){
                    listCatering.add(catering)
                }
            }
            if (listCatering.size == 0){
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
        getDataMyCatering()
    }

}
