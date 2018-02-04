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
import android.graphics.Bitmap
import android.support.v4.app.NotificationCompat
import com.emrealtunbilek.firebasekotlin.MainActivity
import com.emrealtunbilek.firebasekotlin.R
import com.emrealtunbilek.firebasekotlin.SohbetOdasiActivity


/**
 * Created by Emre on 3.02.2018.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    var okunmayiBekleyenMesajSayisi = 0


    override fun onMessageReceived(p0: RemoteMessage?) {


        if (!activityKontrolEt()) {
            Log.e("TTT", "activity acık mı:" + activityKontrolEt())
            var bildirimBaslik = p0?.notification?.title
            var bildirimBody = p0?.notification?.body
            var data = p0?.data


            var baslik = p0?.data?.get("baslik")
            var icerik = p0?.data?.get("icerik")
            var bildirim_turu = p0?.data?.get("bildirim_turu")
            var sohbet_odasi_id = p0?.data?.get("sohbet_odasi_id")

            Log.e("FCM", "Başlık : " + baslik + "İçerik : $icerik" + " Bildirim_turu: $bildirim_turu" + " Secilen sohbet odası:" + sohbet_odasi_id)

            var ref = FirebaseDatabase.getInstance().reference
                    .child("sohbet_odasi")
                    .orderByKey()
                    .equalTo(sohbet_odasi_id)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            var tekSohbetOdasi = p0?.children?.iterator()?.next()

                            var oAnkiSohbetOdasi = SohbetOdasi()

                            var nesneMap = (tekSohbetOdasi?.getValue() as java.util.HashMap<String, Object>)

                            oAnkiSohbetOdasi.sohbetodasi_id = nesneMap.get("sohbetodasi_id").toString()
                            oAnkiSohbetOdasi.sohbetodasi_adi = nesneMap.get("sohbetodasi_adi").toString()
                            oAnkiSohbetOdasi.seviye = nesneMap.get("seviye").toString()
                            oAnkiSohbetOdasi.olusturan_id = nesneMap.get("olusturan_id").toString()

                            var gorulenMesajSayisi = tekSohbetOdasi.child("odadaki_kullanicilar")
                                    .child(FirebaseAuth.getInstance().currentUser?.uid)
                                    .child("okunan_mesaj_sayisi")
                                    .getValue().toString().toInt()

                            var toplamMesajSayisi = tekSohbetOdasi.child("sohbet_odasi_mesajlari").childrenCount.toInt()

                            okunmayiBekleyenMesajSayisi = toplamMesajSayisi - gorulenMesajSayisi

                            bildirimGonder(baslik, icerik, oAnkiSohbetOdasi)


                        }


                    })


        }


    }

    private fun bildirimGonder(baslik: String?, icerik: String?, oAnkiSohbetOdasi: SohbetOdasi) {

        var bildirimID = notificationIDOlustur(oAnkiSohbetOdasi.sohbetodasi_id!!)
        Log.e("AAA", "" + bildirimID)

        var pendingIntent=Intent(this,MainActivity::class.java)
        pendingIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        pendingIntent.putExtra("sohbet_odasi_id",oAnkiSohbetOdasi.sohbetodasi_id!!)

        var bildirimPendingIntent=PendingIntent.getActivity(this,10,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        var builder = NotificationCompat.Builder(this, oAnkiSohbetOdasi.sohbetodasi_adi!!)
                .setSmallIcon(R.drawable.ic_action_user)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_action_user))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(oAnkiSohbetOdasi.sohbetodasi_adi + " odasından " + baslik)
                .setContentText("İÇERİK")
                .setColor(getColor(R.color.colorAccent))
                .setAutoCancel(true)
                .setSubText("" + okunmayiBekleyenMesajSayisi + " yeni mesaj")
                .setStyle(NotificationCompat.BigTextStyle().bigText(icerik))
                .setNumber(okunmayiBekleyenMesajSayisi)
                .setOnlyAlertOnce(true)
                .setContentIntent(bildirimPendingIntent)


        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(bildirimID, builder.build())


    }

    private fun activityKontrolEt(): Boolean {

        if (SohbetOdasiActivity.activityAcikMi) {
            return true
        } else return false

    }

    private fun notificationIDOlustur(sohbetOdasiID: String): Int {

        var id = 0

        for (i in 4..8) {
            id = id + sohbetOdasiID[i].toInt()
        }


        return id
    }


}