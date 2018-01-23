package com.emrealtunbilek.firebasekotlin


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class ProfilResmiFragment : DialogFragment() {

    lateinit var tvGalerindenSec:TextView
    lateinit var tvKameradanSec:TextView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var v=inflater!!.inflate(R.layout.fragment_profil_resmi, container, false)

        tvGalerindenSec=v.findViewById(R.id.tvGaleridenFoto)
        tvKameradanSec=v.findViewById(R.id.tvKameradanFoto)

        tvGalerindenSec.setOnClickListener {
            var intent=Intent(Intent.ACTION_GET_CONTENT)
            intent.type="image/*"
            startActivityForResult(intent,100)
        }

        return v
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        


    }

}
