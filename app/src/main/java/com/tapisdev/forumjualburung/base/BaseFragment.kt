package com.tapisdev.forumjualburung.base

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tapisdev.forumjualburung.model.UserPreference
import es.dmoral.toasty.Toasty

open class BaseFragment : Fragment() {

    lateinit var pDialogLoading : SweetAlertDialog
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var currentUser : FirebaseUser
    lateinit var mUserPref : UserPreference

    val myDB = FirebaseFirestore.getInstance()

    val userRef = myDB.collection("users")
    val tokoRef = myDB.collection("toko")
    val cateringRef = myDB.collection("catering")
    val tendaRef = myDB.collection("tenda")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pDialogLoading = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        /*var settings = FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()
        myDB.firestoreSettings = settings*/

    }

    open fun showLoading(mcontext: Activity?){
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
       /* SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText(message)
            .show()*/
        activity?.let { Toasty.error(it, message, Toast.LENGTH_SHORT, true).show() }
    }

    fun showLongErrorMessage(message : String){
        activity?.let { Toasty.error(it, message, Toast.LENGTH_LONG, true).show() }
    }

    fun showSuccessMessage(message : String){
        activity?.let { Toasty.success(it, message, Toast.LENGTH_LONG, true).show() }
    }

    fun showInfoMessage(message : String){
        activity?.let { Toasty.info(it, message, Toast.LENGTH_SHORT, true).show() }
    }

    fun showWarningMessage(message : String){
        activity?.let { Toasty.warning(it, message, Toast.LENGTH_SHORT, true).show() }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            currentUser = auth.currentUser!!
        }
    }
}

