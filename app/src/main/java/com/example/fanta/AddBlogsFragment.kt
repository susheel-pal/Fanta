package com.example.fanta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.Manifest
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText
import android.app.ProgressDialog
import com.google.firebase.database.DatabaseReference
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.os.Bundle
//import com.example.fantam.R
import android.content.Intent
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.text.TextUtils
import android.widget.Toast
import android.content.DialogInterface
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
//import com.example.fanta.AddBlogsFragment
import android.content.ContentValues
import android.provider.MediaStore
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.tasks.OnSuccessListener
//import com.example.fanta.DashboardActivity
import com.google.android.gms.tasks.OnFailureListener
import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
//import android.view.View
import android.widget.Button
import android.widget.ImageView
//import androidx.fragment.app.Fragment
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddBlogsFragment : Fragment() {
    var firebaseAuth: FirebaseAuth? = null
    var title: EditText? = null
    var des: EditText? = null
    lateinit var cameraPermission: Array<String>
    lateinit var storagePermission: Array<String>
    var pd: ProgressDialog? = null
    var image: ImageView? = null
    var edititle: String? = null
    var editdes: String? = null
    var editimage: String? = null
    var imageuri: Uri? = null
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var dp: String? = null
    var databaseReference: DatabaseReference? = null
    var upload: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        firebaseAuth = FirebaseAuth.getInstance()
        val view = inflater.inflate(R.layout.fragment_add_blogs, container, false)
        title = view.findViewById(R.id.ptitle)
        des = view.findViewById(R.id.pdes)
        image = view.findViewById(R.id.imagep)
        upload = view.findViewById(R.id.pupload)
        pd = ProgressDialog(context)
        pd!!.setCanceledOnTouchOutside(false)
        //val intent = activity!!.intent
        val intent = requireActivity().intent

        // Retrieving the user data like name ,email and profile pic using query
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val query = databaseReference!!.orderByChild("email").equalTo(email)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    name = dataSnapshot1.child("name").value.toString()
                    email = "" + dataSnapshot1.child("email").value
                    dp = "" + dataSnapshot1.child("image").value.toString()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        // Initialising camera and storage permission
        cameraPermission =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // After click on image we will be selecting an image
        image?.setOnClickListener(View.OnClickListener { showImagePicDialog() })

        // Now we will upload out blog
        upload?.setOnClickListener(View.OnClickListener {
            val titl = "" + title?.getText().toString().trim { it <= ' ' }
            val description = "" + des?.getText().toString().trim { it <= ' ' }

            // If empty set error
            if (TextUtils.isEmpty(titl)) {
                title?.setError("Title Cant be empty")
                Toast.makeText(context, "Title can't be left empty", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }

            // If empty set error
            if (TextUtils.isEmpty(description)) {
                des?.setError("Description Cant be empty")
                Toast.makeText(context, "Description can't be left empty", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }

            // If empty show error
            if (imageuri == null) {
                Toast.makeText(context, "Select an Image", Toast.LENGTH_LONG).show()
                return@OnClickListener
            } else {
                uploadData(titl, description)
            }
        })
        return view
    }

    private fun showImagePicDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Pick Image From")
        builder.setItems(options) { dialog, which -> // check for the camera and storage permission if
            // not given the request for permission
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

    // check for storage permission
    private fun checkStoragePermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
                == PackageManager.PERMISSION_GRANTED)
    }

    // if not given then request for permission after that check if request is given or not
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.size > 0) {
                    val camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    // if request access given the pick data
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromCamera()
                    } else {
                        Toast.makeText(
                            context,
                            "Please Enable Camera and Storage Permissions",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            STORAGE_REQUEST -> {
                if (grantResults.size > 0) {
                    val writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    // if request access given the pick data
                    if (writeStorageaccepted) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(
                            context,
                            "Please Enable Storage Permissions",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    // request for permission to write data into storage
    private fun requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST)
    }

    // check camera permission to click picture using camera
    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val result1 = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    // request for permission to click photo using camera in app
    private fun requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST)
    }

    // if access is given then pick image from camera and then put
    // the imageuri in intent extra and pass to startactivityforresult
    private fun pickFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description")
        imageuri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        val camerIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri)
        startActivityForResult(camerIntent, IMAGE_PICKCAMERA_REQUEST)
    }

    // if access is given then pick image from gallery
    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST)
    }

    // Upload the value of blog data into firebase
    private fun uploadData(titl: String, description: String) {
        // show the progress dialog box
        pd!!.setMessage("Publishing Post")
        pd!!.show()
        val timestamp = System.currentTimeMillis().toString()
        val filepathname = "Posts/post$timestamp"
        val bitmap = (image!!.drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        // initialising the storage reference for updating the data
        val storageReference1 = FirebaseStorage.getInstance().reference.child(filepathname)
        storageReference1.putBytes(data).addOnSuccessListener { taskSnapshot ->
            // getting the url of image uploaded
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isSuccessful);
            val downloadUri = uriTask.result.toString()
            if (uriTask.isSuccessful) {
                // if task is successful the update the data into firebase
                val hashMap = HashMap<Any, String?>()
                hashMap.put("uid", uid)
                hashMap.put("uname", name)
                hashMap.put("uemail", email)
                hashMap.put("udp", dp)
                hashMap.put("title", titl)
                hashMap.put("description", description)
                hashMap.put("uimage", downloadUri)
                hashMap.put("ptime", timestamp)
                hashMap.put("plike", "0")
                hashMap.put("pcomments", "0")

                // set the data into firebase and then empty the title ,description and image data
                val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
                databaseReference.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener {
                        pd!!.dismiss()
                        Toast.makeText(context, "Published", Toast.LENGTH_LONG).show()
                        title!!.setText("")
                        des!!.setText("")
                        image!!.setImageURI(null)
                        imageuri = null
                        startActivity(Intent(context, DashboardActivity::class.java))
                        requireActivity().finish()
                    }.addOnFailureListener {
                        pd!!.dismiss()
                        Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                    }
            }
        }.addOnFailureListener {
            pd!!.dismiss()
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
        }
    }

    // Here we are getting data from image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                imageuri = data!!.data
                image!!.setImageURI(imageuri)
            }
            if (requestCode == IMAGE_PICKCAMERA_REQUEST) {
                image!!.setImageURI(imageuri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val CAMERA_REQUEST = 100
        private const val STORAGE_REQUEST = 200
        private const val IMAGEPICK_GALLERY_REQUEST = 300
        private const val IMAGE_PICKCAMERA_REQUEST = 400
    }
}