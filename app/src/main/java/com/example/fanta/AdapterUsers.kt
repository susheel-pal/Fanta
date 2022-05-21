package com.example.fanta

import android.content.Context
//import com.example.fanta.ModelUsers.uid
//import com.example.fanta.ModelUsers.image
//import com.example.fanta.ModelUsers.name
//import com.example.fanta.ModelUsers.email
//import com.example.fanta.ModelUsers
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
//import com.example.fanta.R
import com.bumptech.glide.Glide
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView

class AdapterUsers(var context: Context, var list: List<ModelUsers>) :
    RecyclerView.Adapter<AdapterUsers.MyHolder>() {
    var firebaseAuth: FirebaseAuth
    var uid: String?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_users, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val hisuid = list[position].uid
        val userImage = list[position].image
        val username = list[position].name
        val usermail = list[position].email
        holder.name.text = username
        holder.email.text = usermail
        try {
            Glide.with(context).load(userImage).into(holder.profiletv)
        } catch (e: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profiletv: CircleImageView
        var name: TextView
        var email: TextView

        init {
            profiletv = itemView.findViewById(R.id.imagep)
            name = itemView.findViewById(R.id.namep)
            email = itemView.findViewById(R.id.emailp)
        }
    }

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        uid = firebaseAuth.uid
    }
}