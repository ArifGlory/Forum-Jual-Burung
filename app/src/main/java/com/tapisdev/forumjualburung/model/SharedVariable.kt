package com.tapisdev.forumjualburung.model

import com.google.android.gms.maps.model.LatLng

class SharedVariable {
    //open var selectedIdPenyedia = "-"

    companion object {
        var selectedIdPenyedia: String = "-"
        var IdPenyediaCart: String = "-"
        var totalKeranjang : Int = 0
        var listCart = ArrayList<Cart>()
        var lokasiToko  = LatLng(0.0,0.0)
    }
}