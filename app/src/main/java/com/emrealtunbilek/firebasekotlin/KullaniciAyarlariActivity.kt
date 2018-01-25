package com.emrealtunbilek.firebasekotlin

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_kullanici.*
import java.io.ByteArrayOutputStream


class KullaniciAyarlariActivity : AppCompatActivity(), ProfilResmiFragment.onProfilResimListener {

    var izinlerVerildi = false
    var galeridenGelenURI:Uri? = null
    var kameradanGelenBitmap:Bitmap? = null
    val MEGABYTE=1000000.toDouble()

    override fun getResimYolu(resimPath: Uri?) {

        galeridenGelenURI=resimPath
        Picasso.with(this).load(galeridenGelenURI).resize(100,100).into(imgProfilResmi)
    }

    override fun getResimBitmap(bitmap: Bitmap) {

        kameradanGelenBitmap=bitmap
        imgProfilResmi.setImageBitmap(bitmap)
       // Picasso.with(this).load(bitmap)
    }

    inner class BackgroundResimCompress : AsyncTask<Uri, Void, ByteArray?> {

        var myBitmap:Bitmap? = null

        constructor(){}

        constructor(bm:Bitmap){

            if(bm != null){
                myBitmap=bm
            }


        }

        override fun onPreExecute() {
            super.onPreExecute()
        }


        override fun doInBackground(vararg params: Uri?): ByteArray? {

            //galeriden resim seçilmiş
            if(myBitmap == null){
                myBitmap=MediaStore.Images.Media.getBitmap(this@KullaniciAyarlariActivity.contentResolver, params[0])
                Log.e("TEST","Orjinal resmin boyutu:"+(myBitmap!!.byteCount).toDouble()/MEGABYTE)
            }

            var resimBytes:ByteArray? = null

            for (i in 1..5){
                resimBytes=convertBitmaptoByte(myBitmap, 100/i)
            }

            return resimBytes

        }

        private fun convertBitmaptoByte(myBitmap: Bitmap?, i: Int): ByteArray? {

            var stream=ByteArrayOutputStream()
            myBitmap?.compress(Bitmap.CompressFormat.JPEG, i, stream)
            return stream.toByteArray()

        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            uploadResimtoFirebase(result)
        }

    }

    private fun uploadResimtoFirebase(result: ByteArray?) {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kullanici)

        var kullanici = FirebaseAuth.getInstance().currentUser!!


        kullaniciBilgileriniOku()

        tvSifremiUnuttum.setOnClickListener {

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

            kullanici = FirebaseAuth.getInstance().currentUser!!

            if (etKullaniciAdi.text.toString().isNotEmpty()) {


                var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                        .setDisplayName(etKullaniciAdi.text.toString())
                        .build()
                kullanici.updateProfile(bilgileriGuncelle)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                FirebaseDatabase.getInstance().reference
                                        .child("kullanici")
                                        .child(FirebaseAuth.getInstance().currentUser?.uid)
                                        .child("isim")
                                        .setValue(etKullaniciAdi.text.toString())
                                Toast.makeText(this@KullaniciAyarlariActivity, "Değişiklikler Yapıldı", Toast.LENGTH_SHORT).show()
                            }
                        }
            } else {
                Toast.makeText(this@KullaniciAyarlariActivity, "Kullanıcı adını doldurunuz", Toast.LENGTH_SHORT).show()
            }

            if (etKullaniciTelefon.text.toString().isNotEmpty()) run {

                FirebaseDatabase.getInstance().reference
                        .child("kullanici")
                        .child(FirebaseAuth.getInstance().currentUser?.uid)
                        .child("telefon")
                        .setValue(etKullaniciTelefon.text.toString())


            }

            if(galeridenGelenURI != null){

                fotografCompressed(galeridenGelenURI!!)

            }else if (kameradanGelenBitmap != null){
                fotografCompressed(kameradanGelenBitmap!!)
            }


        }

        tvMailSifreGuncelle.setOnClickListener {

            if (etKullaniciSuankiSifre.text.toString().isNotEmpty()) {

                var credential = EmailAuthProvider.getCredential(kullanici.email.toString(), etKullaniciSuankiSifre.text.toString())
                kullanici.reauthenticate(credential)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {

                                guncelleLayout.visibility = View.VISIBLE
                                btnMailGuncelle.setOnClickListener {

                                    mailAdresiniGuncelle()


                                }

                                btnSifreGuncelle.setOnClickListener {
                                    sifreBilgisiniGuncelle()
                                }

                            } else {

                                Toast.makeText(this@KullaniciAyarlariActivity, "Şuanki şifrenizi yanlış girdiniz", Toast.LENGTH_SHORT).show()
                                guncelleLayout.visibility = View.INVISIBLE
                            }


                        }


            } else {
                Toast.makeText(this@KullaniciAyarlariActivity, "Güncellemeler için geçerli şifrenizi yazmalısınız", Toast.LENGTH_SHORT).show()
            }


        }

        imgProfilResmi.setOnClickListener {

           if(izinlerVerildi){
               var dialog=ProfilResmiFragment()
               dialog.show(supportFragmentManager,"fotosec")
           }else{
               izinleriIste()
           }

        }


    }

    private fun fotografCompressed(galeridenGelenURI: Uri) {
        var compressed=BackgroundResimCompress()
        compressed.execute(galeridenGelenURI)
    }

    private fun fotografCompressed(kameradanGelenBitmap: Bitmap) {

        var compressed=BackgroundResimCompress(kameradanGelenBitmap)
        var uri:Uri?=null
        compressed.execute(uri)

    }

    private fun izinleriIste() {

        var izinler= arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
        if(ContextCompat.checkSelfPermission(this, izinler[0])==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, izinler[1])==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, izinler[2])==PackageManager.PERMISSION_GRANTED){

            izinlerVerildi=true
        }else {
            ActivityCompat.requestPermissions(this, izinler, 150)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 150){

            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED){
                var dialog=ProfilResmiFragment()
                dialog.show(supportFragmentManager,"fotosec")
            }else{
                Toast.makeText(this@KullaniciAyarlariActivity,"Tüm izinleri vermelisiniz",Toast.LENGTH_SHORT).show()
            }


        }
    }


    private fun kullaniciBilgileriniOku() {

        var referans=FirebaseDatabase.getInstance().reference

        var kullanici=FirebaseAuth.getInstance().currentUser
        tvMailAdresi.text = kullanici?.email


        //query 1
        var sorgu=referans.child("kullanici")
                .orderByKey()
               // .equalTo(kullanici?.uid)
                .limitToLast(2)
        sorgu.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError?) {

            }
            override fun onDataChange(p0: DataSnapshot?) {
                for (singleSnapshot in p0!!.children){
                    var okunanKullanici = singleSnapshot.getValue(Kullanici::class.java)
                    etKullaniciAdi.setText(okunanKullanici?.isim)
                    etKullaniciTelefon.setText(okunanKullanici?.telefon)
                    Log.e("FIREBASE","Adı:"+okunanKullanici?.isim+" Telefon:"+okunanKullanici?.telefon+" Uid:"+okunanKullanici?.kullanici_id+" Seviye:"+okunanKullanici?.seviye)
                }
            }

        })

        //query 2
        var sorgu2=referans.child("kullanici")
                .orderByChild("kullanici_id")
                .equalTo(kullanici?.uid)
        sorgu2.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError?) {

            }
            override fun onDataChange(p0: DataSnapshot?) {
                for (singleSnapshot in p0!!.children){
                    var okunanKullanici = singleSnapshot.getValue(Kullanici::class.java)
                  //  etKullaniciAdi.setText(okunanKullanici?.isim)
                    //etKullaniciTelefon.setText(okunanKullanici?.telefon)
                    Log.e("FIREBASE2","Adı:"+okunanKullanici?.isim+" Telefon:"+okunanKullanici?.telefon+" Uid:"+okunanKullanici?.kullanici_id+" Seviye:"+okunanKullanici?.seviye)
                }
            }

        })


        //query 3
        var sorgu3=referans.child("kullanici")
                .child(kullanici?.uid)
                .orderByValue()
                //.equalTo(kullanici?.uid)
        sorgu3.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError?) {

            }
            override fun onDataChange(p0: DataSnapshot?) {
                for (singleSnapshot in p0!!.children){
                   // var okunanKullanici = singleSnapshot.getValue(Kullanici::class.java)
                    //  etKullaniciAdi.setText(okunanKullanici?.isim)
                    //etKullaniciTelefon.setText(okunanKullanici?.telefon)
                    Log.e("FIREBASE3",singleSnapshot?.value.toString())
                }
            }

        })




    }


    private fun sifreBilgisiniGuncelle() {

        var kullanici = FirebaseAuth.getInstance().currentUser!!

        if (kullanici != null) {
            kullanici.updatePassword(etYeniSifre.text.toString())
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

            FirebaseAuth.getInstance().fetchProvidersForEmail(etYeniMail.text.toString())
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            if (task.getResult().providers?.size == 1) {
                                Toast.makeText(this@KullaniciAyarlariActivity, "Email Kullanımda", Toast.LENGTH_SHORT).show()
                            } else {
                                kullanici.updateEmail(etYeniMail.text.toString())
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
