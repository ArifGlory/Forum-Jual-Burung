package com.tapisdev.forumjualburung.base

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.tapisdev.forumjualburung.model.UserPreference
import es.dmoral.toasty.Toasty

open class BaseActivity : AppCompatActivity() {

    lateinit var pDialogLoading : SweetAlertDialog
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var currentUser : FirebaseUser
    lateinit var mUserPref : UserPreference

    val myDB = FirebaseFirestore.getInstance()
    val userRef = myDB.collection("users")
    val cateringRef = myDB.collection("catering")
    val tendaRef = myDB.collection("tenda")
    val settingsRef = myDB.collection("settings")
    val pesanananRef = myDB.collection("pesanan")
    val detailpesananRef = myDB.collection("detail_pesanan")
    val tokoRef = myDB.collection("toko")
    val informasiRef = myDB.collection("informasi")
    val burungRef = myDB.collection("burung")
    val tutorialRef = myDB.collection("tutorial")
    val diskusiRef = myDB.collection("diskusi")
    val komentarRef = myDB.collection("komentar")

    override fun setContentView(view: View?) {
        super.setContentView(view)

        pDialogLoading = SweetAlertDialog(applicationContext, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        var settings = FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()
        myDB.firestoreSettings = settings

    }

    open fun showLoading(mcontext : Context){
        pDialogLoading = SweetAlertDialog(mcontext, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        pDialogLoading.show()
    }

    fun dismissLoading(){
        pDialogLoading.dismiss()
    }

    fun showErrorMessage(message : String){
        applicationContext?.let { Toasty.error(it, message, Toast.LENGTH_SHORT, true).show() }
    }

    fun showSuccessMessage(message : String){
        applicationContext?.let { Toasty.success(it, message, Toast.LENGTH_SHORT, true).show() }
    }

    fun showLongSuccessMessage(message : String){
        applicationContext?.let { Toasty.success(it, message, Toast.LENGTH_LONG, true).show() }
    }

    fun showLongErrorMessage(message : String){
        applicationContext?.let { Toasty.error(it, message, Toast.LENGTH_LONG, true).show() }
    }

    fun showInfoMessage(message : String){
        applicationContext?.let { Toasty.info(it, message, Toast.LENGTH_SHORT, true).show() }
    }

    fun showWarningMessage(message : String){
        applicationContext?.let { Toasty.warning(it, message, Toast.LENGTH_SHORT, true).show() }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            currentUser = auth.currentUser!!
        }
    }
}