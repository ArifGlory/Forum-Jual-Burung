package com.tapisdev.forumjualburung.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.admin.TambahTutorialActivity
import com.tapisdev.forumjualburung.adapter.AdapterTutorial
import com.tapisdev.forumjualburung.base.BaseFragment
import com.tapisdev.forumjualburung.model.Tutorial
import java.util.*
import kotlin.collections.ArrayList

class AdminTutorialFragment : BaseFragment() {

    lateinit var rvTutorial: RecyclerView
    lateinit var fab : FloatingActionButton
    var TAG_GET_Tutorial = "getTutorial"
    lateinit var adapter: AdapterTutorial
    lateinit var animation_view : LottieAnimationView

    var listTutorial = ArrayList<Tutorial>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_admin_tutorial, container, false)
        rvTutorial = root.findViewById(R.id.rvTutorial)
        fab = root.findViewById(R.id.fab)
        animation_view = root.findViewById(R.id.animation_view)

        adapter = AdapterTutorial(listTutorial)

        rvTutorial.setHasFixedSize(true)
        rvTutorial.layoutManager = LinearLayoutManager(activity)
        rvTutorial.adapter = adapter

        fab.setOnClickListener {
            val i = Intent(activity, TambahTutorialActivity::class.java)
            startActivity(i)
        }


        getDataMyTutorial() 
        return root
    }

    companion object {
        fun newInstance(): AdminTutorialFragment{
            val fragment = AdminTutorialFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    fun getDataMyTutorial(){
        tutorialRef.get().addOnSuccessListener { result ->
            listTutorial.clear()
            for (document in result){
                Log.d(TAG_GET_Tutorial, "Datanya : "+document.data)

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
        getDataMyTutorial()
    }

}
