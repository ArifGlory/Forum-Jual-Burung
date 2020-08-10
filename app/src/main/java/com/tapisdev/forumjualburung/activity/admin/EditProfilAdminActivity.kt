package com.tapisdev.forumjualburung.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.UserPreference
import com.tapisdev.forumjualburung.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_add_catering.edFullName
import kotlinx.android.synthetic.main.activity_add_catering.ivCatering
import kotlinx.android.synthetic.main.activity_edit_profil_admin.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class EditProfilAdminActivity : BaseActivity(),PermissionHelper.PermissionListener {

    var TAG_EDIT = "ubahProfil"
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil_admin)

        storageReference = FirebaseStorage.getInstance().reference.child("images")
        mUserPref = UserPreference(this)

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        ivCatering.setOnClickListener {
            launchGallery()
        }
        tvUpdate.setOnClickListener {
            checkValidation()
        }

        updateUI()
    }

    fun updateUI(){
        edFullName.setText(mUserPref.getName())
        edPhone.setText(mUserPref.getPhone())
        if (mUserPref.getFoto().equals("")){
            ivCatering.setImageResource(R.drawable.ic_placeholder)
        }else{
            Glide.with(this)
                .load(mUserPref.getFoto())
                .into(ivCatering)
        }
    }

    override fun onPermissionCheckDone() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    fun checkValidation(){
        var getName = edFullName.text.toString()
        var getPhone = edPhone.text.toString()

        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getPhone.equals("") || getPhone.length == 0){
            showErrorMessage("Harga Belum diisi")
        } else if (fileUri == null) {
            updateDataOnly(getName,getPhone)
        }else {
            uploadAndUpdate(getName,getPhone)
        }
    }

    fun updateDataOnly(name : String,phone : String){
        showLoading(this)
        userRef.document(auth.currentUser?.uid.toString()).update("name",name)
        userRef.document(auth.currentUser?.uid.toString()).update("phone",phone).addOnCompleteListener { task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Ubah data berhasil")
                mUserPref.saveName(name)
                mUserPref.savePhone(phone)
                onBackPressed()
            }else{
                showErrorMessage("terjadi kesalahan : "+task.exception)
                Log.d(TAG_EDIT,"err : "+task.exception)
            }
        }
    }

    fun uploadAndUpdate(name : String,phone : String){
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

                        userRef.document(auth.currentUser?.uid.toString()).update("name",name)
                        userRef.document(auth.currentUser?.uid.toString()).update("phone",name)
                        userRef.document(auth.currentUser?.uid.toString()).update("foto",url).addOnCompleteListener { task ->
                            dismissLoading()
                            if (task.isSuccessful){
                                showSuccessMessage("Ubah data berhasil")
                                mUserPref.saveName(name)
                                mUserPref.savePhone(phone)
                                mUserPref.saveFoto(url)
                                onBackPressed()
                            }else{
                                showErrorMessage("terjadi kesalahan : "+task.exception)
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
