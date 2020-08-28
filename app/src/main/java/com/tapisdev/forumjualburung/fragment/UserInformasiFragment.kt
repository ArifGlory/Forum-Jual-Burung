package com.tapisdev.forumjualburung.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.adapter.AdapterInformasi
import com.tapisdev.forumjualburung.adapter.AdapterTokoUser
import com.tapisdev.forumjualburung.base.BaseFragment
import com.tapisdev.forumjualburung.model.Informasi
import com.tapisdev.forumjualburung.model.Toko
import java.util.*
import kotlin.collections.ArrayList

class UserInformasiFragment : BaseFragment() {

    lateinit var rvInformasi: RecyclerView
    lateinit var animation_view : LottieAnimationView
    var TAG_GET_INFORMASI = "getInformasi"
    lateinit var adapter:AdapterInformasi

    var listInformasi = ArrayList<Informasi>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_user_informasi, container, false)
        rvInformasi = root.findViewById(R.id.rvInformasi)
        animation_view = root.findViewById(R.id.animation_view)

        adapter = AdapterInformasi(listInformasi)
        rvInformasi.setHasFixedSize(true)
        rvInformasi.layoutManager = GridLayoutManager(requireContext(), 2)
        rvInformasi.adapter = adapter
        

        getDataToko()
        return root
    }

    companion object {
        fun newInstance(): UserInformasiFragment{
            val fragment = UserInformasiFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    fun getDataToko(){
        informasiRef.get().addOnSuccessListener { result ->
            listInformasi.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
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
        getDataToko()
    }

}
