package com.emrealtunbilek.firebasekotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.emrealtunbilek.firebasekotlin.model.Kullanici
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_register)


        btnKayitOl.setOnClickListener {
            if (etMail.text.isNotEmpty() && etSifre.text.isNotEmpty() && etSifreTekrar.text.isNotEmpty()) {
                if (etSifre.text.toString().equals(etSifreTekrar.text.toString())) {
                    progressBarGoster()
                    yeniUyeKayit(etMail.text.toString(), etSifre.text.toString())
                } else {
                    Toast.makeText(this, "Şifreler aynı değil", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Boş alanları doldurunuz", Toast.LENGTH_SHORT).show()
            }
        }

        imgBack.setOnClickListener {

            super.onBackPressed()
        }
    }

    private fun yeniUyeKayit(mail: String, sifre: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, sifre)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0.isSuccessful) {
                            progressBarGizle()
                            onayMailiGonder()

                            var veritabaninaEklenecekKullanici= Kullanici()
                            veritabaninaEklenecekKullanici.isim=etMail.text.toString().substring(0,etMail.text.toString().indexOf("@"))
                            veritabaninaEklenecekKullanici.kullanici_id=FirebaseAuth.getInstance().currentUser?.uid
                            veritabaninaEklenecekKullanici.profil_resmi="http://emrealtunbilek.com/wp-content/uploads/2016/10/apple-icon-72x72.png"
                            veritabaninaEklenecekKullanici.telefon="123"
                            veritabaninaEklenecekKullanici.seviye="1"

                            FirebaseDatabase.getInstance().reference
                                    .child("kullanici")
                                    .child(FirebaseAuth.getInstance().currentUser?.uid)
                                    .setValue(veritabaninaEklenecekKullanici).addOnCompleteListener { task->

                                if(task.isSuccessful){
                                    Toast.makeText(this@RegisterActivity, "Üye kaydedildi:" + FirebaseAuth.getInstance().currentUser?.uid, Toast.LENGTH_SHORT).show()
                                    FirebaseAuth.getInstance().signOut()
                                    loginSayfasinaYonlendir()
                                }

                            }

                        } else {
                            progressBarGizle()
                            Toast.makeText(this@RegisterActivity, "Üye kaydedilirken sorun olustu:" + p0.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })


    }

    private fun onayMailiGonder(){

        var kullanici=FirebaseAuth.getInstance().currentUser

        if(kullanici != null){

            kullanici.sendEmailVerification()
                    .addOnCompleteListener(object : OnCompleteListener<Void>{
                        override fun onComplete(p0: Task<Void>) {
                            if(p0.isSuccessful){
                                Toast.makeText(this@RegisterActivity, "Mail kutunuzu kontrol edin, mailiniz onaylayın", Toast.LENGTH_SHORT).show()
                            }else {
                                Toast.makeText(this@RegisterActivity, "Mail göndeirlirken sorun oluştu " + p0.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }

                    })

        }

    }

    private fun progressBarGoster() {
        progressBar.visibility = View.VISIBLE
    }

    private fun progressBarGizle() {
        progressBar.visibility = View.INVISIBLE
    }
    private fun loginSayfasinaYonlendir() {

        var intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
