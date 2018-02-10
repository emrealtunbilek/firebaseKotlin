package com.emrealtunbilek.firebasekotlin.adapters


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.renderscript.Sampler
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.emrealtunbilek.firebasekotlin.R
import com.emrealtunbilek.firebasekotlin.SohbetActivity
import com.emrealtunbilek.firebasekotlin.SohbetOdasiActivity
import com.emrealtunbilek.firebasekotlin.model.Kullanici
import com.emrealtunbilek.firebasekotlin.model.SohbetOdasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tek_satir_sohbet_odasi.view.*

/**
 * Created by Emre on 27.01.2018.
 */
class SohbetOdasiRecyclerViewAdapter(mActivity:AppCompatActivity, tumSohbetOdalari:ArrayList<SohbetOdasi>) : RecyclerView.Adapter<SohbetOdasiRecyclerViewAdapter.SohbetOdasiHolder>() {


    var sohbetOdalari=tumSohbetOdalari
    var myActivity=mActivity

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SohbetOdasiHolder {
        var inflater=LayoutInflater.from(parent?.context)
        var tekSatirSohbetOdalari = inflater.inflate(R.layout.tek_satir_sohbet_odasi,parent,false)

        return SohbetOdasiHolder(tekSatirSohbetOdalari)
    }

    override fun getItemCount(): Int {
        return sohbetOdalari.size
    }



    override fun onBindViewHolder(holder: SohbetOdasiHolder?, position: Int) {

        var oAnOlusturulanSohbetOdasi = sohbetOdalari.get(position)
        holder?.setData(oAnOlusturulanSohbetOdasi, position)

    }


    inner class SohbetOdasiHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var tekSatirSohbetOdasi=itemView as ConstraintLayout
        var sohbetOdasiResim = tekSatirSohbetOdasi.imgProfilResmiSohbetOdasi
        var sohbetOdasiSil=tekSatirSohbetOdasi.imgSohbetOdasiSil
        var sohbetOdasiMesajSayisi=tekSatirSohbetOdasi.tvMesajSayisi
        var sohbetOdasiAdi = tekSatirSohbetOdasi.tvSohbetOdasiAdi



        @SuppressLint("RestrictedApi")
        fun setData(oAnOlusturulanSohbetOdasi: SohbetOdasi, position: Int) {
            sohbetOdasiAdi.text=oAnOlusturulanSohbetOdasi.sohbetodasi_adi
           // sohbetOdasiOlusturan.text=oAnOlusturulanSohbetOdasi.sohbetodasi_id
            sohbetOdasiSil.setOnClickListener {

                if(oAnOlusturulanSohbetOdasi.olusturan_id.equals(FirebaseAuth.getInstance().currentUser?.uid)){

                    var dialog=AlertDialog.Builder(ContextThemeWrapper(itemView.context, R.style.dialogstyle))
                    dialog.setTitle("Sohbet Odası Sil ?")
                    dialog.setMessage("Sohbet odası silinecektir ? Emin Misiniz ?")
                    dialog.setCancelable(true)
                    dialog.setPositiveButton("Evet Sil", object :DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {

                            (myActivity as SohbetActivity).sohbetOdasiSil(oAnOlusturulanSohbetOdasi)
                        }

                    })

                    dialog.setNegativeButton("Hayır Silme", object:DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {

                        }

                    })


                    dialog.show()



                }else{

                    Toast.makeText(itemView.context,"Bu sohbet odasını sen oluşturmadın ki silesin!!1",Toast.LENGTH_SHORT).show()

                }


            }

            tekSatirSohbetOdasi.setOnClickListener {



                var kullanici=FirebaseAuth.getInstance().currentUser
                var seviye=0
                var ref=FirebaseDatabase.getInstance().reference
                        .child("kullanici")
                        .orderByKey()
                        .equalTo(kullanici?.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError?) {

                            }

                            override fun onDataChange(p0: DataSnapshot?) {
                              for (tekKullanici in p0?.children!!){
                                  var okunanKullanici=tekKullanici.getValue(Kullanici::class.java)
                                  seviye=okunanKullanici?.seviye?.toInt()!!

                                  if(oAnOlusturulanSohbetOdasi.seviye?.toInt()!! <= seviye){

                                      kullaniciyiSohbetOdasinaKaydet(oAnOlusturulanSohbetOdasi)

                                      var intent=Intent(myActivity,SohbetOdasiActivity::class.java)
                                      intent.putExtra("sohbet_odasi_id",oAnOlusturulanSohbetOdasi.sohbetodasi_id)
                                      myActivity.startActivity(intent)
                                  }else {
                                      Toast.makeText(myActivity,"Seviyeniz yeterli değil",Toast.LENGTH_SHORT).show()
                                  }

                              }
                            }

                        })






            }

            sohbetOdasiMesajSayisi.text="Toplam Mesaj Sayısı: "+ (oAnOlusturulanSohbetOdasi.sohbet_odasi_mesajlari)?.size.toString()

            var ref=FirebaseDatabase.getInstance().reference
            var sorgu=ref.child("kullanici")
                    .orderByKey()
                    .equalTo(oAnOlusturulanSohbetOdasi.olusturan_id).addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(p0: DataSnapshot?) {
                  for(kullanici in p0!!.children){
                      var profilResmiPath=kullanici.getValue(Kullanici::class.java)?.profil_resmi.toString()
                      if(profilResmiPath.isNullOrEmpty() or profilResmiPath.isNullOrBlank())
                      {
                          Picasso.with(itemView.context).load(R.drawable.defaultprofilepic).resize(72,72).into(sohbetOdasiResim)

                      }else {
                          Picasso.with(itemView.context).load(profilResmiPath).resize(72,72).into(sohbetOdasiResim)
                      }
                      Picasso.with(itemView.context).load(profilResmiPath).resize(72,72).into(sohbetOdasiResim)
                     // sohbetOdasiOlusturan.text=kullanici.getValue(Kullanici::class.java)?.isim.toString()
                  }
                }


            })


        }

        private fun kullaniciyiSohbetOdasinaKaydet(oAnOlusturulanSohbetOdasi: SohbetOdasi) {

            var ref=FirebaseDatabase.getInstance().reference
                    .child("sohbet_odasi")
                    .child(oAnOlusturulanSohbetOdasi.sohbetodasi_id)
                    .child("odadaki_kullanicilar")
                    .child(FirebaseAuth.getInstance()?.currentUser?.uid)
                    .child("okunan_mesaj_sayisi")
                    .setValue((oAnOlusturulanSohbetOdasi.sohbet_odasi_mesajlari)?.size.toString())

        }



    }
}