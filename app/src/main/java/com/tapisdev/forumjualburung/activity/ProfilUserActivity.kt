package com.tapisdev.forumjualburung.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.firestore.EventListener
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.UserModel
import kotlinx.android.synthetic.main.activity_profil_user.*

class ProfilUserActivity : BaseActivity() {

    var idUser = ""
    lateinit var i : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_user)

        i = intent
        idUser = i.getStringExtra("iduser")

        ivBack.setOnClickListener {
            onBackPressed()
        }


        getDataUser()
    }

    fun getDataUser(){
        userRef.document(idUser).addSnapshotListener(EventListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot != null && documentSnapshot.exists()){
                userRef.document(idUser).get().addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(UserModel::class.java)

                    tvNamaUser.setText(user?.name)
                    tvEmailUser.setText("Email  : "+user?.email)
                    tvPhoneUser.setText("Telepon  : "+user?.phone)

                    Glide.with(this)
                        .load(user?.foto)
                        .into(ivFotoUser)
                }
            }else{
                Log.d("dataUser", "Current data: null")
            }
        })
    }
}
