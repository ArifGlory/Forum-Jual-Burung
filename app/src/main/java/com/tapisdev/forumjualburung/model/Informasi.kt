package com.tapisdev.forumjualburung.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Informasi(
    var informasiId: String? = "",
    var judul: String? = "",
    var foto: String? = "",
    var deskripsi: String? = "",
    var idAdmin: String? = ""
) : Parcelable, java.io.Serializable