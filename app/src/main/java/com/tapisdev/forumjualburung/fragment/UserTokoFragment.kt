package com.tapisdev.forumjualburung.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.pengguna.KeranjangActivity
import com.tapisdev.forumjualburung.adapter.AdapterPenyedia
import com.tapisdev.forumjualburung.adapter.AdapterTokoUser
import com.tapisdev.forumjualburung.base.BaseFragment
import com.tapisdev.forumjualburung.model.Toko
import com.tapisdev.forumjualburung.model.UserModel
import java.util.*
import kotlin.collections.ArrayList

class UserTokoFragment : BaseFragment() {

    lateinit var rvToko: RecyclerView
    lateinit var animation_view : LottieAnimationView
    var TAG_GET_PENYEDIA = "getPenyedia"
    lateinit var adapter:AdapterTokoUser

    var listToko = ArrayList<Toko>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_user_home, container, false)
        rvToko = root.findViewById(R.id.rvToko)
        animation_view = root.findViewById(R.id.animation_view)

        adapter = AdapterTokoUser(listToko)
        rvToko.setHasFixedSize(true)
        rvToko.layoutManager = GridLayoutManager(requireContext(), 2)
        rvToko.adapter = adapter
        

        getDataToko()
        return root
    }

    companion object {
        fun newInstance(): UserTokoFragment{
            val fragment = UserTokoFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    fun getDataToko(){
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
            Collections.reverse(listToko)
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_PENYEDIA,"err : "+exception.message)
        }
    }


    override fun onResume() {
        super.onResume()
        getDataToko()
    }

}
