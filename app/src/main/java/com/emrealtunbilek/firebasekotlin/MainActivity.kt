package com.emrealtunbilek.firebasekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var myAuthStateListener:FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAuthStateListener()
    }

    private fun initAuthStateListener() {
        myAuthStateListener=object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var kullanici= p0.currentUser

                if(kullanici != null){

                }else {

                    var intent=Intent(this@MainActivity, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()


                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.anamenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            R.id.menuCikisyap -> {
                cikisyap()
                return true
            }

        }


        return super.onOptionsItemSelected(item)
    }

    private fun cikisyap() {
       FirebaseAuth.getInstance().signOut()
    }

    override fun onResume() {
        super.onResume()
        kullaniciyiKontrolEt()
    }

    private fun kullaniciyiKontrolEt() {
        var kullanici=FirebaseAuth.getInstance().currentUser

        if(kullanici == null){
            var intent=Intent(this@MainActivity, LoginActivity::class.java)
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
        if(myAuthStateListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(myAuthStateListener)
        }
    }
}
