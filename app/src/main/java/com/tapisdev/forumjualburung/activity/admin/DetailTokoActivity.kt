package com.tapisdev.forumjualburung.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
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
import com.tapisdev.forumjualburung.activity.LokasiTokoActivity
import com.tapisdev.forumjualburung.activity.MapsActivity
import com.tapisdev.forumjualburung.base.BaseActivity
import com.tapisdev.forumjualburung.model.SharedVariable
import com.tapisdev.forumjualburung.model.Toko
import com.tapisdev.forumjualburung.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_add_toko.*
import kotlinx.android.synthetic.main.activity_detail_toko.*
import kotlinx.android.synthetic.main.activity_detail_toko.edDeskripsi
import kotlinx.android.synthetic.main.activity_detail_toko.edFullName
import kotlinx.android.synthetic.main.activity_detail_toko.edAlamat
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class DetailTokoActivity : BaseActivity(),PermissionHelper.PermissionListener {

    lateinit var toko : Toko
    var state = "view"
    var TAG_DELETE = "deleteToko"
    var TAG_EDIT = "editToko"
    lateinit var i : Intent
    var alamat = "none"
    var latlon = "none"

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var storageReference: StorageReference? = null
    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_toko)

        i = intent
        toko = i.getSerializableExtra("toko") as Toko
        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        tvSaveEdit.setOnClickListener {
            checkValidation()
        }
        ivToko.setOnClickListener {
            launchGallery()
        }
        tvEditLokasi.setOnClickListener {
            val i = Intent(this, MapsActivity::class.java)
            startActivity(i)
        }
        tvCekLokasi.setOnClickListener {
            val i = Intent(this, LokasiTokoActivity::class.java)
            i.putExtra("latlon",toko.latlon)
            startActivity(i)
        }
        ivBack.setOnClickListener {
            onBackPressed()
        }
        ivListBurung.setOnClickListener {
            val i = Intent(this,AdminListBurungActivity::class.java)
            i.putExtra("tokoId",toko.tokoId)
            startActivity(i)
        }
        ivAddBurung.setOnClickListener {
            val i = Intent(this,AddBurungActivity::class.java)
            i.putExtra("tokoId",toko.tokoId)
            startActivity(i)
        }


        updateUI()
    }

    fun checkValidation(){
        var getName = edFullName.text.toString()
        var getAlamat = edAlamat.text.toString()
        var getDeskripsi = edDeskripsi.text.toString()

        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getAlamat.equals("") || getAlamat.length == 0){
            showErrorMessage("Alamat Belum diisi")
        } else if (getDeskripsi.equals("") || getDeskripsi.length == 0){
            showErrorMessage("Deskripsi Belum diisi")
        }else if (fileUri == null) {
            updateDataOnly(getName,getAlamat,getDeskripsi)
        }else{
            uploadAndUpdate(getName,getAlamat,getDeskripsi)
        }
    }

    private fun getCompleteAddressString(
        LATITUDE: Double,
        LONGITUDE: Double
    ): String? {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? =
                geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.getMaxAddressLineIndex()) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.d("address", strReturnedAddress.toString())
            } else {
                Log.d("address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("address", "Canont get Address!")
        }
        return strAdd
    }

    fun updateDataOnly(name : String,alamat : String,deskripsi : String){
        showLoading(this)
        tokoRef.document(toko.tokoId.toString()).update("nama",name)
        tokoRef.document(toko.tokoId.toString()).update("alamat",alamat)
        tokoRef.document(toko.tokoId.toString()).update("latlon",toko.latlon)
        tokoRef.document(toko.tokoId.toString()).update("deskripsi",deskripsi).addOnCompleteListener { task ->
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

    fun uploadAndUpdate(name : String,alamat : String,deskripsi : String){
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

                        tokoRef.document(toko.tokoId.toString()).update("nama",name)
                        tokoRef.document(toko.tokoId.toString()).update("alamat",alamat)
                        tokoRef.document(toko.tokoId.toString()).update("latlon",toko.latlon)
                        tokoRef.document(toko.tokoId.toString()).update("foto",url)
                        tokoRef.document(toko.tokoId.toString()).update("deskripsi",deskripsi).addOnCompleteListener { task ->
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
            edFullName.setText(toko.nama)
            edAlamat.setText(toko.alamat.toString())
            edDeskripsi.setText(toko.deskripsi)
            edPhone.setText(toko.phone)
            tvHintFoto.visibility = View.INVISIBLE
            tvSaveEdit.visibility = View.INVISIBLE

            Glide.with(this)
                .load(toko.foto)
                .into(ivToko)

            edFullName.isEnabled = false
            edAlamat.isEnabled = false
            edDeskripsi.isEnabled = false
            edPhone.isEnabled = false
            ivToko.isEnabled = false
            tvSaveEdit.isEnabled = false
            tvEditLokasi.isEnabled = false
        }else if (state.equals("edit")){
            tvHintFoto.visibility = View.VISIBLE
            tvSaveEdit.visibility = View.VISIBLE
            tvEditLokasi.visibility = View.VISIBLE

            edFullName.isEnabled = true
            edAlamat.isEnabled = true
            edDeskripsi.isEnabled = true
            edPhone.isEnabled = true
            ivToko.isEnabled = true
            tvSaveEdit.isEnabled= true
            tvEditLokasi.isEnabled= true
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
                ivToko.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (SharedVariable.lokasiToko.latitude != 0.0){
            latlon = ""+ SharedVariable.lokasiToko.latitude+","+ SharedVariable.lokasiToko.longitude
            alamat = getCompleteAddressString(SharedVariable.lokasiToko.latitude, SharedVariable.lokasiToko.longitude).toString()
            edAlamat.setText(alamat)

            toko.latlon = latlon
            toko.alamat = alamat
        }
    }
}
