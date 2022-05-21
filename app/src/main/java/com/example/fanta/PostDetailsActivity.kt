package com.example.fanta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.recyclerview.widget.RecyclerView
import com.example.fanta.ModelComment
import com.example.fanta.AdapterComment
import android.app.ProgressDialog
//import com.example.fanta.R
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import com.example.fanta.PostLikedByActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import com.bumptech.glide.Glide
import com.google.firebase.database.*
//import com.google.firebase.database.R
import java.util.*

class PostDetailsActivity : AppCompatActivity() {
    var hisuid: String? = null
    var ptime: String? = null
    var myuid: String? = null
    var myname: String? = null
    var myemail: String? = null
    var mydp: String? = null
    var uimage: String? = null
    var postId: String? = null
    var plike: String? = null
    var hisdp: String? = null
    var hisname: String? = null
    var picture: ImageView? = null
    var image: ImageView? = null
    var name: TextView? = null
    var time: TextView? = null
    var title: TextView? = null
    var description: TextView? = null
    var like: TextView? = null
    var tcomment: TextView? = null
    var more: ImageButton? = null
    var likebtn: Button? = null
    var share: Button? = null
    var profile: LinearLayout? = null
    var comment: EditText? = null
    var sendb: ImageButton? = null
    var recyclerView: RecyclerView? = null
    var commentList: MutableList<ModelComment>? = null
    var adapterComment: AdapterComment? = null
    var imagep: ImageView? = null
    var mlike = false
    var actionBar: ActionBar? = null
    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)
        actionBar = supportActionBar
        actionBar!!.title = "Comments"
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)
        postId = intent.getStringExtra("pid")
        recyclerView = findViewById(R.id.recyclecomment)
        picture = findViewById(R.id.pictureco)
        image = findViewById(R.id.pimagetvco)
        name = findViewById(R.id.unameco)
        time = findViewById(R.id.utimeco)
        more = findViewById(R.id.morebtn)
        title = findViewById(R.id.ptitleco)
        myemail = FirebaseAuth.getInstance().currentUser!!.email
        myuid = FirebaseAuth.getInstance().currentUser!!.uid
        description = findViewById(R.id.descriptco)
        tcomment = findViewById(R.id.pcommenttv)
        like = findViewById(R.id.plikebco)
        likebtn = findViewById(R.id.like)
        comment = findViewById(R.id.typecommet)
        sendb = findViewById(R.id.sendcomment)
        imagep = findViewById(R.id.commentimge)
        share = findViewById(R.id.share)
        profile = findViewById(R.id.profilelayout)
        progressDialog = ProgressDialog(this)
        loadPostInfo()
        loadUserInfo()
        setLikes()
        actionBar!!.subtitle = "SignedInAs:$myemail"
        loadComments()
        sendb?.setOnClickListener(View.OnClickListener { postComment() })
        likebtn?.setOnClickListener(View.OnClickListener { likepost() })
        like?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@PostDetailsActivity, PostLikedByActivity::class.java)
            intent.putExtra("pid", postId)
            startActivity(intent)
        })
    }

    private fun loadComments() {
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView!!.layoutManager = layoutManager
        commentList = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Posts").child(
            postId!!
        ).child("Comments")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentList?.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelComment: ModelComment? = dataSnapshot1.getValue<ModelComment>(
                        ModelComment::class.java
                    )
                    if (modelComment != null) {
                        commentList?.add(modelComment)
                    }
                    adapterComment =
                        AdapterComment(applicationContext,
                            commentList as MutableList<ModelComment>, myuid!!, postId!!)
                    recyclerView!!.adapter = adapterComment
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun setLikes() {
        val liekeref = FirebaseDatabase.getInstance().reference.child("Likes")
        liekeref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(postId!!).hasChild(myuid!!)) {
                    likebtn!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0)
                    likebtn!!.text = "Liked"
                } else {
                    likebtn!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0)
                    likebtn!!.text = "Like"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun likepost() {
        mlike = true
        val liekeref = FirebaseDatabase.getInstance().reference.child("Likes")
        val postref = FirebaseDatabase.getInstance().reference.child("Posts")
        liekeref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mlike) {
                    mlike = if (dataSnapshot.child(postId!!).hasChild(myuid!!)) {
                        postref.child(postId!!).child("plike").setValue("" + (plike?.toInt() ?:  - 1))
                        liekeref.child(postId!!).child(myuid!!).removeValue()
                        false
                    } else {
                        postref.child(postId!!).child("plike").setValue("" + (plike?.toInt() ?:  + 1))
                        liekeref.child(postId!!).child(myuid!!).setValue("Liked")
                        false
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun postComment() {
        progressDialog!!.setMessage("Adding Comment")
        val commentss: String = comment!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(commentss)) {
            Toast.makeText(this@PostDetailsActivity, "Empty comment", Toast.LENGTH_LONG).show()
            return
        }
        progressDialog!!.show()
        val timestamp = System.currentTimeMillis().toString()
        val datarf = FirebaseDatabase.getInstance().getReference("Posts").child(
            postId!!
        ).child("Comments")
        val hashMap = HashMap<String, Any?>()
        hashMap.put("cId", timestamp)
        hashMap.put("comment", commentss)
        hashMap.put("ptime", timestamp)
        hashMap.put("uid", myuid)
        hashMap.put("uemail", myemail)
        hashMap.put("udp", mydp)
        hashMap.put("uname", myname)
        datarf.child(timestamp).setValue(hashMap).addOnSuccessListener {
            progressDialog!!.dismiss()
            Toast.makeText(this@PostDetailsActivity, "Added", Toast.LENGTH_LONG).show()
            comment!!.setText("")
            updatecommetcount()
        }.addOnFailureListener {
            progressDialog!!.dismiss()
            Toast.makeText(this@PostDetailsActivity, "Failed", Toast.LENGTH_LONG).show()
        }
    }

    var count = false
    private fun updatecommetcount() {
        count = true
        val reference = FirebaseDatabase.getInstance().getReference("Posts").child(
            postId!!
        )
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (count) {
                    val comments = "" + dataSnapshot.child("pcomments").value
                    val newcomment: Int = comments.toInt() + 1
                    reference.child("pcomments").setValue("" + newcomment)
                    count = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun loadUserInfo() {
        val myref: Query = FirebaseDatabase.getInstance().getReference("Users")
        myref.orderByChild("uid").equalTo(myuid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (dataSnapshot1 in dataSnapshot.children) {
                        myname = dataSnapshot1.child("name").value.toString()
                        mydp = dataSnapshot1.child("image").value.toString()
                        try {
                            Glide.with(this@PostDetailsActivity).load(mydp).into(imagep!!)
                        } catch (e: Exception) {
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun loadPostInfo() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        val query = databaseReference.orderByChild("ptime").equalTo(postId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    val ptitle = dataSnapshot1.child("title").value.toString()
                    val descriptions = dataSnapshot1.child("description").value.toString()
                    uimage = dataSnapshot1.child("uimage").value.toString()
                    hisdp = dataSnapshot1.child("udp").value.toString()
                    // hisuid = dataSnapshot1.child("uid").getValue().toString();
                    val uemail = dataSnapshot1.child("uemail").value.toString()
                    hisname = dataSnapshot1.child("uname").value.toString()
                    ptime = dataSnapshot1.child("ptime").value.toString()
                    plike = dataSnapshot1.child("plike").value.toString()
                    val commentcount = dataSnapshot1.child("pcomments").value.toString()
                    val calendar = Calendar.getInstance(Locale.ENGLISH)
                    calendar.timeInMillis = ptime!!.toLong()
                    val timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString()
                    name!!.text = hisname
                    title!!.text = ptitle
                    description!!.text = descriptions
                    like!!.text = "$plike Likes"
                    time!!.text = timedate
                    tcomment!!.text = "$commentcount Comments"
                    if (uimage == "noImage") {
                        image!!.visibility = View.GONE
                    } else {
                        image!!.visibility = View.VISIBLE
                        try {
                            Glide.with(this@PostDetailsActivity).load(uimage).into(image!!)
                        } catch (e: Exception) {
                        }
                    }
                    try {
                        Glide.with(this@PostDetailsActivity).load(hisdp).into(picture!!)
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}