package com.tapisdev.forumjualburung.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import com.google.android.gms.tasks.OnCompleteListener
import com.tapisdev.forumjualburung.MainActivity
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.UserModel
import com.tapisdev.forumjualburung.model.UserPreference
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {

    var selectedJenisUser = "none"
    var TAG_JENIS = "jenisUser"
    var TAG_SIMPAN = "simpanUser"
    lateinit var userModel : UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mUserPref = UserPreference(this)
        selectedJenisUser = "pengguna"

        tvLogin.setOnClickListener {
            val i = Intent(applicationContext,MainActivity::class.java)
            startActivity(i)
        }
        tvSignup.setOnClickListener {
            checkValidation()
        }

    }

    fun checkValidation(){
        var getName = edFullName.text.toString()
        var getEmail = edEmail.text.toString()
        var getPhone = edMobile.text.toString()
        var getPassword = edPassword.text.toString()
        var getConfirmPassword = edCPassword.text.toString()

        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        }else  if (getEmail.equals("") || getEmail.length == 0){
            showErrorMessage("email Belum diisi")
        }else  if (getPhone.equals("") || getPhone.length == 0){
            showErrorMessage("phone Belum diisi")
        }else  if (getPassword.equals("") || getEmail.length == 0){
            showErrorMessage("password Belum diisi")
        }else  if (getConfirmPassword.equals("") || getConfirmPassword.length == 0){
            showErrorMessage("konfirmasi passsword Belum diisi")
        }else if (selectedJenisUser.equals("none")){
            showErrorMessage("Jenis Pengguna Belum dipilih")
        }else if (!getPassword.equals(getConfirmPassword)){
            showErrorMessage("Konfirmasi password tidak valid")
        }else if(getPassword.length < 8 || getConfirmPassword.length < 8){
            showErrorMessage("Password harus lebih dari 8 karakter")
        }
        else{
            userModel = UserModel(getName,getEmail,"",getPhone,selectedJenisUser,"")
            Log.d(TAG_SIMPAN," namanya : "+userModel.name)

            registerUser(userModel.email,getPassword)
        }
    }

    fun registerUser(email : String,pass : String){
        showLoading(this)
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, OnCompleteListener{task ->
            if (task.isSuccessful){
                var userId = auth.currentUser?.uid

                if (userId != null) {
                    userModel.uId = userId
                    userRef.document(userId).set(userModel).addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            dismissLoading()
                            showLongSuccessMessage("Pendaftaran Berhasil, Silakan Login Untuk Melanjutkan")
                            val i = Intent(applicationContext,MainActivity::class.java)
                            startActivity(i)
                        }else{
                            dismissLoading()
                            showLongErrorMessage("Error pendaftaran, coba lagi nanti ")
                            Log.d(TAG_SIMPAN,"err : "+task.exception)
                        }
                    }
                }else{
                    showLongErrorMessage("user id tidak di dapatkan")
                }
            }else{
                dismissLoading()
                if(task.exception?.equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")!!){
                    showLongErrorMessage("Email sudah pernah digunakan ")
                }else{
                    showLongErrorMessage("Error pendaftaran, Cek apakah email sudah pernah digunakan / belum dan  coba lagi nanti ")
                    Log.d(TAG_SIMPAN,"err : "+task.exception)
                }

            }
        })
    }
}

