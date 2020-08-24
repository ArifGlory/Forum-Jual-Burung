package com.tapisdev.forumjualburung.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Burung(
    var burungId: String? = "",
    var nama: String? = "",
    var harga: Int? = 0,
    var foto: String? = "",
    var kondisi: String? = "",
    var idToko: String? = ""
) : Parcelable, java.io.Serializable