package com.emrealtunbilek.firebasekotlin.dialogs


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.emrealtunbilek.firebasekotlin.R


class ProfilResmiFragment : DialogFragment() {

    lateinit var tvGalerindenSec:TextView
    lateinit var tvKameradanSec:TextView

    interface onProfilResimListener {

        fun getResimYolu(resimPath:Uri?)
        fun getResimBitmap(bitmap:Bitmap)

    }

    lateinit var mProfilResimListener : onProfilResimListener


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
        tvKameradanSec.setOnClickListener {

           var intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
           startActivityForResult(intent, 200)


        }

        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //galeriden resim seçiliyor
        if(requestCode==100 && resultCode==Activity.RESULT_OK && data != null){

            var galeridenSecilenResimYolu = data.data
            Log.e("EMRE","galeridenSecilenResimYolu")
            mProfilResimListener.getResimYolu(galeridenSecilenResimYolu)
            dismiss()

        }
        //kameradan resim seçiliyor
        else if(requestCode==200 && resultCode==Activity.RESULT_OK && data != null){

            var kameradanCekilenResim : Bitmap
            kameradanCekilenResim=data.extras.get("data") as Bitmap
            mProfilResimListener.getResimBitmap(kameradanCekilenResim)
            dialog.dismiss()

        }


    }

    override fun onAttach(context: Context?) {

        mProfilResimListener=activity as onProfilResimListener

        super.onAttach(context)
    }

}
