package com.emrealtunbilek.firebasekotlin



import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class OnayMailTekrarGonderFragment : DialogFragment() {

    lateinit var emailEdittext:EditText
    lateinit var sifreEdittext:EditText
    lateinit var mContext:FragmentActivity


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view=inflater!!.inflate(R.layout.fragment_dialog, container, false)

        emailEdittext=view.findViewById(R.id.etDialogMail)
        sifreEdittext=view.findViewById(R.id.etDialogSifre)
        mContext=activity


        var btnIptal=view.findViewById<Button>(R.id.btnDialogIptal)
        btnIptal.setOnClickListener {
            dialog.dismiss()
        }



        var btnGonder=view.findViewById<Button>(R.id.btnDialogGonder)
        btnGonder.setOnClickListener {

           if(emailEdittext.text.toString().isNotEmpty() && sifreEdittext.text.toString().isNotEmpty()){
               
               girisYapveOnayMailiniTekrarGonder(emailEdittext.text.toString(), sifreEdittext.text.toString())
               
               
               
           }else{
               
               Toast.makeText(mContext,"Boş alanları doldurunuz",Toast.LENGTH_SHORT).show()
           }

        }


        return view
    }

    private fun girisYapveOnayMailiniTekrarGonder(email: String, sifre: String) {

        var credential=EmailAuthProvider.getCredential(email,sifre)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        onayMailiniTekrarGonder()
                        dialog.dismiss()
                    }else{
                        Toast.makeText(activity,"Email veya şifre hatalı",Toast.LENGTH_SHORT).show()
                    }
                }



    }

    private fun onayMailiniTekrarGonder() {
        var kullanici=FirebaseAuth.getInstance().currentUser

        if(kullanici != null){

            kullanici.sendEmailVerification()
                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            if(p0.isSuccessful){
                               Toast.makeText(mContext, "Mail kutunuzu kontrol edin, mailiniz onaylayın", Toast.LENGTH_SHORT).show()
                            }else {
                                Toast.makeText(mContext, "Mail gönderilirken sorun oluştu " + p0.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }

                    })

        }
    }

}
