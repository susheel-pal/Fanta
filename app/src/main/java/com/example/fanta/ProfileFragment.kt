package com.example.fanta

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.StorageReference
import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.example.fantam.ModelPost
//import com.example.fantam.AdapterPosts
import android.app.ProgressDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.fanta.R
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseError
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
//import com.example.fantam.EditProfilePage
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    private var firebaseAuth: FirebaseAuth? = null
    var firebaseUser: FirebaseUser? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
    var avatartv: ImageView? = null
    var covertv: ImageView? = null
    var nam: TextView? = null
    var email: TextView? = null
    var phone: TextView? = null
    var postrecycle: RecyclerView? = null
    var storageReference: StorageReference? = null
    var storagepath = "Users_Profile_Cover_image/"
    var fab: FloatingActionButton? = null
    var posts: MutableList<ModelPost>? = null
    var adapterPosts: AdapterPosts? = null
    var uid: String? = null
    var pd: ProgressDialog? = null
    lateinit var cameraPermission: Array<String>
    lateinit var storagePermission: Array<String>
    var imageuri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("Users")
        avatartv = view.findViewById(R.id.avatartv)
        nam = view.findViewById(R.id.nametv)
        email = view.findViewById(R.id.emailtv)
        uid = FirebaseAuth.getInstance().uid
        fab = view.findViewById(R.id.fab)
        postrecycle = view.findViewById(R.id.recyclerposts)
        posts = ArrayList()
        pd = ProgressDialog(activity)
        loadMyPosts()
        pd!!.setCanceledOnTouchOutside(false)

        // Retrieving user data from firebase
        val query = databaseReference!!.orderByChild("email").equalTo(
            firebaseUser!!.email
        )
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    val name = "" + dataSnapshot1.child("name").value
                    val emaill = "" + dataSnapshot1.child("email").value
                    val image = "" + dataSnapshot1.child("image").value
                    nam?.setText(name)
                    email?.setText(emaill)
                    try {
                        Glide.with(activity!!).load(image).into((avatartv)!!)
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        fab?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    activity,
                    EditProfilePage::class.java
                )
            )
        })
        return view
    }

    private fun loadMyPosts() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        postrecycle!!.layoutManager = layoutManager
        val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        val query = databaseReference.orderByChild("uid").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                posts!!.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelPost: ModelPost? =
                        dataSnapshot1.getValue<ModelPost>(ModelPost::class.java)
                    if (modelPost != null) {
                        posts!!.add(modelPost)
                    }
                    adapterPosts = activity?.let { AdapterPosts(it, posts!!) }
                    postrecycle!!.adapter = adapterPosts
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, databaseError.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    companion object {
        private const val CAMERA_REQUEST = 100
        private const val STORAGE_REQUEST = 200
        private const val IMAGEPICK_GALLERY_REQUEST = 300
        private const val IMAGE_PICKCAMERA_REQUEST = 400
    }
}