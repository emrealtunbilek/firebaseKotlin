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

    constructor(to:String, data:Data){
        this.to=to
        this.data=data

    }

    class Data {
        @SerializedName("baslik")
        var baslik: String? = null
        @SerializedName("icerik")
        var icerik: String? = null
        @SerializedName("bildirim_turu")
        var bildirim_turu: String? = null
        @SerializedName("sohbet_odasi_id")
        var sohbet_odasi_id: String? = null


        constructor(baslik:String, icerik:String, bildirim_turu:String, sohbet_odasi_id: String?){
            this.baslik=baslik
            this.icerik=icerik
            this.bildirim_turu=bildirim_turu
            this.sohbet_odasi_id=sohbet_odasi_id
        }
    }
}
