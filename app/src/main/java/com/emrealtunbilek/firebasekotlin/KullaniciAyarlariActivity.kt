package com.emrealtunbilek.firebasekotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_kullanici_ayarlari.*

class KullaniciAyarlariActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kullanici_ayarlari)

        etDetayName.setText(FirebaseAuth.getInstance().currentUser?.displayName.toString())
        etDetayMail.setText(FirebaseAuth.getInstance().currentUser?.email.toString())
    }
}
