package com.emrealtunbilek.firebasekotlin.services

import android.util.Log
import com.emrealtunbilek.firebasekotlin.model.SohbetOdasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import kotlin.collections.HashMap
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.media.RingtoneManager
import android.graphics.BitmapFactory
import android.app.PendingIntent
import android.content.Intent
import android.content.ContentValues.TAG
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.emrealtunbilek.firebasekotlin.MainActivity
import com.emrealtunbilek.firebasekotlin.R
import com.emrealtunbilek.firebasekotlin.SohbetOdasiActivity


/**
 * Created by Emre on 3.02.2018.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    var okunmayiBekleyenMesajSayisi=0


    override fun onMessageReceived(p0: RemoteMessage?) {

        var bildirimBaslik=p0?.notification?.title
        var bildirimBody=p0?.notification?.body
        var data=p0?.data


        var baslik=p0?.data?.get("baslik")
        var icerik=p0?.data?.get("icerik")
        var bildirim_turu=p0?.data?.get("bildirim_turu")
        var sohbet_odasi_id=p0?.data?.get("sohbet_odasi_id")

        Log.e("FCM", "Başlık : "+baslik+ "İçerik : $icerik" + " Bildirim_turu: $bildirim_turu"+ " Secilen sohbet odası:"+sohbet_odasi_id)




    }



}