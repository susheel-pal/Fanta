package com.example.fanta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.material.bottomnavigation.BottomNavigationView
//import android.os.Bundle
import androidx.appcompat.app.ActionBar
//import com.example.fanta.R
//import com.example.fanta.HomeFragment
//import com.example.fanta.ProfileFragment
//import com.example.fanta.UsersFragment
//import com.example.fanta.AddBlogsFragment

class DashboardActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    var firebaseUser: FirebaseUser? = null
    var myuid: String? = null
    var actionBar: ActionBar? = null
    var navigationView: BottomNavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        actionBar = supportActionBar
        actionBar!!.title = "Profile Activity"
        firebaseAuth = FirebaseAuth.getInstance()
        navigationView = findViewById(R.id.navigation)
        navigationView?.setOnNavigationItemSelectedListener(selectedListener)
        actionBar!!.title = "Home"

        // When we open the application first
        // time the fragment should be shown to the user
        // in this case it is home fragment
        val fragment = HomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content, fragment, "")
        fragmentTransaction.commit()
    }

    private val selectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    actionBar!!.title = "Home"
                    val fragment = HomeFragment()
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.content, fragment, "")
                    fragmentTransaction.commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    actionBar!!.title = "Profile"
                    val fragment1 = ProfileFragment()
                    val fragmentTransaction1 = supportFragmentManager.beginTransaction()
                    fragmentTransaction1.replace(R.id.content, fragment1)
                    fragmentTransaction1.commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_users -> {
                    actionBar!!.title = "Users"
                    val fragment2 = UsersFragment()
                    val fragmentTransaction2 = supportFragmentManager.beginTransaction()
                    fragmentTransaction2.replace(R.id.content, fragment2, "")
                    fragmentTransaction2.commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_addblogs -> {
                    actionBar!!.title = "Add Blogs"
                    val fragment4 = AddBlogsFragment()
                    val fragmentTransaction4 = supportFragmentManager.beginTransaction()
                    fragmentTransaction4.replace(R.id.content, fragment4, "")
                    fragmentTransaction4.commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
}