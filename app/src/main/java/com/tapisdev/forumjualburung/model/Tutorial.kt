package com.tapisdev.forumjualburung.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Tutorial(
    var idTutorial: String? = "",
    var nama: String? = "",
    var foto: String? = "",
    var tipe: String? = "",
    var urlVideo: String? = "",
    var deskripsi: String? = ""
) : Parcelable, java.io.Serializable