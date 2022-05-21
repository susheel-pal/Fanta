package com.example.fanta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
//import android.os.Bundle
//import com.example.fantam.R
import android.content.Intent
import android.os.Handler
//import com.example.fantam.LoginActivity
//import com.example.fantam.DashboardActivity

class SplashScreen : AppCompatActivity() {
    var currentUser: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        mAuth = FirebaseAuth.getInstance()
        if (mAuth != null) {
            currentUser = mAuth!!.currentUser
        }
        Handler().postDelayed({
            val user = mAuth!!.currentUser
            if (user == null) {
                val intent = Intent(this@SplashScreen, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val mainIntent = Intent(this@SplashScreen, DashboardActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
                finish()
            }
        }, 1000)
    }
}