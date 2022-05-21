package com.example.fanta

//import com.example.fanta.ModelPost
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
//import com.example.fantam.R
import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import android.content.Intent
//import com.example.fanta.PostLikedByActivity
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
//import com.example.fanta.PostDetailsActivity
import android.app.ProgressDialog
import android.content.Context
import android.text.format.DateFormat
import android.view.*
import android.widget.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class AdapterPosts(var context: Context, var modelPosts: List<ModelPost>) :
    RecyclerView.Adapter<AdapterPosts.MyHolder>() {
    var myuid: String
    private val liekeref: DatabaseReference
    private val postref: DatabaseReference
    var mprocesslike = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, @SuppressLint("RecyclerView") position: Int) {
        val uid = modelPosts[position].getUid() //uid
        val nameh = modelPosts[position].getUname() //uname
        val titlee = modelPosts[position].getTitle() //title
        val descri = modelPosts[position].getDescription() //description
        val ptime = modelPosts[position].getPtime() //ptime
        val dp = modelPosts[position].getUdp() //udp
        val plike = modelPosts[position].getPlike() //plike
        val image = modelPosts[position].getUimage() //uimage
        val email = modelPosts[position].getUemail() //uemail
        val comm = modelPosts[position].getPcomments() //pcomments
        val pid = modelPosts[position].getPtime() //ptime
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        if (ptime != null) {
            calendar.timeInMillis = ptime.toLong()
        }
        val timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString()
        holder.name.text = nameh
        holder.title.text = titlee
        holder.description.text = descri
        holder.time.text = timedate
        holder.like.text = "$plike Likes"
        holder.comments.text = "$comm Comments"
        setLikes(holder, ptime!!)
        try {
            Glide.with(context).load(dp).into(holder.picture)
        } catch (e: Exception) {
        }
        holder.image.visibility = View.VISIBLE
        try {
            Glide.with(context).load(image).into(holder.image)
        } catch (e: Exception) {
        }
        holder.like.setOnClickListener {
            val intent = Intent(holder.itemView.context, PostLikedByActivity::class.java)
            intent.putExtra("pid", pid)
            holder.itemView.context.startActivity(intent)
        }
        holder.likebtn.setOnClickListener {
            val plike: Int? = modelPosts[position].getPlike()?.toInt()
            mprocesslike = true
            val postid = modelPosts[position].getPtime()
            liekeref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (mprocesslike) {
                        mprocesslike = if (dataSnapshot.child(postid!!).hasChild(myuid)) {
                            postref.child(postid).child("plike").setValue("" + (plike?.minus(1)))
                            liekeref.child(postid).child(myuid).removeValue()
                            false
                        } else {
                            postref.child(postid).child("plike").setValue("" + (plike?.plus(1)))
                            liekeref.child(postid).child(myuid).setValue("Liked")
                            false
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
        holder.more.setOnClickListener { showMoreOptions(holder.more, uid!!, myuid, ptime!!, image!!) }
        holder.comment.setOnClickListener {
            val intent = Intent(context, PostDetailsActivity::class.java)
            intent.putExtra("pid", ptime)
            context.startActivity(intent)
        }
    }

    private fun showMoreOptions(
        more: ImageButton,
        uid: String,
        myuid: String,
        pid: String,
        image: String
    ) {
        val popupMenu = PopupMenu(context, more, Gravity.END)
        if (uid == myuid) {
            popupMenu.menu.add(Menu.NONE, 0, 0, "DELETE")
        }
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.itemId == 0) {
                deltewithImage(pid, image)
            }
            false
        }
        popupMenu.show()
    }

    private fun deltewithImage(pid: String, image: String) {
        val pd = ProgressDialog(context)
        pd.setMessage("Deleting")
        val picref = FirebaseStorage.getInstance().getReferenceFromUrl(image)
        picref.delete().addOnSuccessListener {
            val query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("ptime")
                .equalTo(pid)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (dataSnapshot1 in dataSnapshot.children) {
                        dataSnapshot1.ref.removeValue()
                    }
                    pd.dismiss()
                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_LONG).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }.addOnFailureListener { }
    }

    private fun setLikes(holder: MyHolder, pid: String) {
        liekeref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(pid).hasChild(myuid)) {
                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_liked,
                        0,
                        0,
                        0
                    )
                    holder.likebtn.text = "Liked"
                } else {
                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_like,
                        0,
                        0,
                        0
                    )
                    holder.likebtn.text = "Like"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun getItemCount(): Int {
        return modelPosts.size
    }

    //inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var picture: ImageView
        var image: ImageView
        var name: TextView
        var time: TextView
        var title: TextView
        var description: TextView
        var like: TextView
        var comments: TextView
        var more: ImageButton
        var likebtn: Button
        var comment: Button
        var profile: LinearLayout

        init {
            picture = itemView.findViewById(R.id.picturetv)
            image = itemView.findViewById(R.id.pimagetv)
            name = itemView.findViewById(R.id.unametv)
            time = itemView.findViewById(R.id.utimetv)
            more = itemView.findViewById(R.id.morebtn)
            title = itemView.findViewById(R.id.ptitletv)
            description = itemView.findViewById(R.id.descript)
            like = itemView.findViewById(R.id.plikeb)
            comments = itemView.findViewById(R.id.pcommentco)
            likebtn = itemView.findViewById(R.id.like)
            comment = itemView.findViewById(R.id.comment)
            profile = itemView.findViewById(R.id.profilelayout)
        }
    }

    init {
        myuid = FirebaseAuth.getInstance().currentUser!!.uid
        liekeref = FirebaseDatabase.getInstance().reference.child("Likes")
        postref = FirebaseDatabase.getInstance().reference.child("Posts")
    }
}