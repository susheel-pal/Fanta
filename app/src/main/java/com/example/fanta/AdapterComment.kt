package com.example.fanta

import android.content.Context
import android.text.format.DateFormat
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.widget.TextView
import java.util.*

class AdapterComment(
    var context: Context,
    var list: List<ModelComment>,
    var myuid: String,
    var postid: String
) : RecyclerView.Adapter<AdapterComment.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val uid = list[position].getUid()
        val name = list[position].getUname()
        val email = list[position].getUemail()
        val image = list[position].getUdp()
        val cid = list[position].getCid()
        val comment = list[position].getComment()
        val timestamp = list[position].getPtime()
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        if (timestamp != null) {
            calendar.timeInMillis =  timestamp.toLong()
        }      //java.lang.Long.valueOf(timestamp)
        val timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString()
        holder.name.text = name
        holder.time.text = timedate
        holder.comment.text = comment
        try {
            Glide.with(context).load(image).into(holder.imagea)
        } catch (e: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagea: ImageView
        var name: TextView
        var comment: TextView
        var time: TextView

        init {
            imagea = itemView.findViewById(R.id.loadcomment)
            name = itemView.findViewById(R.id.commentname)
            comment = itemView.findViewById(R.id.commenttext)
            time = itemView.findViewById(R.id.commenttime)
        }
    }
}