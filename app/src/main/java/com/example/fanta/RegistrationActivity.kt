package com.example.fanta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import android.app.ProgressDialog
import com.google.firebase.auth.FirebaseAuth
//import android.os.Bundle
//import com.example.fantam.R
import android.content.Intent
import android.util.Patterns
import android.view.View
import android.widget.Button
//import com.example.fantam.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.widget.Toast
//import com.example.fantam.DashboardActivity
import com.google.android.gms.tasks.OnFailureListener
import java.util.*

class RegistrationActivity : AppCompatActivity() {
    private var email: EditText? = null
    private var password: EditText? = null
    private var name: EditText? = null
    private var mRegister: Button? = null
    private var existaccount: TextView? = null
    private var progressDialog: ProgressDialog? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        val actionBar = supportActionBar
        actionBar!!.setTitle("Create Account")
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        email = findViewById(R.id.register_email)
        name = findViewById(R.id.register_name)
        password = findViewById(R.id.register_password)
        mRegister = findViewById(R.id.register_button)
        existaccount = findViewById(R.id.homepage)
        mAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Register")
        mRegister?.setOnClickListener(View.OnClickListener {
            val emaill: String = email?.getText().toString().trim { it <= ' ' }
            val uname: String = name?.getText().toString().trim { it <= ' ' }
            val pass: String = password?.getText().toString().trim { it <= ' ' }
            if (!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()) {
                email?.setError("Invalid Email")
                email?.setFocusable(true)
            } else if (pass.length < 6) {
                password?.setError("Length Must be greater than 6 character")
                password?.setFocusable(true)
            } else {
                registerUser(emaill, pass, uname)
            }
        })
        existaccount?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@RegistrationActivity,
                    LoginActivity::class.java
                )
            )
        })
    }

    private fun registerUser(emaill: String, pass: String, uname: String) {
        progressDialog!!.show()
        mAuth!!.createUserWithEmailAndPassword(emaill, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog!!.dismiss()
                val user = mAuth!!.currentUser
                val email = user!!.email
                val uid = user.uid
                val hashMap = HashMap<Any, String?>()
                hashMap.put("email", email)
                hashMap.put("uid", uid)
                hashMap.put("name", uname)
                hashMap.put("onlineStatus", "online")
                hashMap.put("typingTo", "noOne")
                hashMap.put("image", "")
                val database = FirebaseDatabase.getInstance()
                val reference = database.getReference("Users")
                reference.child(uid).setValue(hashMap)
                Toast.makeText(
                    this@RegistrationActivity,
                    "Registered User " + user.email,
                    Toast.LENGTH_LONG
                ).show()
                val mainIntent = Intent(this@RegistrationActivity, DashboardActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
                finish()
            } else {
                progressDialog!!.dismiss()
                Toast.makeText(this@RegistrationActivity, "Error", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            progressDialog!!.dismiss()
            Toast.makeText(this@RegistrationActivity, "Error Occured", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}