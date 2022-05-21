package com.example.fanta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.RecyclerView
//import com.example.fanta.ModelPost
//import com.example.fanta.AdapterPosts
//import android.os.Bundle
//import com.example.fanta.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.widget.Toast
import androidx.core.view.MenuItemCompat
import android.text.TextUtils
import android.content.Intent
import android.view.*
import android.widget.SearchView
//import androidx.fragment.app.Fragment
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    var firebaseAuth: FirebaseAuth? = null
    var myuid: String? = null
    var recyclerView: RecyclerView? = null
    var posts: MutableList<ModelPost>? = null
    var adapterPosts: AdapterPosts? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        recyclerView = view.findViewById(R.id.postrecyclerview)
        recyclerView?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView?.setLayoutManager(layoutManager)
        posts = ArrayList()
        loadPosts()
        return view
    }

    private fun loadPosts() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                posts!!.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    //val modelPost: ModelPost? = dataSnapshot1.getValue<ModelPost>(ModelPost::class.java)
                      val modelPost: ModelPost? = dataSnapshot1.getValue(ModelPost::class.java)
                    if (modelPost != null) {
                        posts!!.add(modelPost)
                    }
                    adapterPosts = activity?.let { AdapterPosts(it, posts!!) }
                    recyclerView?.adapter = adapterPosts
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, databaseError.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    // Search post code
    private fun searchPosts(search: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                posts!!.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelPost: ModelPost? = dataSnapshot1.getValue<ModelPost>(ModelPost::class.java)
                    if (modelPost!!.getTitle()!!.toLowerCase().contains(search.toLowerCase()) ||
                        modelPost.getDescription()!!.toLowerCase().contains(search.toLowerCase())
                    ) {
                        if (modelPost != null) {
                            posts!!.add(modelPost)
                        }
                    }
                    adapterPosts = activity?.let { AdapterPosts(it, posts!!) }
                    recyclerView!!.adapter = adapterPosts
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        val item = menu.findItem(R.id.search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!TextUtils.isEmpty(query)) {
                    searchPosts(query)
                } else {
                    loadPosts()
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (!TextUtils.isEmpty(newText)) {
                    searchPosts(newText)
                } else {
                    loadPosts()
                }
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Logout functionality
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            firebaseAuth!!.signOut()
            startActivity(Intent(context, SplashScreen::class.java))
            requireActivity().finish()
        }
        return super.onOptionsItemSelected(item)
    }
}