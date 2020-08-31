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
import com.tapisdev.forumjualburung.adapter.AdapterTutorial
import com.tapisdev.forumjualburung.adapter.AdapterTokoUser
import com.tapisdev.forumjualburung.base.BaseFragment
import com.tapisdev.forumjualburung.model.Tutorial
import com.tapisdev.forumjualburung.model.Toko
import java.util.*
import kotlin.collections.ArrayList

class UserTutorialFragment : BaseFragment() {

    lateinit var rvTutorial: RecyclerView
    lateinit var animation_view : LottieAnimationView
    var TAG_GET_Tutorial = "getTutorial"
    lateinit var adapter:AdapterTutorial

    var listTutorial = ArrayList<Tutorial>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_user_tutorial, container, false)
        rvTutorial = root.findViewById(R.id.rvTutorial)
        animation_view = root.findViewById(R.id.animation_view)

        adapter = AdapterTutorial(listTutorial)
        rvTutorial.setHasFixedSize(true)
        rvTutorial.layoutManager = GridLayoutManager(requireContext(), 2)
        rvTutorial.adapter = adapter
        

        getDataTutorial()
        return root
    }

    companion object {
        fun newInstance(): UserTutorialFragment{
            val fragment = UserTutorialFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    fun getDataTutorial(){
        tutorialRef.get().addOnSuccessListener { result ->
            listTutorial.clear()
            //Log.d(TAG_GET_CATERING," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_CATERING, "Datanya : "+document.data)
                var tutorial : Tutorial = document.toObject(Tutorial::class.java)
                tutorial.idTutorial = document.id
                listTutorial.add(tutorial)
            }
            if (listTutorial.size == 0){
                animation_view.setAnimation(R.raw.empty_box)
                animation_view.playAnimation()
                animation_view.loop(false)
            }else{
                animation_view.visibility = View.INVISIBLE
            }
            Collections.reverse(listTutorial)
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_Tutorial,"err : "+exception.message)
        }
    }


    override fun onResume() {
        super.onResume()
        getDataTutorial()
    }

}
