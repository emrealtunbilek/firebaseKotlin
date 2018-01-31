package com.emrealtunbilek.firebasekotlin.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emrealtunbilek.firebasekotlin.R
import com.emrealtunbilek.firebasekotlin.model.SohbetMesaj
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tek_satir_mesaj_layout.view.*


class SohbetMesajRecyclerviewAdapter(context: Context, tumMesajlar:ArrayList<SohbetMesaj>):RecyclerView.Adapter<SohbetMesajRecyclerviewAdapter.SohbetMesajViewHolder>() {

    var myContext=context
    var myTumMesajlar=tumMesajlar


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SohbetMesajViewHolder {

        var inflater=LayoutInflater.from(myContext)
        var view=inflater.inflate(R.layout.tek_satir_sohbet_odasi , parent , false)

        return SohbetMesajViewHolder(view)
    }


    override fun onBindViewHolder(holder: SohbetMesajViewHolder?, position: Int) {

        var oanKiMesaj = myTumMesajlar.get(position)
        holder?.setData(oanKiMesaj, position)

    }

    override fun getItemCount(): Int {
        return myTumMesajlar.size
    }


    inner class SohbetMesajViewHolder(itemView:View?):RecyclerView.ViewHolder(itemView){

        var tumLayout=itemView as ConstraintLayout
        var profilResmi=tumLayout.imgMesajprofilResmi
        var mesaj=tumLayout.tvMesaj
        var isim=tumLayout.tvMesajUserAd
        var tarih=tumLayout.tvMesajTarih



        fun setData(oanKiMesaj: SohbetMesaj, position: Int) {

            mesaj.text=oanKiMesaj.mesaj
            isim.text=oanKiMesaj.adi
            tarih.text=oanKiMesaj.timestamp

            if(!oanKiMesaj.profil_resmi.isNullOrEmpty()){
                Picasso.with(myContext).load(oanKiMesaj.profil_resmi).resize(48,48).into(profilResmi)
            }


            
        }


    }



}