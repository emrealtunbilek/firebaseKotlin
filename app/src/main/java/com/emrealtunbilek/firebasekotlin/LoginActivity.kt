package com.emrealtunbilek.firebasekotlin

import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.emrealtunbilek.firebasekotlin.dialogs.OnayMailTekrarGonderFragment
import com.emrealtunbilek.firebasekotlin.dialogs.SifremiUnuttumDialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_login)
/*
        var customFont=Typeface.createFromAsset(assets,"fonts/roboto.ttf")
        tvSifreTekrarYolla.setTypeface(customFont)*/
        initMyAuthStateListener()


        tvKayitOl.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvOnaymailiniTekrarGonder.setOnClickListener {
            var dialogGoster= OnayMailTekrarGonderFragment()
            dialogGoster.show(supportFragmentManager,"gosterdialog")
        }

        tvSifreTekrarYolla.setOnClickListener {
            var dialogSifreyiTekrarGonder= SifremiUnuttumDialogFragment()
            dialogSifreyiTekrarGonder.show(supportFragmentManager,"gosterdialogsifre")
        }

        btnGirisYap.setOnClickListener {

            if (etMail.text.isNotEmpty() && etSifre.text.isNotEmpty()) {
                progressBarGoster()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(etMail.text.toString(), etSifre.text.toString())
                        .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                            override fun onComplete(p0: Task<AuthResult>) {

                                if (p0.isSuccessful) {
                                    progressBarGizle()

                                    if(!p0.result.user.isEmailVerified){
                                        FirebaseAuth.getInstance().signOut()
                                    }


                                   // Toast.makeText(this@LoginActivity, "Başarılı Giriş :" + FirebaseAuth.getInstance().currentUser?.email, Toast.LENGTH_SHORT).show()

                                } else {
                                    progressBarGizle()
                                    Toast.makeText(this@LoginActivity, "Hatalı Giriş : " + p0.exception?.message, Toast.LENGTH_SHORT).show()
                                }

                            }

                        })


            } else {
                Toast.makeText(this@LoginActivity, "Boş alanları doldurunuz", Toast.LENGTH_SHORT).show()
            }


        }


    }

    private fun progressBarGoster() {
        progressBarLogin.visibility = View.VISIBLE
    }

    private fun progressBarGizle() {
        progressBarLogin.visibility = View.INVISIBLE
    }

    private fun initMyAuthStateListener() {

        mAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici= p0.currentUser

                if(kullanici != null){

                    if(kullanici.isEmailVerified){
                        Toast.makeText(this@LoginActivity, "Mail onaylanmış giriş yapılabilir", Toast.LENGTH_SHORT).show()
                        var intent=Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }else {
                        Toast.makeText(this@LoginActivity, "Mail adresinizi onaylayıp öyle giriş yapın", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
    }
}
