package com.emrealtunbilek.firebasekotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_kullanici_ayarlari.*

class KullaniciAyarlariActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kullanici_ayarlari)

        var kullanici = FirebaseAuth.getInstance().currentUser!!

        etDetayName.setText(kullanici.displayName.toString())
        etDetayMail.setText(kullanici.email.toString())

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

            if (etDetayName.text.toString().isNotEmpty() && etDetayMail.text.toString().isNotEmpty()) {

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
    }
}
