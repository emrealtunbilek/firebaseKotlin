package com.emrealtunbilek.firebasekotlin.dialogs


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.emrealtunbilek.firebasekotlin.R
import com.emrealtunbilek.firebasekotlin.model.Kullanici
import com.emrealtunbilek.firebasekotlin.model.SohbetMesaj
import com.emrealtunbilek.firebasekotlin.model.SohbetOdasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class YeniSohbetOdasiFDialogFragment : DialogFragment() {

    lateinit var etSohbetOdasiAdi : EditText
    lateinit var btnSohbetOdasiOlustur : Button
    lateinit var seekBarSeviye : SeekBar
    lateinit var tvKullaniciSeviye : TextView
    var mSeekProgress = 0
    var kullaniciSeviye=0


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view=inflater!!.inflate(R.layout.fragment_yeni_sohbet_odasi_dialog, container, false)

        btnSohbetOdasiOlustur=view.findViewById(R.id.btnYeniSohbetodasiOlustur)
        etSohbetOdasiAdi=view.findViewById(R.id.etYeniSohbetOdasiAdi)
        seekBarSeviye=view.findViewById(R.id.seekBarSeviye)
        tvKullaniciSeviye=view.findViewById(R.id.tvYeniSohbetSeviye)
        tvKullaniciSeviye.setText(mSeekProgress.toString())



        seekBarSeviye.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
               mSeekProgress = progress
                tvKullaniciSeviye.setText(mSeekProgress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        kullaniciSeviyeBilgisiniGetir()

        btnSohbetOdasiOlustur.setOnClickListener {

            if(!etSohbetOdasiAdi.text.isNullOrEmpty()){


                if(kullaniciSeviye >= seekBarSeviye.progress){

                    var ref=FirebaseDatabase.getInstance().reference

                    var sohbetOdasiID=ref.child("sohbet_odasi").push().key

                    var yeniSohbetOdasi= SohbetOdasi()
                    yeniSohbetOdasi.olusturan_id=FirebaseAuth.getInstance().currentUser?.uid
                    yeniSohbetOdasi.seviye=mSeekProgress.toString()
                    yeniSohbetOdasi.sohbetodasi_adi=etSohbetOdasiAdi.text.toString()
                    yeniSohbetOdasi.sohbetodasi_id=sohbetOdasiID

                    ref.child("sohbet_odasi").child(sohbetOdasiID).setValue(yeniSohbetOdasi)


                    var mesajID=ref.child("sohbet_odasi").push().key

                    var karsilamaMesaji=SohbetMesaj()
                    karsilamaMesaji.mesaj="Sohbet odasına hoşgeldiniz"
                    karsilamaMesaji.timestamp=getMesajTarihi()

                    ref.child("sohbet_odasi")
                            .child(sohbetOdasiID)
                            .child("sohbet_odasi_mesajlari")
                            .child(mesajID)
                            .setValue(karsilamaMesaji)


                    Toast.makeText(activity,"Sohbet Odası Olusturuldu",Toast.LENGTH_SHORT).show()


                }else {

                    Toast.makeText(activity,"Seviyeniz : "+kullaniciSeviye+" ve bu seviyeden yukarı sohbet odası oluşturamazsınız",Toast.LENGTH_SHORT).show()

                }



            }else {

                Toast.makeText(activity,"Sohbet odası adını yazınız",Toast.LENGTH_SHORT).show()

            }





        }




        return view
    }

    private fun kullaniciSeviyeBilgisiniGetir() {

        var ref=FirebaseDatabase.getInstance().reference

        var sorgu=ref.child("kullanici").orderByKey().equalTo(FirebaseAuth.getInstance().currentUser?.uid)
        sorgu.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
            }
            override fun onDataChange(p0: DataSnapshot?) {

                for (tekKayit in p0!!.children){
                    kullaniciSeviye=tekKayit.getValue(Kullanici::class.java)?.seviye!!.toInt()
                }

            }
        })

    }


    private fun getMesajTarihi() : String{

        var sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("tr"))
        return sdf.format(Date())


    }

}
