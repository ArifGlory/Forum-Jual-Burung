package com.tapisdev.forumjualburung.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Cart(
    var cartId: String? = "",
    var nama: String? = "",
    var harga: Int? = 0,
    var foto: String? = "",
    var deksripsi: String? = "",
    var idAdmin: String? = "",
    var idUser: String? = "",
    var jenis: String? = "",
    var jumlah: Int? = 0,
    var idPesanan: String? = ""
) : Parcelable, java.io.Serializable