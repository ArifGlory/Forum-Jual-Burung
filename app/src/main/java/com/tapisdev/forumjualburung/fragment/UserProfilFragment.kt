package com.tapisdev.forumjualburung.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.makeramen.roundedimageview.RoundedImageView
import com.tapisdev.forumjualburung.MainActivity
import com.tapisdev.forumjualburung.R
import com.tapisdev.forumjualburung.base.BaseFragment
import com.tapisdev.forumjualburung.model.Burung
import com.tapisdev.forumjualburung.model.UserPreference
import com.tapisdev.forumjualburung.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_add_burung.*
import kotlinx.android.synthetic.main.fragment_user_profil.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class UserProfilFragment : BaseFragment(),PermissionHelper.PermissionListener {

    lateinit var rvCatering: RecyclerView
    lateinit var tvLogout : TextView
    lateinit var tvSaveEdit : TextView
    lateinit var tvEnableUpdate : TextView
    lateinit var edEmailAddress : EditText
    lateinit var edUserName : EditText
    lateinit var edMobileNumber : EditText
    lateinit var ivProfile : RoundedImageView
    lateinit var ivGallery : ImageView
    var state = "view"

    var TAG_FOTO = "ubahFoto"
    var urlFoto = "none"
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_user_profil, container, false)
        mUserPref = UserPreference(this.requireActivity())

        tvLogout = root.findViewById(R.id.tvLogout)
        tvSaveEdit = root.findViewById(R.id.tvSaveEdit)
        tvEnableUpdate = root.findViewById(R.id.tvEnableUpdate)
        edUserName = root.findViewById(R.id.edUserName)
        edMobileNumber = root.findViewById(R.id.edMobileNumber)
        ivProfile = root.findViewById(R.id.ivProfile)
        ivGallery = root.findViewById(R.id.ivGallery)

        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(activity)
        permissionHelper.setPermissionListener(this)


        tvLogout.setOnClickListener {
            auth.signOut()
            clearSession()
            val i = Intent(activity, MainActivity::class.java)
            startActivity(i)
        }
        ivGallery.setOnClickListener {
            launchGallery()
        }
        tvEnableUpdate.setOnClickListener {
            state = "edit"
            updateUI()
        }
        tvSaveEdit.setOnClickListener {
            checkValidation()
        }

        updateUI()
        return root
    }

    fun checkValidation(){
        var getUsername = edUserName.text.toString()
        var getPhone = edMobileNumber.text.toString()

        if (getUsername.equals("") || getUsername.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getPhone.equals("") || getPhone.length == 0){
            showErrorMessage("Telepon Belum diisi")
        }
        else {
            updateDataUser(getUsername,getPhone)
        }
    }

    companion object {
        fun newInstance(): UserProfilFragment{
            val fragment = UserProfilFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    fun updateDataUser(name : String,phone : String){
        showLoading(activity)
        userRef.document(auth.currentUser?.uid.toString()).update("name",name)
        userRef.document(auth.currentUser?.uid.toString()).update("phone",phone).addOnCompleteListener { task ->
            dismissLoading()
            if (task.isSuccessful){
                mUserPref.saveName(name)
                mUserPref.savePhone(phone)

                showSuccessMessage("Ubah data berhasil")
                state = "view"
                updateUI()
            }else{
                showLongErrorMessage("terjadi kesalahan : "+task.exception)
                Log.d("ubahProfil","err : "+task.exception)
            }
        }
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
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, filePath)
                fotoBitmap = bitmap
                ivProfile.setImageBitmap(bitmap)
                uploadFoto()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun uploadFoto(){
        showLoading(activity)

        if (fileUri != null){
            Log.d(TAG_FOTO,"uri :"+fileUri.toString())

            val baos = ByteArrayOutputStream()
            fotoBitmap?.compress(Bitmap.CompressFormat.JPEG,50,baos)
            val data: ByteArray = baos.toByteArray()

            val fileReference = storageReference!!.child(System.currentTimeMillis().toString())
            val uploadTask = fileReference.putBytes(data)

            uploadTask.addOnFailureListener {
                    exception -> Log.d(TAG_FOTO, exception.toString())
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
                        Log.d(TAG_FOTO,"download URL : "+ downloadUri.toString())// This is the one you should store
                        urlFoto  = url
                        saveFoto()
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

    fun saveFoto(){
        pDialogLoading.setTitleText("menyimpan data..")
        showInfoMessage("Sedang menyimpan ke database..")
        userRef.document(auth.currentUser?.uid.toString()).update("foto",urlFoto).addOnCompleteListener{
                task ->
            dismissLoading()
            if (task.isSuccessful){
                mUserPref.saveFoto(urlFoto)
                showSuccessMessage("Foto berhasil diubah")
            }else{
                showLongErrorMessage("Penyimpanan data gagal")
                Log.d(TAG_FOTO,"err : "+task.exception)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    fun updateUI(){
        if (state.equals("view")){
            edUserName.setText(mUserPref.getName())
            edMobileNumber.setText(mUserPref.getPhone())
            tvSaveEdit.visibility = View.INVISIBLE

            if (!mUserPref.getFoto().equals("")){
                Glide.with(requireActivity())
                    .load(mUserPref.getFoto())
                    .into(ivProfile)
            }

            edUserName.isEnabled = false
            edMobileNumber.isEnabled = false
            tvSaveEdit.isEnabled = false

            edUserName.background = resources.getDrawable(R.drawable.bg_editfield)
            edMobileNumber.background = resources.getDrawable(R.drawable.bg_editfield)
        }else if (state.equals("edit")){
            edUserName.isEnabled = true
            edMobileNumber.isEnabled = true
            tvSaveEdit.isEnabled = true
            tvSaveEdit.visibility = View.VISIBLE

            edUserName.background = resources.getDrawable(R.drawable.bg_onlycorner_gary)
            edMobileNumber.background = resources.getDrawable(R.drawable.bg_onlycorner_gary)

            edUserName.requestFocus()
        }

    }

    fun clearSession(){
        mUserPref.saveName("")
        mUserPref.saveEmail("")
        mUserPref.saveFoto("")
        mUserPref.saveJenisUser("none")
        mUserPref.savePhone("")
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
