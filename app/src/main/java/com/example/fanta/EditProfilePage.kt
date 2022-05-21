package com.example.fanta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import android.app.ProgressDialog
//import com.example.fanta.R
import com.google.firebase.storage.FirebaseStorage
import com.bumptech.glide.Glide
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.annotation.RequiresApi
import android.os.Build
import com.example.fanta.EditProfilePage
import android.view.LayoutInflater
import android.text.TextUtils
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import android.content.DialogInterface
import android.content.Intent
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.google.firebase.database.*
//import com.google.firebase.database.R
import com.google.firebase.storage.UploadTask
import java.util.*

class EditProfilePage() : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    var firebaseUser: FirebaseUser? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
    var storageReference: StorageReference? = null
    var storagepath = "Users_Profile_Cover_image/"
    var uid: String? = null
    var set: ImageView? = null
    var profilepic: TextView? = null
    var editname: TextView? = null
    var editpassword: TextView? = null
    var pd: ProgressDialog? = null
    lateinit var cameraPermission: Array<String>
    lateinit var storagePermission: Array<String>
    var imageuri: Uri? = null
    var profileOrCoverPhoto: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_page)
        profilepic = findViewById(R.id.profilepic)
        editname = findViewById(R.id.editname)
        set = findViewById(R.id.setting_profile_image)
        pd = ProgressDialog(this)
        pd!!.setCanceledOnTouchOutside(false)
        editpassword = findViewById(R.id.changepassword)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        databaseReference = firebaseDatabase!!.getReference("Users")
        cameraPermission =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val query: Query = databaseReference!!.orderByChild("email").equalTo(
            firebaseUser!!.email
        )
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1: DataSnapshot in dataSnapshot.children) {
                    val image: String = "" + dataSnapshot1.child("image").value
                    try {
                        Glide.with(this@EditProfilePage).load(image).into((set)!!)
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        editpassword?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                pd!!.setMessage("Changing Password")
                showPasswordChangeDailog()
            }
        })
        profilepic?.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onClick(v: View) {
                pd!!.setMessage("Updating Profile Picture")
                profileOrCoverPhoto = "image"
                showImagePicDialog()
            }
        })
        editname?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                pd!!.setMessage("Updating Name")
                showNamephoneupdate("name")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        val query: Query = databaseReference!!.orderByChild("email").equalTo(
            firebaseUser!!.email
        )
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1: DataSnapshot in dataSnapshot.children) {
                    val image: String = "" + dataSnapshot1.child("image").value
                    try {
                        Glide.with(this@EditProfilePage).load(image).into((set)!!)
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        editpassword!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                pd!!.setMessage("Changing Password")
                showPasswordChangeDailog()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val query: Query = databaseReference!!.orderByChild("email").equalTo(
            firebaseUser!!.email
        )
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1: DataSnapshot in dataSnapshot.children) {
                    val image: String = "" + dataSnapshot1.child("image").value
                    try {
                        Glide.with(this@EditProfilePage).load(image).into((set)!!)
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        editpassword!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                pd!!.setMessage("Changing Password")
                showPasswordChangeDailog()
            }
        })
    }

    // checking storage permission ,if given then we can add something in our storage
    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
    }

    // requesting for storage permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST)
    }

    // checking camera permission ,if given then we can click image using our camera
    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == (PackageManager.PERMISSION_GRANTED)
        val result1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
        return result && result1
    }

    // requesting for camera permission if not given
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST)
    }

    // We will show an alert box where we will write our old and new password
    private fun showPasswordChangeDailog() {
        val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_update_password, null)
        val oldpass: EditText = view.findViewById(R.id.oldpasslog)
        val newpass: EditText = view.findViewById(R.id.newpasslog)
        val editpass: Button = view.findViewById(R.id.updatepass)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(view)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        editpass.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val oldp: String = oldpass.text.toString().trim { it <= ' ' }
                val newp: String = newpass.text.toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(oldp)) {
                    Toast.makeText(
                        this@EditProfilePage,
                        "Current Password cant be empty",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                if (TextUtils.isEmpty(newp)) {
                    Toast.makeText(
                        this@EditProfilePage,
                        "New Password cant be empty",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                dialog.dismiss()
                updatePassword(oldp, newp)
            }
        })
    }

    // Now we will check that if old password was authenticated
    // correctly then we will update the new password
    private fun updatePassword(oldp: String, newp: String) {
        pd!!.show()
        val user = firebaseAuth!!.currentUser
        val authCredential = EmailAuthProvider.getCredential((user!!.email)!!, oldp)
        user.reauthenticate(authCredential)
            .addOnSuccessListener {
                user.updatePassword(newp)
                    .addOnSuccessListener {
                        pd!!.dismiss()
                        Toast.makeText(this@EditProfilePage, "Changed Password", Toast.LENGTH_LONG)
                            .show()
                    }.addOnFailureListener {
                        pd!!.dismiss()
                        Toast.makeText(this@EditProfilePage, "Failed", Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener {
                pd!!.dismiss()
                Toast.makeText(this@EditProfilePage, "Failed", Toast.LENGTH_LONG).show()
            }
    }

    // Updating name
    private fun showNamephoneupdate(key: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Update$key")

        // creating a layout to write the new name
        val layout: LinearLayout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)
        val editText: EditText = EditText(this)
        editText.hint = "Enter$key"
        layout.addView(editText)
        builder.setView(layout)
        builder.setPositiveButton("Update", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                val value: String = editText.text.toString().trim { it <= ' ' }
                if (!TextUtils.isEmpty(value)) {
                    pd!!.show()

                    // Here we are updating the new name
                    val result: HashMap<String, Any> = HashMap()
                    result.put(key, value)
                    databaseReference!!.child(firebaseUser!!.uid).updateChildren(result)
                        .addOnSuccessListener(object : OnSuccessListener<Void?> {
                            override fun onSuccess(aVoid: Void?) {
                                pd!!.dismiss()

                                // after updated we will show updated
                                Toast.makeText(this@EditProfilePage, " updated ", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }).addOnFailureListener(object : OnFailureListener {
                            override fun onFailure(e: Exception) {
                                pd!!.dismiss()
                                Toast.makeText(
                                    this@EditProfilePage,
                                    "Unable to update",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    if ((key == "name")) {
                        val databaser: DatabaseReference =
                            FirebaseDatabase.getInstance().getReference("Posts")
                        val query: Query = databaser.orderByChild("uid").equalTo(uid)
                        query.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (dataSnapshot1: DataSnapshot in dataSnapshot.children) {
                                    val child: String? = databaser.key
                                    dataSnapshot1.ref.child("uname").setValue(value)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
                    }
                } else {
                    Toast.makeText(this@EditProfilePage, "Unable to update", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                pd!!.dismiss()
            }
        })
        builder.create().show()
    }

    // Here we are showing image pic dialog where we will select
    // and image either from camera or gallery
    @RequiresApi(Build.VERSION_CODES.M)
    private fun showImagePicDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Image From")
        builder.setItems(options) { dialog, which -> // if access is not given then we will request for permission
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission()
                } else {
                    pickFromCamera()
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }
        builder.create().show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                //assert data != null;        //adding now
                imageuri = data!!.data
                uploadProfileCoverPhoto(imageuri)
            }
            if (requestCode == IMAGE_PICKCAMERA_REQUEST) {
                uploadProfileCoverPhoto(imageuri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.size > 0) {
                    val camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromCamera()
                    } else {
                        Toast.makeText(
                            this,
                            "Please Enable Camera and Storage Permissions",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            STORAGE_REQUEST -> {
                if (grantResults.size > 0) {
                    val writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (writeStorageaccepted) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    // Here we will click a photo and then go to startactivityforresult for updating data
    private fun pickFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description")
        imageuri =
            this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val camerIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri)
        startActivityForResult(camerIntent, IMAGE_PICKCAMERA_REQUEST)
    }

    // We will select an image from gallery
    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST)
    }

    // We will upload the image from here.
    private fun uploadProfileCoverPhoto(uri: Uri?) {
        pd!!.show()

        // We are taking the filepath as storagepath + firebaseauth.getUid()+".png"
        val filepathname = storagepath + "" + profileOrCoverPhoto + "_" + firebaseUser!!.uid
        val storageReference1 = storageReference!!.child(filepathname)
        storageReference1.putFile((uri)!!)
            .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot) {
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);

                    // We will get the url of our image using uritask
                    val downloadUri = uriTask.result
                    if (uriTask.isSuccessful) {

                        // updating our image url into the realtime database
                        val hashMap = HashMap<String?, Any>()
                        hashMap.put(profileOrCoverPhoto, downloadUri.toString())
                        databaseReference!!.child(firebaseUser!!.uid).updateChildren(hashMap)
                            .addOnSuccessListener(
                                OnSuccessListener {
                                    pd!!.dismiss()
                                    Toast.makeText(
                                        this@EditProfilePage,
                                        "Updated",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }).addOnFailureListener(object : OnFailureListener {
                                override fun onFailure(e: Exception) {
                                    pd!!.dismiss()
                                    Toast.makeText(
                                        this@EditProfilePage,
                                        "Error Updating ",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                    } else {
                        pd!!.dismiss()
                        Toast.makeText(this@EditProfilePage, "error", Toast.LENGTH_LONG).show()
                    }
                }
            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    pd!!.dismiss()
                    Toast.makeText(this@EditProfilePage, "Error", Toast.LENGTH_LONG).show()
                }
            })
    }

    companion object {
        private val CAMERA_REQUEST = 100
        private val STORAGE_REQUEST = 200
        private val IMAGEPICK_GALLERY_REQUEST = 300
        private val IMAGE_PICKCAMERA_REQUEST = 400
    }
}