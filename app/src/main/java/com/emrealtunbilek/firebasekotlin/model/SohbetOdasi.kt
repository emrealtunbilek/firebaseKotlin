package com.emrealtunbilek.firebasekotlin.model

/**
 * Created by Emre on 27.01.2018.
 */

class SohbetOdasi {

    var sohbetodasi_adi: String? = null
    var olusturan_id: String? = null
    var seviye: String? = null
    var sohbetodasi_id: String? = null
    var sohbet_odasi_mesajlari:List<SohbetMesaj>? = null

    constructor() {}

    constructor(sohbetodasi_adi: String, olusturan_id: String, seviye: String, sohbetodasi_id: String, sohbet_odasi_mesajlari:List<SohbetMesaj>) {
        this.sohbetodasi_adi = sohbetodasi_adi
        this.olusturan_id = olusturan_id
        this.seviye = seviye
        this.sohbetodasi_id = sohbetodasi_id
        this.sohbet_odasi_mesajlari=sohbet_odasi_mesajlari
    }
}
