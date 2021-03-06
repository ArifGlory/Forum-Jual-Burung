package com.tapisdev.forumjualburung.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Informasi
import com.tapisdev.forumjualburung.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_detail_informasi.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class DetailInformasiActivity : BaseActivity(), PermissionHelper.PermissionListener {

    lateinit var informasi : Informasi
    var state = "view"
    var TAG_DELETE = "deleteinformasi"
    var TAG_EDIT = "editinformasi"
    lateinit var i : Intent

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var storageReference: StorageReference? = null
    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_informasi)

        i = intent
                informasi = i.getSerializableExtra("informasi") as Informasi
        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        tvEdit.setOnClickListener {
            state = "edit"
            updateUI()
        }
        tvDelete.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin menghapus ini ?")
                .setContentText("Data yang sudah dihapus tidak bisa dikembalikan")
                .setConfirmText("Ya")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    showLoading(this)
                    informasiRef.document(informasi.informasiId.toString()).delete().addOnSuccessListener {
                        dismissLoading()
                        showSuccessMessage("Data berhasil dihapus")
                        onBackPressed()
                        Log.d("deleteDoc", "DocumentSnapshot successfully deleted!")
                    }.addOnFailureListener {
                            e ->
                        dismissLoading()
                        showErrorMessage("terjadi kesalahan "+e)
                        Log.w("deleteDoc", "Error deleting document", e)
                    }

                }
                .setCancelButton(
                    "Tidak"
                ) { sDialog -> sDialog.dismissWithAnimation() }
                .show()
        }
        tvSaveEdit.setOnClickListener {
            checkValidation()
        }
        ivCatering.setOnClickListener {
            launchGallery()
        }


        updateUI()
    }

    fun checkValidation(){
        var getName = edTitle.text.toString()
        var getDeskripsi = edDeskripsi.text.toString()

        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Judul Belum diisi")
        } else if (getDeskripsi.equals("") || getDeskripsi.length == 0){
            showErrorMessage("Deskripsi Belum diisi")
        }else if (fileUri == null) {
            updateDataOnly(getName,getDeskripsi)
        }else{
            uploadAndUpdate(getName,getDeskripsi)
        }
    }

    fun updateDataOnly(name : String,deskripsi : String){
        showLoading(this)
        informasiRef.document(informasi.informasiId.toString()).update("judul",name)
        informasiRef.document(informasi.informasiId.toString()).update("deskripsi",deskripsi).addOnCompleteListener { task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Ubah data berhasil")
                onBackPressed()
            }else{
                showLongErrorMessage("terjadi kesalahan : "+task.exception)
                Log.d(TAG_EDIT,"err : "+task.exception)
            }
        }
    }

    fun uploadAndUpdate(name : String,deskripsi : String){
        showLoading(this)
        if (fileUri != null){
            val baos = ByteArrayOutputStream()
            fotoBitmap?.compress(Bitmap.CompressFormat.JPEG,50,baos)
            val data: ByteArray = baos.toByteArray()

            val fileReference = storageReference!!.child(System.currentTimeMillis().toString())
            val uploadTask = fileReference.putBytes(data)

            uploadTask.addOnFailureListener {
                    exception -> Log.d(TAG_EDIT, exception.toString())
            }.addOnSuccessListener {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                showSuccessMessage("Image Berhasil di upload")
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                    }

                    fileReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val url = downloadUri!!.toString()
                        Log.d(TAG_EDIT,"download URL : "+ downloadUri.toString())// This is the one you should store

                        informasiRef.document(informasi.informasiId.toString()).update("judul",name)
                        informasiRef.document(informasi.informasiId.toString()).update("foto",url)
                        informasiRef.document(informasi.informasiId.toString()).update("deskripsi",deskripsi).addOnCompleteListener { task ->
                            dismissLoading()
                            if (task.isSuccessful){
                                showSuccessMessage("Ubah data berhasil")
                                onBackPressed()
                            }else{
                                showLongErrorMessage("terjadi kesalahan : "+task.exception)
                                Log.d(TAG_EDIT,"err : "+task.exception)
                            }
                        }

                    } else {
                        dismissLoading()
                        showErrorMessage("Terjadi kesalahan, coba lagi nanti")
                    }
                }
            }.addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                pDialogLoading.setTitleText("Uploaded " + progress.toInt() + "%...")
            }
        }else{
            dismissLoading()
            showErrorMessage("Belum memilih gambar")
        }
    }

    fun updateUI(){
        if (state.equals("view")){
            edTitle.setText(informasi.judul)
            edDeskripsi.setText(informasi.deskripsi)
            tvHintFoto.visibility = View.INVISIBLE
            tvSaveEdit.visibility = View.INVISIBLE

            Glide.with(this)
                .load(informasi.foto)
                .into(ivCatering)

            edTitle.isEnabled = false
            edDeskripsi.isEnabled = false
            ivCatering.isEnabled = false
            tvSaveEdit.isEnabled = false
        }else if (state.equals("edit")){
            tvHintFoto.visibility = View.VISIBLE
            tvSaveEdit.visibility = View.VISIBLE

            edTitle.isEnabled = true
            edDeskripsi.isEnabled = true
            ivCatering.isEnabled = true
            tvSaveEdit.isEnabled= true
        }
    }

    override fun onPermissionCheckDone() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun launchGallery() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        listPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionHelper.checkAndRequestPermissions(listPermissions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            fileUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                fotoBitmap = bitmap
                ivCatering.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
