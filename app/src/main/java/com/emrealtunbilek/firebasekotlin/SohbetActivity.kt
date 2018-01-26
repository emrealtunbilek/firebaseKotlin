package com.emrealtunbilek.firebasekotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sohbet.*

class SohbetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sohbet)


        fabYeniSohbetOdasi.setOnClickListener {

            var dialog=YeniSohbetOdasiFDialogFragment()
            dialog.show(supportFragmentManager,"gosteryenisohbetodasi")

        }


    }
}
