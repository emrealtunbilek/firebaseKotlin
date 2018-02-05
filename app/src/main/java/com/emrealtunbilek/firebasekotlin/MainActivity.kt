package com.emrealtunbilek.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.os.Build
import android.annotation.TargetApi
import android.view.*
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.PopupMenu


class MainActivity : AppCompatActivity() {


    lateinit var myAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        menuyuayarla()
        initAuthStateListener()
        initFCM()
        getPendingIntent()

    }

    private fun menuyuayarla() {

        imgMenu.setOnClickListener {

            val wrapper = ContextThemeWrapper(this@MainActivity, R.style.popupMenuStyle)
            val popupMenu = PopupMenu(wrapper, imgMenu)

         //   var popupMenu=PopupMenu(this@MainActivity, imgMenu)
            popupMenu.menuInflater.inflate(R.menu.anamenu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.menuCikisyap -> {
                            cikisyap()
                            return true
                        }

                        R.id.menuHesapAyalari -> {

                            var intent = Intent(this@MainActivity, KullaniciAyarlariActivity::class.java)
                            startActivity(intent)
                            return true
                        }

                        R.id.menuSohbet -> {

                            var intent = Intent(this@MainActivity, SohbetActivity::class.java)
                            startActivity(intent)
                            return true
                        }

                        else -> return false
                    }

                }



            })

            popupMenu.show()


        }
    }


    private fun getPendingIntent() {
        var gelenIntent = intent

        if (gelenIntent.hasExtra("sohbet_odasi_id")) {

            var intent = Intent(this, SohbetOdasiActivity::class.java)
            intent.putExtra("sohbet_odasi_id", gelenIntent.getStringExtra("sohbet_odasi_id"))
            startActivity(intent)


        }
    }

    private fun initFCM() {
        var token = FirebaseInstanceId.getInstance().token
        tokenVeriTabaninaKaydet(token)
    }

    private fun tokenVeriTabaninaKaydet(refreshedToken: String?) {

        var ref = FirebaseDatabase.getInstance().reference
                .child("kullanici")
                .child(FirebaseAuth.getInstance().currentUser?.uid)
                .child("mesaj_token")
                .setValue(refreshedToken)
    }


    private fun setKullaniciBilgileri() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if (kullanici != null) {
            tvKullaniciAdi.text = if (kullanici.displayName.isNullOrEmpty()) "Tanımlanmadı" else kullanici.displayName
            tvKullaniciEmail.text = kullanici.email
            tvKullaniciUid.text = kullanici.uid
        }
    }

    private fun initAuthStateListener() {
        myAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var kullanici = p0.currentUser

                if (kullanici != null) {

                } else {

                    var intent = Intent(this@MainActivity, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()


                }

            }
        }
    }


    private fun cikisyap() {
        FirebaseAuth.getInstance().signOut()
    }

    override fun onResume() {
        super.onResume()
        kullaniciyiKontrolEt()
        setKullaniciBilgileri()

    }



    private fun kullaniciyiKontrolEt() {
        var kullanici = FirebaseAuth.getInstance().currentUser

        if (kullanici == null) {
            var intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(myAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        if (myAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(myAuthStateListener)
        }
    }


}
