package com.tapisdev.forumjualburung.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.tapisdev.forumjualburung.MainActivity
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.UserPreference
import kotlinx.android.synthetic.main.activity_change_password.*


class ChangePasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        mUserPref = UserPreference(this)
        val user = FirebaseAuth.getInstance().currentUser

        tvSaveChange.setOnClickListener {
            var oldPass = edOldPassword.text.toString()
            var newPass = edNewPassword.text.toString()

            if (oldPass.equals("")){
                showErrorMessage("Passwrod lama belom diisi")
            }else if (newPass.equals("")){
                showErrorMessage("Password baru belum di isi")
            }else if (newPass.length < 7){
                showErrorMessage("Password harus lebih dari 7 karakter")
            }
            else{
                val credential = EmailAuthProvider
                    .getCredential(mUserPref.getEmail().toString(), oldPass)

                showLoading(this)
                user?.reauthenticate(credential)?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        user.updatePassword(newPass).addOnCompleteListener { task ->
                            if (task.isSuccessful()) {
                                dismissLoading()
                                showSuccessMessage("Password berhasil diganti, Silakan login kembali")
                                logout()
                                Log.d("changePass", "Password updated");
                            } else {
                                dismissLoading()
                                showErrorMessage("Ganti Passwword Gagal")
                                Log.d("changePass", "Error password not updated "+task.exception)
                            }
                        }
                    }else{
                        dismissLoading()
                        showErrorMessage("Ganti Passwword Gagal, Periksa kembali password lama anda")
                        Log.d("changePass", "Ganti Passwword Gagal "+task.exception)
                    }
                }

            }
        }

    }

    fun logout(){
        auth.signOut()
        clearSession()
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    fun clearSession(){
        mUserPref.saveName("")
        mUserPref.saveEmail("")
        mUserPref.saveFoto("")
        mUserPref.saveJenisUser("none")
        mUserPref.savePhone("")
    }
}
