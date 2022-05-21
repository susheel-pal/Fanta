package com.example.fanta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
//import com.example.fantam.ModelUsers
//import com.example.fantam.AdapterUsers
import com.google.firebase.auth.FirebaseAuth
//import android.os.Bundle
//import com.example.fantam.R
//import android.content.Intent
//import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.*

class PostLikedByActivity : AppCompatActivity() {
    var recyclerView: RecyclerView? = null
    var postId: String? = null
    var list: MutableList<ModelUsers>? = null
    var adapterUsers: AdapterUsers? = null
    var firebaseAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_liked_by)
        val actionBar = supportActionBar
        actionBar!!.setTitle("Post Liked By")
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
        firebaseAuth = FirebaseAuth.getInstance()
        actionBar.setSubtitle(firebaseAuth!!.currentUser!!.email)
        recyclerView = findViewById(R.id.likerecycle)
        val intent = intent
        postId = intent.getStringExtra("pid")
        list = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Likes")
        reference.child(postId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list?.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val hisUid = "" + dataSnapshot1.ref.key
                    getUsers(hisUid)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getUsers(hisUid: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.orderByChild("uid").equalTo(hisUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        val model: ModelUsers? = ds.getValue<ModelUsers>(ModelUsers::class.java)
                        if (model != null) {
                            list!!.add(model)
                        }
                    }
                    adapterUsers = list?.let { AdapterUsers(this@PostLikedByActivity, it) }
                    recyclerView!!.adapter = adapterUsers
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}