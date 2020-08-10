package com.tapisdev.forumjualburung.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Tenda
import com.tapisdev.forumjualburung.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_add_catering.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class AddTendaActivity : BaseActivity(),PermissionHelper.PermissionListener {

    var TAG_SIMPAN = "simpanTenda"
    lateinit var tendaModel : Tenda

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tenda)

        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        ivCatering.setOnClickListener {
            launchGallery()
        }
        tvAdd.setOnClickListener {
            checkValidation()
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
                ivCatering.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun checkValidation(){
        var getName = edFullName.text.toString()
        var getHarga = edHarga.text.toString()
        var getDeskripsi = edDeskripsi.text.toString()

        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getHarga.equals("") || getHarga.length == 0){
            showErrorMessage("Harga Belum diisi")
        } else if (getDeskripsi.equals("") || getDeskripsi.length == 0){
            showErrorMessage("Deskripsi Belum diisi")
        }else {
            var harga = Integer.parseInt(getHarga)
            tendaModel = Tenda("",
                getName,
                harga,
                "",
                getDeskripsi,
                auth.currentUser?.uid
            )
            uploadTenda()
        }
    }

    fun uploadTenda(){
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
                        tendaModel.foto = url
                        saveTenda()
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

    fun saveTenda(){
        pDialogLoading.setTitleText("menyimpan data..")
        showInfoMessage("Sedang menyimpan ke database..")
        tendaRef.document().set(tendaModel).addOnCompleteListener{
                task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Data tenda berhasil ditambahkan")
                onBackPressed()
            }else{
                showLongErrorMessage("Penyimpanan data gagal")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
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
