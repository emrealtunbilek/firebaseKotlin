package com.emrealtunbilek.firebasekotlin.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Emre on 3.02.2018.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(p0: RemoteMessage?) {

        var bildirimBaslik=p0?.notification?.title
        var bildirimBody=p0?.notification?.body
        var data=p0?.data


        var baslik=p0?.data?.get("baslik")
        var icerik=p0?.data?.get("icerik")
        var bildirim_turu=p0?.data?.get("bildirim_turu")

        Log.e("FCM", "Başlık : "+baslik+ "İçerik : $icerik" + " Bildirim_turu: $bildirim_turu")


    }
}