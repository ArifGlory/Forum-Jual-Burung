package com.tapisdev.forumjualburung.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Catering(
    var cateringId: String? = "",
    var nama: String? = "",
    var harga: Int? = 0,
    var foto: String? = "",
    var deksripsi: String? = "",
    var idAdmin: String? = ""
) : Parcelable, java.io.Serializable