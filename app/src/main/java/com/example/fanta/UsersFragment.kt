package com.example.fanta

//import com.example.fantam.ModelUsers.uid
//import com.example.fantam.ModelUsers.name
//import com.example.fantam.ModelUsers.email
import androidx.recyclerview.widget.RecyclerView
//import com.example.fantam.AdapterUsers
//import com.example.fantam.ModelUsers
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
//import com.example.fantam.R
import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import androidx.core.view.MenuItemCompat
import android.text.TextUtils
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class UsersFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var adapterUsers: AdapterUsers? = null
    var usersList: MutableList<ModelUsers>? = null
    var firebaseAuth: FirebaseAuth? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        recyclerView = view.findViewById(R.id.recyclep)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setLayoutManager(LinearLayoutManager(activity))
        usersList = ArrayList()
        firebaseAuth = FirebaseAuth.getInstance()
        allUsers
        return view
    }

    private val allUsers: Unit
        private get() {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val reference = FirebaseDatabase.getInstance().getReference("Users")
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    usersList!!.clear()
                    for (dataSnapshot1 in dataSnapshot.children) {
                        val modelUsers: ModelUsers? =
                            dataSnapshot1.getValue<ModelUsers>(ModelUsers::class.java)
                        if (modelUsers?.uid != null && modelUsers.uid != firebaseUser!!.uid) {
                            usersList!!.add(modelUsers)
                        }
                        adapterUsers = activity?.let { AdapterUsers(it, usersList!!) }
                        recyclerView!!.adapter = adapterUsers
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun searchusers(s: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList!!.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelUsers: ModelUsers? =
                        dataSnapshot1.getValue<ModelUsers>(ModelUsers::class.java)
                    if (modelUsers?.uid != null && modelUsers.uid != firebaseUser!!.uid) {
                        if (modelUsers.name?.toLowerCase()?.contains(s.toLowerCase()) == true ||
                            modelUsers.email?.toLowerCase()?.contains(s.toLowerCase()) == true
                        ) {
                            usersList!!.add(modelUsers)
                        }
                    }
                    adapterUsers = activity?.let { AdapterUsers(it, usersList!!) }
                    adapterUsers!!.notifyDataSetChanged()
                    recyclerView!!.adapter = adapterUsers
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        menu.findItem(R.id.logout).isVisible = false
        val item = menu.findItem(R.id.search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!TextUtils.isEmpty(query.trim { it <= ' ' })) {
                    searchusers(query)
                } else {
                    allUsers
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (!TextUtils.isEmpty(newText.trim { it <= ' ' })) {
                    searchusers(newText)
                } else {
                    allUsers
                }
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }
}