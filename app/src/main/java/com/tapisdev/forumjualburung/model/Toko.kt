package com.tapisdev.forumjualburung.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Toko(
    var tokoId: String? = "",
    var nama: String? = "",
    var foto: String? = "",
    var deskripsi: String? = "",
    var alamat: String? = "",
    var latlon: String? = ""
) : Parcelable, java.io.Serializable