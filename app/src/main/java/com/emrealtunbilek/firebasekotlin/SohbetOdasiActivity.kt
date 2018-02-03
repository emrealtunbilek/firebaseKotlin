package com.emrealtunbilek.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.emrealtunbilek.firebasekotlin.adapters.SohbetMesajRecyclerviewAdapter
import com.emrealtunbilek.firebasekotlin.model.Kullanici
import com.emrealtunbilek.firebasekotlin.model.SohbetMesaj
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sohbet_odasi.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.widget.Toast
import com.emrealtunbilek.firebasekotlin.model.FCMModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class SohbetOdasiActivity : AppCompatActivity() {

    //Firebase
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    var mMesajReferans: DatabaseReference? = null
    var SERVER_KEY:String? = null
    var BASE_URL="https://fcm.googleapis.com/fcm/"

    var secilenSohbetOdasiId: String = ""
    var tumMesajlar: ArrayList<SohbetMesaj>? = null
    var mesajIDSet: HashSet<String>? = null
    var myAdapter: SohbetMesajRecyclerviewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_odasi)

        //kullanıcının giriş çıkış işlemleri dinler
        baslatFirebaseAuthListener()

        //sohbet activityden gelen seçilen sohbet odasının id bilgisini alır ve valueEventlistener kaydı yapar
        sohbetOdasiniOgren()
        serverkeyOku()
        init()


    }

    private fun serverkeyOku() {
       var ref=FirebaseDatabase.getInstance().reference
               .child("server")
               .orderByValue()
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                var singleSnapShot=p0?.children?.iterator()?.next()
                SERVER_KEY=singleSnapShot?.getValue().toString()
                Log.e("SERVERKEY","OKUNAN SERVERKEY="+SERVER_KEY)
            }


        })
    }

    private fun init() {

        etYeniMesaj.setOnClickListener {

            rvMesajlar.smoothScrollToPosition(myAdapter!!.itemCount - 1)

        }



        imgMesajGonder.setOnClickListener {

            if (!etYeniMesaj.text.toString().equals("")) {

                var yazilanMesaj = etYeniMesaj.text.toString()

                var kaydedilecekMesaj = SohbetMesaj()
                kaydedilecekMesaj.mesaj = yazilanMesaj
                kaydedilecekMesaj.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
                kaydedilecekMesaj.timestamp = getMesajTarihi()

                var referans = FirebaseDatabase.getInstance().reference
                        .child("sohbet_odasi")
                        .child(secilenSohbetOdasiId)
                        .child("sohbet_odasi_mesajlari")

                var yeniMesajID = referans.push().key
                referans.child(yeniMesajID)
                        .setValue(kaydedilecekMesaj)



                var retrofit=Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                var myInterface=retrofit.create(FCMInterface::class.java)

                var headers=HashMap<String, String>()
                headers.put("Content-Type", "application/json")
                headers.put("Authorization", "key="+SERVER_KEY)

                var data=FCMModel.Data("Yeni Mesaj Var", etYeniMesaj.text.toString(),"sohbet")
                var to="fEexSsxyOVg:APA91bGs-znlHVB6roo5FlrIjoS49sBmtGFz2va4jTR9hdve6T0Kl6lkxV8WjLnZGx2JKeBU3_GT4aAN8sGwaZWciSf2KrxU8rTj9EVahhAgS8wRxN28G8AvKejk3Z1GW2EKk1dcaWcx"

                var bildirim:FCMModel= FCMModel(to,data)


                var istek=myInterface.bildirimGonder(headers,bildirim)
                istek.enqueue(object : Callback<Response<FCMModel>>{
                    override fun onResponse(call: Call<Response<FCMModel>>?, response: Response<Response<FCMModel>>?) {
                        Log.e("RETROFIT", "BASARILI : "+response?.raw()+  " MESAJ:"+call?.request())
                    }

                    override fun onFailure(call: Call<Response<FCMModel>>?, t: Throwable?) {
                        Log.e("RETROFIT", "HATA : "+t?.message)
                    }

                })
                etYeniMesaj.setText("")

        }

        }
    }

    private fun getMesajTarihi(): String {

        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("tr"))
        return sdf.format(Date())


    }

    private fun sohbetOdasiniOgren() {
        secilenSohbetOdasiId = intent.getStringExtra("sohbet_odasi_id")
        baslatMesajListener()
    }

    var mValueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {

        }

        //cagrıldı an tüm mesajları getirir, sonrasında ise bir ekleme veya cıkarma durumunda tetiklenir
        override fun onDataChange(p0: DataSnapshot?) {
            sohbetOdasindakiMesajlariGetir()
        }


    }

    private fun sohbetOdasindakiMesajlariGetir() {

        if (tumMesajlar == null) {
            tumMesajlar = ArrayList<SohbetMesaj>()
            mesajIDSet = HashSet<String>()
        }


        mMesajReferans = FirebaseDatabase.getInstance().getReference()

        var sorgu = mMesajReferans?.child("sohbet_odasi")?.child(secilenSohbetOdasiId)?.child("sohbet_odasi_mesajlari")!!
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {

                        for (tekMesaj in p0!!.children) {

                            var geciciMesaj = SohbetMesaj()
                            var kullaniciID = tekMesaj.getValue(SohbetMesaj::class.java)!!.kullanici_id

                            if (!mesajIDSet!!.contains(tekMesaj.key)) {

                                mesajIDSet!!.add(tekMesaj.key)

                                if (kullaniciID != null) {
                                    geciciMesaj.mesaj = tekMesaj.getValue(SohbetMesaj::class.java)!!.mesaj
                                    geciciMesaj.kullanici_id = tekMesaj.getValue(SohbetMesaj::class.java)!!.kullanici_id
                                    geciciMesaj.timestamp = tekMesaj.getValue(SohbetMesaj::class.java)!!.timestamp

                                    var kullaniciDetaylari = mMesajReferans?.child("kullanici")?.orderByKey()?.equalTo(kullaniciID)
                                    kullaniciDetaylari?.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError?) {

                                        }

                                        override fun onDataChange(p0: DataSnapshot?) {
                                            var bulunanKullanici = p0?.children?.iterator()?.next()
                                            Log.e("XXX", bulunanKullanici?.getValue(Kullanici::class.java)?.profil_resmi)
                                            geciciMesaj.profil_resmi = bulunanKullanici?.getValue(Kullanici::class.java)?.profil_resmi
                                            geciciMesaj.adi = bulunanKullanici?.getValue(Kullanici::class.java)?.isim
                                            Log.e("XXX", bulunanKullanici?.getValue(Kullanici::class.java)?.isim)
                                            myAdapter?.notifyDataSetChanged()
                                        }


                                    })

                                    tumMesajlar?.add(geciciMesaj)
                                    myAdapter?.notifyDataSetChanged()
                                    rvMesajlar.scrollToPosition(myAdapter!!.itemCount - 1)

                                } else {
                                    geciciMesaj.mesaj = tekMesaj.getValue(SohbetMesaj::class.java)!!.mesaj
                                    geciciMesaj.timestamp = tekMesaj.getValue(SohbetMesaj::class.java)!!.timestamp
                                    geciciMesaj.profil_resmi = ""
                                    geciciMesaj.adi = ""
                                    tumMesajlar?.add(geciciMesaj)
                                    myAdapter?.notifyDataSetChanged()
                                }


                            }


                        }


                    }

                })


        if (myAdapter == null) {
            initMesajlarListesi()
        }


    }

    private fun initMesajlarListesi() {
        myAdapter = SohbetMesajRecyclerviewAdapter(this, tumMesajlar!!)
        rvMesajlar.adapter = myAdapter
        rvMesajlar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMesajlar.scrollToPosition(myAdapter?.itemCount!! - 1)
    }


    private fun baslatMesajListener() {
        mMesajReferans = FirebaseDatabase.getInstance().getReference().child("sohbet_odasi")
                .child(secilenSohbetOdasiId)
                .child("sohbet_odasi_mesajlari")

        mMesajReferans?.addValueEventListener(mValueEventListener)
    }

    private fun baslatFirebaseAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var kullanici = p0.currentUser

                if (kullanici == null) {
                    var intent = Intent(this@SohbetOdasiActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }


            }


        }
    }

    private fun updateNumMessages(numMessages: Int) {
        val reference = FirebaseDatabase.getInstance().reference

        reference
                .child("sohbet_odasi")
                .child(secilenSohbetOdasiId)
                .child("odadaki_kullanicilar")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("son_gorunen_mesaj_sayisi")
                .setValue(numMessages.toString())
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener!!)
        }
    }

    override fun onResume() {
        super.onResume()
        kullaniciKontrolEt()
    }

    private fun kullaniciKontrolEt() {
        var kullanici = FirebaseAuth.getInstance().currentUser

        if (kullanici == null) {
            var intent = Intent(this@SohbetOdasiActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}
