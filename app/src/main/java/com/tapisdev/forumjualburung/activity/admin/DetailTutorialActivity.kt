package com.tapisdev.forumjualburung.activity.admin

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.activity.ShowVideoActivity
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Tutorial
import com.tapisdev.forumjualburung.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_detail_tutorial.*
import kotlinx.android.synthetic.main.activity_detail_tutorial.*
import kotlinx.android.synthetic.main.activity_detail_tutorial.*
import kotlinx.android.synthetic.main.activity_detail_tutorial.edDeskripsi
import kotlinx.android.synthetic.main.activity_detail_tutorial.edNamaTutorial
import kotlinx.android.synthetic.main.activity_detail_tutorial.edUrlVideo
import kotlinx.android.synthetic.main.activity_detail_tutorial.ivCatering
import kotlinx.android.synthetic.main.activity_detail_tutorial.tvDelete
import kotlinx.android.synthetic.main.activity_detail_tutorial.tvEdit
import kotlinx.android.synthetic.main.activity_detail_tutorial.tvHintFoto
import kotlinx.android.synthetic.main.activity_detail_tutorial.tvSaveEdit
import kotlinx.android.synthetic.main.activity_tambah_tutorial.*
import java.io.ByteArrayOutputStream
import java.util.ArrayList

class DetailTutorialActivity : BaseActivity(),PermissionHelper.PermissionListener {

    lateinit var tutorial : Tutorial
    var state = "view"
    var TAG_DELETE = "deletetutorial"
    var TAG_EDIT = "edittutorial"
    var urlVideo = "none"
    lateinit var i : Intent

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var storageReference: StorageReference? = null
    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tutorial)

        i = intent
        tutorial = i.getSerializableExtra("tutorial") as Tutorial
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
                    tutorialRef.document(tutorial.idTutorial.toString()).delete().addOnSuccessListener {
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
        tvTitleTutorial.setOnClickListener {
            if(tutorial.tipe.equals("Video")){
                val i = Intent(this,ShowVideoActivity::class.java)
                i.putExtra("idVideo",tutorial.urlVideo)
                startActivity(i)
            }
        }


        updateUI()
    }

    fun checkValidation(){
        var getNama = edNamaTutorial.text.toString()
        var getDeskripsi = edDeskripsi.text.toString()
        if (tutorial.tipe.equals("Video")){
            var getUrlVideo = edUrlVideo.text.toString()
            urlVideo = getUrlVideo
        }

        if (getNama.equals("") || getNama.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getDeskripsi.equals("") || getDeskripsi.length == 0){
            showErrorMessage("Deskripsi Belum diisi")
        }else if (tutorial.tipe.equals("Video") && urlVideo.length == 0){
            showErrorMessage("URL Video Belum diisi")
        }else if (fileUri == null) {
            updateDataOnly(getNama,getDeskripsi)
        }
        else {
            uploadAndUpdate(getNama,getDeskripsi)
        }
    }

    fun updateDataOnly(name : String,deskripsi : String){
        showLoading(this)
        if(tutorial.tipe.equals("Video")){
            tutorialRef.document(tutorial.idTutorial.toString()).update("urlVideo",urlVideo)
        }
        tutorialRef.document(tutorial.idTutorial.toString()).update("nama",name)
        tutorialRef.document(tutorial.idTutorial.toString()).update("deskripsi",deskripsi).addOnCompleteListener { task ->
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

                        if(tutorial.tipe.equals("Video")){
                            tutorialRef.document(tutorial.idTutorial.toString()).update("urlVideo",urlVideo)
                        }
                        tutorialRef.document(tutorial.idTutorial.toString()).update("nama",name)
                        tutorialRef.document(tutorial.idTutorial.toString()).update("foto",url)
                        tutorialRef.document(tutorial.idTutorial.toString()).update("deskripsi",deskripsi).addOnCompleteListener { task ->
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
            edNamaTutorial.setText(tutorial.nama)
            edDeskripsi.setText(tutorial.deskripsi)
            tvHintFoto.visibility = View.INVISIBLE
            tvSaveEdit.visibility = View.INVISIBLE

            Glide.with(this)
                .load(tutorial.foto)
                .into(ivCatering)

            edNamaTutorial.isEnabled = false
            edDeskripsi.isEnabled = false
            ivCatering.isEnabled = false
            tvSaveEdit.isEnabled = false

            if(tutorial.tipe.equals("Video")){
                tvTitleTutorial.setText("Lihat Video")
                //showDialog Nampilin Videonya

                edUrlVideo.visibility = View.VISIBLE
                edUrlVideo.isEnabled = false
                edUrlVideo.setText(tutorial.urlVideo)
            }
        }else if (state.equals("edit")){
            tvHintFoto.visibility = View.VISIBLE
            tvSaveEdit.visibility = View.VISIBLE

            edNamaTutorial.isEnabled = true
            edDeskripsi.isEnabled = true
            ivCatering.isEnabled = true
            tvSaveEdit.isEnabled= true

            if(tutorial.tipe.equals("Video")){
                tvTitleTutorial.setText("Lihat Video")
                //showDialog Nampilin Videonya

                edUrlVideo.visibility = View.VISIBLE
                edUrlVideo.isEnabled = true
            }
        }
    }

    private fun launchGallery() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        listPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionHelper.checkAndRequestPermissions(listPermissions)
    }

    override fun onPermissionCheckDone() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
}
