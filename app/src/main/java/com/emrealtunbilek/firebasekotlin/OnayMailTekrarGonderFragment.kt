package com.emrealtunbilek.firebasekotlin



import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class OnayMailTekrarGonderFragment : DialogFragment() {

    lateinit var emailEdittext:EditText
    lateinit var sifreEdittext:EditText


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view=inflater!!.inflate(R.layout.fragment_dialog, container, false)

        emailEdittext=view.findViewById(R.id.etDialogMail)
        sifreEdittext=view.findViewById(R.id.etDialogSifre)


        var btnIptal=view.findViewById<Button>(R.id.btnDialogIptal)
        btnIptal.setOnClickListener {
            dialog.dismiss()
        }



        var btnGonder=view.findViewById<Button>(R.id.btnDialogGonder)
        btnGonder.setOnClickListener {

            Toast.makeText(activity,"Gönder Tıklandı",Toast.LENGTH_SHORT).show()

        }


        return view
    }

}
