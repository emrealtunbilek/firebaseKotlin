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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_sohbet.*
import java.util.*
import kotlin.collections.ArrayList

class SohbetActivity : AppCompatActivity() {

    var tumSohbetOdalari:ArrayList<SohbetOdasi>? = null
    var myAdapter:SohbetOdasiRecyclerViewAdapter? = null
    var sohbetOdasiIDSet: HashSet<String>? = null
    var mSohbetOdasiReferans: DatabaseReference? = null
    var odaSilinecek=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_sohbet)
        baslatOdaListener()
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


    var mValueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {

        }

        //cagrıldı an tüm mesajları getirir, sonrasında ise bir ekleme veya cıkarma durumunda tetiklenir
        override fun onDataChange(p0: DataSnapshot?) {

            myAdapter?.notifyDataSetChanged()
            tumSohbetOdalariniGetir()
        }


    }

    private fun baslatOdaListener() {
        mSohbetOdasiReferans = FirebaseDatabase.getInstance().getReference().child("sohbet_odasi")


        mSohbetOdasiReferans?.addValueEventListener(mValueEventListener)
    }

    private fun tumSohbetOdalariniGetir() {


        if (tumSohbetOdalari == null) {
            tumSohbetOdalari=ArrayList<SohbetOdasi>()
            sohbetOdasiIDSet=HashSet<String>()
        }


        var ref=FirebaseDatabase.getInstance().reference

        var sorgu=ref.child("sohbet_odasi").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {

                for(tekSohbetOdasi in p0!!.children){

                    if(!sohbetOdasiIDSet!!.contains(tekSohbetOdasi.key)){

                        sohbetOdasiIDSet?.add(tekSohbetOdasi.key)


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
                            myAdapter?.notifyDataSetChanged()

                        }

                        /* oAnkiSohbetOdasi.olusturan_id=tekSohbetOdasi.getValue(SohbetOdasi::class.java)?.olusturan_id
                         oAnkiSohbetOdasi.seviye=tekSohbetOdasi.getValue(SohbetOdasi::class.java)?.seviye
                         oAnkiSohbetOdasi.sohbetodasi_adi=tekSohbetOdasi.getValue(SohbetOdasi::class.java)?.sohbetodasi_adi
                         oAnkiSohbetOdasi.sohbetodasi_id=tekSohbetOdasi.getValue(SohbetOdasi::class.java)?.sohbetodasi_id*/




                        oAnkiSohbetOdasi.sohbet_odasi_mesajlari=tumMesajlar
                        tumSohbetOdalari?.add(oAnkiSohbetOdasi)
                        myAdapter?.notifyDataSetChanged()

                    }



                }


                    if(myAdapter==null){
                        sohbetOdalariListele()
                    }




            }


        })


    }

    private fun sohbetOdalariListele() {

        myAdapter=SohbetOdasiRecyclerViewAdapter(this@SohbetActivity, tumSohbetOdalari!!)
        rvSohbetOdalari.adapter=myAdapter
        rvSohbetOdalari.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)



    }

   /* fun sohbetOdasiSil(silinecekSohbetOdasi:SohbetOdasi){


        var ref=FirebaseDatabase.getInstance().reference
        ref.child("sohbet_odasi")
                .child(silinecekSohbetOdasi.sohbetodasi_id)
                .removeValue()

        init()

        Toast.makeText(this,"Sohbet Odası Silindi",Toast.LENGTH_SHORT).show()




    }*/
}
