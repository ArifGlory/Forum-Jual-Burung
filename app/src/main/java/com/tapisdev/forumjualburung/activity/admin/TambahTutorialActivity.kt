package com.tapisdev.forumjualburung.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Tutorial
import com.tapisdev.forumjualburung.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_tambah_tutorial.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class TambahTutorialActivity : BaseActivity(),PermissionHelper.PermissionListener {

    var TAG_SIMPAN = "simpanTutorial"
    lateinit var tutorial: Tutorial
    var selectedTipe = "none"
    lateinit var tokoId : String

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_tutorial)
        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        spTipeTutorial.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                var selected = parent?.getItemAtPosition(position).toString()
                if (selected.equals("Pilih Tipe Tutorial")){
                    selectedTipe = "none"
                }else{
                    selectedTipe = selected
                    if (selectedTipe.equals("Video")){
                        edUrlVideo.visibility = View.VISIBLE
                    }else if (selected.equals("Text")){
                        edUrlVideo.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        ivTutorial.setOnClickListener {
            launchGallery()
        }
        tvAddTutorial.setOnClickListener {
            checkValidation()
        }
    }

    fun checkValidation(){
        var getNama = edNamaTutorial.text.toString()
        var getDeskripsi = edDeskripsi.text.toString()
        var getUrlVideo = edUrlVideo.text.toString()

        if (getNama.equals("") || getNama.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getDeskripsi.equals("") || getDeskripsi.length == 0){
            showErrorMessage("Deskripsi Belum diisi")
        }else if (fileUri == null){
            showErrorMessage("Gambar belum dipilih")
        }else if (selectedTipe.equals("none")){
            showErrorMessage("Tipe Tutorial belum dipilih")
        }else if (selectedTipe.equals("Video") && getUrlVideo.length == 0){
            showErrorMessage("URl Video belum diisi")
        }
        else {
            tutorial = Tutorial(
                "",
                getNama,
                "",
                selectedTipe,
                getUrlVideo,
                getDeskripsi
            )
            uploadTutorial()
        }
    }

    fun uploadTutorial(){
        showLoading(this)

        if (fileUri != null){
            Log.d(TAG_SIMPAN,"uri :"+fileUri.toString())

            val baos = ByteArrayOutputStream()
            fotoBitmap?.compress(Bitmap.CompressFormat.JPEG,50,baos)
            val data: ByteArray = baos.toByteArray()

            val fileReference = storageReference!!.child(System.currentTimeMillis().toString())
            val uploadTask = fileReference.putBytes(data)

            uploadTask.addOnFailureListener {
                    exception -> Log.d(TAG_SIMPAN, exception.toString())
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

                        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAu.getInstance().getCurrentUser().getUid());
                        val url = downloadUri!!.toString()
                        Log.d(TAG_SIMPAN,"download URL : "+ downloadUri.toString())// This is the one you should store
                        tutorial.foto = url
                        saveTutorial()
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
            showErrorMessage("Anda belum memilih file")
        }

    }

    fun saveTutorial(){
        pDialogLoading.setTitleText("menyimpan data..")
        showInfoMessage("Sedang menyimpan ke database..")
        tutorialRef.document().set(tutorial).addOnCompleteListener{
                task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Data  berhasil ditambahkan")
                onBackPressed()
            }else{
                showLongErrorMessage("Penyimpanan data gagal")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
            }
        }
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
                ivTutorial.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPermissionCheckDone() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
}
