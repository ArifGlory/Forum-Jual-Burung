package com.tapisdev.forumjualburung.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Komentar(
    var komentarId: String? = "",
    var diskusiId: String? = "",
    var isiKomentar: String? = "",
    var nama: String? = "",
    var idPembuat: String? = "",
    var tanggal: String? = "",
    var foto: String? = ""
) : Parcelable, java.io.Serializable