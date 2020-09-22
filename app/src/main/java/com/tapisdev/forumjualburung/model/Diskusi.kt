package com.tapisdev.forumjualburung.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Diskusi(
    var diskusiId: String? = "",
    var judul: String? = "",
    var deskripsi: String? = "",
    var idPembuat: String? = "",
    var tanggal: String? = "",
    var foto: String? = "",
    var tipePembuat: String? = "",
    var namaPembuat: String? = ""
) : Parcelable, java.io.Serializable