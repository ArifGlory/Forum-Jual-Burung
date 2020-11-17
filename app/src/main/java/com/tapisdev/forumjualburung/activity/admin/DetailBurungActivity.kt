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
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.Burung
import com.tapisdev.forumjualburung.model.UserPreference
import com.tapisdev.forumjualburung.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_add_burung.*
import kotlinx.android.synthetic.main.activity_detail_burung.*
import kotlinx.android.synthetic.main.activity_detail_burung.tvDelete
import kotlinx.android.synthetic.main.activity_detail_burung.tvEdit
import kotlinx.android.synthetic.main.activity_detail_burung.tvSaveEdit
import kotlinx.android.synthetic.main.activity_detail_burung.*
import kotlinx.android.synthetic.main.activity_detail_burung.edHarga
import kotlinx.android.synthetic.main.activity_detail_burung.edNamaBurung
import kotlinx.android.synthetic.main.activity_detail_burung.ivBurung
import kotlinx.android.synthetic.main.activity_detail_burung.spKondisi
import kotlinx.android.synthetic.main.activity_detail_burung.tvHintFoto
import kotlinx.android.synthetic.main.activity_detail_informasi.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class DetailBurungActivity : BaseActivity(),PermissionHelper.PermissionListener {

    lateinit var burung : Burung
    var state = "view"
    var TAG_DELETE = "deleteburung"
    var TAG_EDIT = "editburung"
    lateinit var i : Intent
    var selectedKondisi = "none"

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var storageReference: StorageReference? = null
    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_burung)
        mUserPref = UserPreference(this)

        i = intent
        burung = i.getSerializableExtra("burung") as Burung
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
                    burungRef.document(burung.burungId.toString()).delete().addOnSuccessListener {
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
        ivBurung.setOnClickListener {
            launchGallery()
        }
        spKondisi.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                var selected = parent?.getItemAtPosition(position).toString()
                if (selected.equals("Pilih Kondisi")){
                    selectedKondisi = "none"
                }else{
                    selectedKondisi = selected
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


        updateUI()
    }

    fun checkValidation(){
        var getName = edNamaBurung.text.toString()
        var getHarga = edHarga.text.toString()

        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Judul Belum diisi")
        } else if (getHarga.equals("") || getHarga.length == 0){
            showErrorMessage("Harga Belum diisi")
        }else if (fileUri == null) {
            var hrg = Integer.parseInt(getHarga)
            updateDataOnly(getName,hrg)
        }else{
            var hrg = Integer.parseInt(getHarga)
            uploadAndUpdate(getName,hrg)
        }
    }

    fun updateDataOnly(name : String,harga : Int){
        showLoading(this)
        burungRef.document(burung.burungId.toString()).update("nama",name)
        burungRef.document(burung.burungId.toString()).update("kondisi",selectedKondisi)
        burungRef.document(burung.burungId.toString()).update("harga",harga).addOnCompleteListener { task ->
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

    fun uploadAndUpdate(name : String,harga : Int){
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

                        burungRef.document(burung.burungId.toString()).update("nama",name)
                        burungRef.document(burung.burungId.toString()).update("foto",url)
                        burungRef.document(burung.burungId.toString()).update("kondisi",selectedKondisi)
                        burungRef.document(burung.burungId.toString()).update("harga",harga).addOnCompleteListener { task ->
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
            edNamaBurung.setText(burung.nama)
            edHarga.setText("Rp. "+burung.harga)
            tvKondisi.setText("Kondisi Burung "+burung.kondisi)
            selectedKondisi = burung.kondisi.toString()

            tvHintFoto.visibility = View.INVISIBLE
            tvSaveEdit.visibility = View.INVISIBLE
            tvSaveEdit.visibility = View.INVISIBLE
            spKondisi.visibility = View.GONE
            tvKondisi.visibility = View.VISIBLE

            Glide.with(this)
                .load(burung.foto)
                .into(ivBurung)

            edNamaBurung.isEnabled = false
            edHarga.isEnabled = false
            ivBurung.isEnabled = false
            tvSaveEdit.isEnabled = false
            spKondisi.isEnabled = false

            if (!mUserPref.getJenisUser().equals("penjual")){
                tvEdit.visibility = View.INVISIBLE
                tvDelete.visibility = View.INVISIBLE

                tvEdit.isEnabled = false
                tvDelete.isEnabled = false
            }

        }else if (state.equals("edit")){
            tvHintFoto.visibility = View.VISIBLE
            tvSaveEdit.visibility = View.VISIBLE
            spKondisi.visibility = View.VISIBLE
            tvKondisi.visibility = View.GONE


            edNamaBurung.isEnabled = true
            edHarga.isEnabled = true
            ivBurung.isEnabled = true
            tvSaveEdit.isEnabled= true
            spKondisi.isEnabled= true
            edHarga.setText(burung.harga.toString())

            if (burung.kondisi.equals("Sehat")){
                spKondisi.setSelection(1)
            }else if (burung.kondisi.equals("Sakit")){
                spKondisi.setSelection(2)
            }
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
                ivBurung.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
