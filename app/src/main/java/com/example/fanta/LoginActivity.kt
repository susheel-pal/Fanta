package com.example.fanta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import android.app.ProgressDialog
import com.google.firebase.auth.FirebaseAuth
//import android.os.Bundle
//import com.example.fantam.R
import android.content.Intent
//import com.example.fantam.RegistrationActivity
import android.content.DialogInterface
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
//import com.example.fantam.DashboardActivity
import java.util.*

class LoginActivity : AppCompatActivity() {
    private var email: EditText? = null
    private var password: EditText? = null
    private val name: EditText? = null
    private var mlogin: Button? = null
    private var newdnewaccount: TextView? = null
    private var reocverpass //recoverpass
            : TextView? = null
    var currentUser: FirebaseUser? = null
    private var loadingBar: ProgressDialog? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val actionBar = supportActionBar
        actionBar!!.setTitle("Create Account")
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        // initialising the layout items
        email = findViewById(R.id.login_email)
        password = findViewById(R.id.login_password)
        newdnewaccount = findViewById(R.id.needs_new_account)
        reocverpass = findViewById(R.id.forgetp)
        mAuth = FirebaseAuth.getInstance()
        mlogin = findViewById(R.id.login_button)
        loadingBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()

        // checking if user is null or not
        if (mAuth != null) {
            currentUser = mAuth!!.currentUser
        }
        mlogin?.setOnClickListener(View.OnClickListener {
            val emaill: String = email?.getText().toString().trim { it <= ' ' }
            val pass: String = password?.getText().toString().trim { it <= ' ' }

            // if format of email doesn't matches return null
            if (!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()) {
                email?.setError("Invalid Email")
                email?.setFocusable(true)
            } else {
                loginUser(emaill, pass)
            }
        })

        // If new account then move to Registration Activity
        newdnewaccount?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegistrationActivity::class.java
                )
            )
        })

        // Recover Your Password using email
        reocverpass?.setOnClickListener(View.OnClickListener { showRecoverPasswordDialog() })
    }

    private fun showRecoverPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Recover Password")
        val linearLayout = LinearLayout(this)
        val emailet = EditText(this) //write your registered email
        emailet.setText("Email")
        emailet.minEms = 16
        emailet.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        linearLayout.addView(emailet)
        linearLayout.setPadding(10, 10, 10, 10)
        builder.setView(linearLayout)
        builder.setPositiveButton("Recover") { dialog, which ->
            val emaill: String = emailet.text.toString().trim { it <= ' ' }
            beginRecovery(emaill) //send a mail on the mail to recover password
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
        builder.create().show()
    }

    private fun beginRecovery(emaill: String) {
        loadingBar!!.setMessage("Sending Email....")
        loadingBar!!.setCanceledOnTouchOutside(false)
        loadingBar!!.show()

        // send reset password email
        mAuth!!.sendPasswordResetEmail(emaill).addOnCompleteListener { task ->
            loadingBar!!.dismiss()
            if (task.isSuccessful) {
                Toast.makeText(this@LoginActivity, "Done sent", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@LoginActivity, "Error Occured", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            loadingBar!!.dismiss()
            Toast.makeText(this@LoginActivity, "Error Failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun loginUser(emaill: String, pass: String) {
        loadingBar!!.setMessage("Logging In....")
        loadingBar!!.show()

        // sign in with email and password after authenticating
        mAuth!!.signInWithEmailAndPassword(emaill, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loadingBar!!.dismiss()
                val user = mAuth!!.currentUser
                if (task.result.additionalUserInfo!!.isNewUser) {
                    val email = user!!.email
                    val uid = user.uid
                    val hashMap = HashMap<Any, String?>()
                    hashMap.put("email", email)
                    hashMap.put("uid", uid)
                    hashMap.put("name", "")
                    hashMap.put("onlineStatus", "online")
                    hashMap.put("typingTo", "noOne")
                    hashMap.put("phone", "")
                    hashMap.put("image", "")
                    hashMap.put("cover", "")
                    val database = FirebaseDatabase.getInstance()

                    // store the value in Database in "Users" Node
                    val reference = database.getReference("Users")

                    // storing the value in Firebase
                    reference.child(uid).setValue(hashMap)
                }
                Toast.makeText(
                    this@LoginActivity,
                    "Registered User " + user!!.email,
                    Toast.LENGTH_LONG
                ).show()
                val mainIntent = Intent(this@LoginActivity, DashboardActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
                finish()
            } else {
                loadingBar!!.dismiss()
                Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            loadingBar!!.dismiss()
            Toast.makeText(this@LoginActivity, "Error Occured", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}