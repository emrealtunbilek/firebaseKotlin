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

        Log.e("FCM", "Başlık : "+bildirimBaslik+ "Body : $bildirimBody" + " Data: $data")


    }
}