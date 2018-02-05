package com.emrealtunbilek.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.emrealtunbilek.firebasekotlin.adapters.SohbetOdasiRecyclerViewAdapter
import com.emrealtunbilek.firebasekotlin.dialogs.YeniSohbetOdasiFDialogFragment
import com.emrealtunbilek.firebasekotlin.model.SohbetMesaj
import com.emrealtunbilek.firebasekotlin.model.SohbetOdasi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_sohbet.*
import java.util.*
import kotlin.collections.ArrayList

class SohbetActivity : AppCompatActivity() {

    lateinit var tumSohbetOdalari:ArrayList<SohbetOdasi>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_sohbet)
        init()



    }

    fun init(){

        tumSohbetOdalariniGetir()

        fabYeniSohbetOdasi.setOnClickListener {

            var dialog= YeniSohbetOdasiFDialogFragment()
            dialog.show(supportFragmentManager,"gosteryenisohbetodasi")

        }

        imgBackSohbetActivity.setOnClickListener {

            super.onBackPressed()
        }



    }

    private fun tumSohbetOdalariniGetir() {

        tumSohbetOdalari=ArrayList<SohbetOdasi>()
        var ref=FirebaseDatabase.getInstance().reference

        var sorgu=ref.child("sohbet_odasi").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {

                for(tekSohbetOdasi in p0!!.children){

                    var oAnkiSohbetOdasi = SohbetOdasi()

                    var nesneMap=(tekSohbetOdasi.getValue() as HashMap<String, Object>)

                    oAnkiSohbetOdasi.sohbetodasi_id=nesneMap.get("sohbetodasi_id").toString()
                    oAnkiSohbetOdasi.sohbetodasi_adi=nesneMap.get("sohbetodasi_adi").toString()
                    oAnkiSohbetOdasi.seviye=nesneMap.get("seviye").toString()
                    oAnkiSohbetOdasi.olusturan_id=nesneMap.get("olusturan_id").toString()


                    var tumMesajlar=ArrayList<SohbetMesaj>()
                    for (mesajlar in tekSohbetOdasi.child("sohbet_odasi_mesajlari").children){

                        var okunanMesaj=SohbetMesaj()
                        okunanMesaj.timestamp=mesajlar.getValue(SohbetMesaj::class.java)?.timestamp
                        okunanMesaj.adi=mesajlar.getValue(SohbetMesaj::class.java)?.adi
                        okunanMesaj.kullanici_id=mesajlar.getValue(SohbetMesaj::class.java)?.kullanici_id
                        okunanMesaj.mesaj=mesajlar.getValue(SohbetMesaj::class.java)?.mesaj
                        okunanMesaj.profil_resmi=mesajlar.getValue(SohbetMesaj::class.java)?.profil_resmi

                        tumMesajlar.add(okunanMesaj)
                    }

                    /* oAnkiSohbetOdasi.olusturan_id=tekSohbetOdasi.getValue(SohbetOdasi::class.java)?.olusturan_id
                     oAnkiSohbetOdasi.seviye=tekSohbetOdasi.getValue(SohbetOdasi::class.java)?.seviye
                     oAnkiSohbetOdasi.sohbetodasi_adi=tekSohbetOdasi.getValue(SohbetOdasi::class.java)?.sohbetodasi_adi
                     oAnkiSohbetOdasi.sohbetodasi_id=tekSohbetOdasi.getValue(SohbetOdasi::class.java)?.sohbetodasi_id*/

                    Log.e("TEST", oAnkiSohbetOdasi.sohbetodasi_id +" "+ oAnkiSohbetOdasi.seviye+" "+oAnkiSohbetOdasi.olusturan_id)


                    oAnkiSohbetOdasi.sohbet_odasi_mesajlari=tumMesajlar
                    tumSohbetOdalari.add(oAnkiSohbetOdasi)

                }

                Toast.makeText(this@SohbetActivity,"Tüm Sohbet odası sayısı : "+tumSohbetOdalari.size,Toast.LENGTH_SHORT).show()


                sohbetOdalariListele()

            }


        })


    }

    private fun sohbetOdalariListele() {

        var adapter=SohbetOdasiRecyclerViewAdapter(this@SohbetActivity, tumSohbetOdalari)
        rvSohbetOdalari.adapter=adapter
        rvSohbetOdalari.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)



    }

    fun sohbetOdasiSil(silinecekSohbetOdasiID:String){


        var ref=FirebaseDatabase.getInstance().reference
        ref.child("sohbet_odasi")
                .child(silinecekSohbetOdasiID)
                .removeValue()

        Toast.makeText(this,"Sohbet Odası Silindi",Toast.LENGTH_SHORT).show()
        init()



    }
}
