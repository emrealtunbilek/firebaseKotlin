package com.emrealtunbilek.firebasekotlin

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    private fun yeniUyeKayit(mail: String, sifre: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, sifre)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0.isSuccessful) {
                            progressBarGizle()
                            Toast.makeText(this@RegisterActivity, "Üye kaydedildi:" + FirebaseAuth.getInstance().currentUser?.uid, Toast.LENGTH_SHORT).show()
                            FirebaseAuth.getInstance().signOut()
                        } else {
                            progressBarGizle()
                            Toast.makeText(this@RegisterActivity, "Üye kaydedilirken sorun olustu:" + p0.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })


    }

    private fun progressBarGoster() {
        progressBar.visibility = View.VISIBLE
    }

    private fun progressBarGizle() {
        progressBar.visibility = View.INVISIBLE
    }
}
