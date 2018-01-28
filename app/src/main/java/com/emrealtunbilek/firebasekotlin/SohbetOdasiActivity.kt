package com.emrealtunbilek.firebasekotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.emrealtunbilek.firebasekotlin.model.SohbetMesaj
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SohbetOdasiActivity : AppCompatActivity() {

    lateinit var tumMesajlar:ArrayList<SohbetMesaj>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet_odasi)

        sohbetOdasindakiMesajlariGetir()
    }

    private fun sohbetOdasindakiMesajlariGetir() {

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
}
