package com.emrealtunbilek.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_kullanici_ayarlari.*
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider


class KullaniciAyarlariActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kullanici_ayarlari)

        var kullanici = FirebaseAuth.getInstance().currentUser!!

        etDetayName.setText(kullanici.displayName.toString())


        btnSifreGonder.setOnClickListener {

            FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().currentUser?.email.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            Toast.makeText(this@KullaniciAyarlariActivity, "Şifre sıfırlama maili gönderildi", Toast.LENGTH_SHORT).show()

                        } else {

                            Toast.makeText(this@KullaniciAyarlariActivity, "Hata oluştu :" + task.exception?.message, Toast.LENGTH_SHORT).show()

                        }
                    }

        }

        btnDegisiklikleriKaydet.setOnClickListener {

            if (etDetayName.text.toString().isNotEmpty()) {

                kullanici = FirebaseAuth.getInstance().currentUser!!

                if (!etDetayName.text.toString().equals(kullanici.displayName.toString())) {

                    var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                            .setDisplayName(etDetayName.text.toString())
                            .build()
                    kullanici.updateProfile(bilgileriGuncelle)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@KullaniciAyarlariActivity, "Değişiklikler Yapıldı", Toast.LENGTH_SHORT).show()
                                }
                            }


                }


            } else {

                Toast.makeText(this@KullaniciAyarlariActivity, "Boş alanları doldurunuz", Toast.LENGTH_SHORT).show()

            }


        }

        btnSifreveyaMailGuncelle.setOnClickListener {

            if (etDetaySifre.text.toString().isNotEmpty()) {

                var credential = EmailAuthProvider.getCredential(kullanici.email.toString(), etDetaySifre.text.toString())
                kullanici.reauthenticate(credential)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {

                                guncellelayout.visibility = View.VISIBLE
                                btnMailGuncelle.setOnClickListener {

                                    mailAdresiniGuncelle()


                                }

                                btnSifreGuncelle.setOnClickListener {
                                    sifreBilgisiniGuncelle()
                                }

                            } else {

                                Toast.makeText(this@KullaniciAyarlariActivity, "Şuanki şifrenizi yanlış girdiniz", Toast.LENGTH_SHORT).show()
                                guncellelayout.visibility = View.INVISIBLE
                            }


                        }


            } else {
                Toast.makeText(this@KullaniciAyarlariActivity, "Güncellemeler için geçerli şifrenizi yazmalısınız", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun sifreBilgisiniGuncelle() {

        var kullanici = FirebaseAuth.getInstance().currentUser!!

        if (kullanici != null) {
            kullanici.updatePassword(etyeniSifre.text.toString())
                    .addOnCompleteListener { task ->
                        Toast.makeText(this@KullaniciAyarlariActivity, "Şifreniz değiştirildi tekrar giriş yapın", Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                        loginSayfasinaYonlendir()
                    }
        }

    }

    private fun mailAdresiniGuncelle() {
        var kullanici = FirebaseAuth.getInstance().currentUser!!

        if (kullanici != null) {

            FirebaseAuth.getInstance().fetchProvidersForEmail(etYenimail.text.toString())
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            if (task.getResult().providers?.size == 1) {
                                Toast.makeText(this@KullaniciAyarlariActivity, "Email Kullanımda", Toast.LENGTH_SHORT).show()
                            } else {
                                kullanici.updateEmail(etYenimail.text.toString())
                                        .addOnCompleteListener { task ->

                                            Toast.makeText(this@KullaniciAyarlariActivity, "Mail adresi değişti! tekrar giriş yapın", Toast.LENGTH_SHORT).show()
                                            FirebaseAuth.getInstance().signOut()
                                            loginSayfasinaYonlendir()
                                        }
                            }


                        } else {

                            Toast.makeText(this@KullaniciAyarlariActivity, "Email Güncellenemedi", Toast.LENGTH_SHORT).show()
                        }


                    }


        }
    }

    fun loginSayfasinaYonlendir() {

        var intent = Intent(this@KullaniciAyarlariActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
