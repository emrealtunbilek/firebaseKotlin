package com.emrealtunbilek.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.emrealtunbilek.firebasekotlin.model.SohbetMesaj
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SohbetOdasiActivity : AppCompatActivity() {

    //Firebase
    var mAuthListener:FirebaseAuth.AuthStateListener? = null
    var mMesajReferans : DatabaseReference?=null

    


    var secilenSohbetOdasiId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_odasi)

        //kullanıcının giriş çıkış işlemleri dinler
        baslatFirebaseAuthListener()

        sohbetOdasiniOgren()


    }

    private fun sohbetOdasiniOgren() {
       secilenSohbetOdasiId=intent.getStringExtra("sohbet_odasi_id")
       baslatMesajListener()
    }

    var mValueEventListener:ValueEventListener=object : ValueEventListener{
        override fun onCancelled(p0: DatabaseError?) {

        }

        override fun onDataChange(p0: DataSnapshot?) {
            sohbetOdasindakiMesajlariGetir()
        }


    }

    private fun sohbetOdasindakiMesajlariGetir() {
    }


    private fun baslatMesajListener() {
       mMesajReferans=FirebaseDatabase.getInstance().getReference().child("sohbet_odasi")
               .child(secilenSohbetOdasiId)
               .child("sohbet_odasi_mesajlari")

       mMesajReferans?.addValueEventListener(mValueEventListener)
    }

    private fun baslatFirebaseAuthListener() {
       mAuthListener=object : FirebaseAuth.AuthStateListener{
           override fun onAuthStateChanged(p0: FirebaseAuth) {

               var kullanici=p0.currentUser

               if(kullanici == null){
                   var intent=Intent(this@SohbetOdasiActivity, LoginActivity::class.java)
                   startActivity(intent)
                   finish()
               }


           }


       }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if(mAuthListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener!!)
        }
    }

    override fun onResume() {
        super.onResume()
        kullaniciKontrolEt()
    }

    private fun kullaniciKontrolEt() {
        var kullanici=FirebaseAuth.getInstance().currentUser

        if(kullanici == null){
            var intent=Intent(this@SohbetOdasiActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}


/*
*
*  private fun sohbetOdasindakiMesajlariGetir() {

        var secilenSohbetOdasiID=intent.getStringExtra("sohbet_odasi_id")
        tumMesajlar=ArrayList<SohbetMesaj>()

        var ref=FirebaseDatabase.getInstance().reference

        var sorgu=ref.child("sohbet_odasi")
                .child(secilenSohbetOdasiID)
                .child("sohbet_odasi_mesajlari")
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {

                        for(mesaj in p0!!.children){

                           var eklenecekMesaj=SohbetMesaj()

                            var kullaniciID=mesaj.getValue(SohbetMesaj::class.java)?.kullanici_id

                            if(kullaniciID !=null){
                                eklenecekMesaj.kullanici_id=kullaniciID
                                eklenecekMesaj.mesaj=mesaj.getValue(SohbetMesaj::class.java)?.mesaj
                                eklenecekMesaj.timestamp=mesaj.getValue(SohbetMesaj::class.java)?.timestamp
                                tumMesajlar.add(eklenecekMesaj)


                            }else{
                                eklenecekMesaj.mesaj=mesaj.getValue(SohbetMesaj::class.java)?.mesaj
                                eklenecekMesaj.timestamp=mesaj.getValue(SohbetMesaj::class.java)?.timestamp
                                tumMesajlar.add(eklenecekMesaj)
                            }

                        }
                    }
                })




    }
* */