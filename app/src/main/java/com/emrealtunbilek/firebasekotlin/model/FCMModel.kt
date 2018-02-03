package com.emrealtunbilek.firebasekotlin.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Emre on 3.02.2018.
 */

class FCMModel {

    @SerializedName("to")
    var to: String? = null
    @SerializedName("data")
    var data: Data? = null

    class Data {
        @SerializedName("baslik")
        var baslik: String? = null
        @SerializedName("icerik")
        var icerik: String? = null
        @SerializedName("bildirim_turu")
        var bildirim_turu: String? = null
    }
}
