package com.emrealtunbilek.firebasekotlin.model

/**
 * Created by Emre on 27.01.2018.
 */

class SohbetMesaj {

    var mesaj: String? = null
    var kullanici_id: String? = null
    var timestamp: String? = null
    var profil_resmi: String? = null
    var adi: String? = null

    constructor() {}

    constructor(mesaj: String, kullanici_id: String, timestamp: String, profil_resmi: String, adi: String) {
        this.mesaj = mesaj
        this.kullanici_id = kullanici_id
        this.timestamp = timestamp
        this.profil_resmi = profil_resmi
        this.adi = adi
    }
}
