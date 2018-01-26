package com.emrealtunbilek.firebasekotlin


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView


class YeniSohbetOdasiFDialogFragment : DialogFragment() {

    lateinit var etSohbetOdasiAdi : EditText
    lateinit var btnSohbetOdasiOlustur : Button
    lateinit var seekBarSeviye : SeekBar
    lateinit var tvKullaniciSeviye : TextView


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view=inflater!!.inflate(R.layout.fragment_yeni_sohbet_odasi_dialog, container, false)

        btnSohbetOdasiOlustur=view.findViewById(R.id.btnYeniSohbetodasiOlustur)
        etSohbetOdasiAdi=view.findViewById(R.id.etYeniSohbetOdasiAdi)
        seekBarSeviye=view.findViewById(R.id.seekBarSeviye)
        tvKullaniciSeviye=view.findViewById(R.id.tvYeniSohbetSeviye)

        return view
    }

}
