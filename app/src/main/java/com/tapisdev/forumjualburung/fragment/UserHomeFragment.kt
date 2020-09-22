package com.tapisdev.forumjualburung.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.makeramen.roundedimageview.RoundedImageView
import com.tapisdev.forumjualburung.MainActivity
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.pengguna.ListBurungUserActivity
import com.tapisdev.forumjualburung.activity.pengguna.ListInformasiUserActivity
import com.tapisdev.forumjualburung.activity.pengguna.ListTokoActivity
import com.tapisdev.forumjualburung.base.BaseFragment
import com.tapisdev.forumjualburung.model.Burung
import com.tapisdev.forumjualburung.model.UserPreference
import com.tapisdev.forumjualburung.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_add_burung.*
import kotlinx.android.synthetic.main.fragment_user_profil.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class UserHomeFragment : BaseFragment(){

    lateinit var rlBurung : RelativeLayout
    lateinit var rlToko : RelativeLayout
    lateinit var rlDiskusi : RelativeLayout
    lateinit var rlInformasi : RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_user_home, container, false)
        mUserPref = UserPreference(this.requireActivity())

        rlBurung = root.findViewById(R.id.rlBurung)
        rlToko = root.findViewById(R.id.rlToko)
        rlDiskusi = root.findViewById(R.id.rlDiskusi)
        rlInformasi = root.findViewById(R.id.rlInformasi)

        rlBurung.setOnClickListener {
            val i = Intent(requireContext(),ListBurungUserActivity::class.java)
            startActivity(i)
        }
        rlToko.setOnClickListener {
            val i = Intent(requireContext(),ListTokoActivity::class.java)
            startActivity(i)
        }
        rlInformasi.setOnClickListener {
            val i = Intent(requireContext(),ListInformasiUserActivity::class.java)
            startActivity(i)
        }



        return root
    }


    companion object {
        fun newInstance(): UserHomeFragment{
            val fragment = UserHomeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onResume() {
        super.onResume()
    }



}
