package com.example.fypnewversion

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.fypnewversion.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ProfileFragment : Fragment() {
    private lateinit var profileBinding: FragmentProfileBinding
    private var db = Firebase.firestore
    private var userEmail = FirebaseAuth.getInstance().currentUser?.email.toString().trim()
    private lateinit var userName: String
    private lateinit var points: String
    private lateinit var locationsAdded: String
    private lateinit var locationsVisited: String
    private val storageRef = FirebaseStorage.getInstance().reference
    companion object {
        private const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileBinding = FragmentProfileBinding.inflate(inflater, container, false)

        profileBinding.imageView2.setOnClickListener {
            pickImageFromGallery()
            setProfilePic()
        }
        return profileBinding.root
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the URI of the selected image
            val imageUri = data?.data
            if (imageUri != null) {
                //Delete old images
                val pastImageRef = storageRef.child("images/${userEmail}")
                pastImageRef.listAll().addOnSuccessListener { listResult ->
                    listResult.items.forEach { file ->
                        file.delete()
                    }
                }

                // Upload the image to Firebase Storage
                val imageRef = storageRef.child("images/${userEmail}/${imageUri.lastPathSegment}")
                imageRef.putFile(imageUri)
                    .addOnSuccessListener { taskSnapshot ->
                        // Image upload successful
                        // Get the download URL of the image
                        imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            val imageUrl = downloadUrl.toString()
                            // Store the image URL in Firebase Firestore or Realtime Database
                            // ...
                        }
                    }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setProfilePic()

        db.collection("users").document(userEmail).get().addOnSuccessListener { result ->
            userName = result.data?.get("Name").toString()
            points = result.data?.get("Points").toString()
            locationsAdded = result.data?.get("Added").toString()
            locationsVisited = result.data?.get("Visited").toString()

            profileBinding.usernameLabel.text = userName
//            profileBinding.pointsLabel.text = "Points earned: " + points
            profileBinding.locationsAddedLabel.text = "Locations added: " + locationsAdded
            profileBinding.placesVisitedLabel.text = "Locations visited: " + locationsVisited

            setLocationAddedBadge(result.data?.get("Added") as Long)
        }
        profileBinding.progressBar.progress = 50
    }

    private fun setLocationAddedBadge(i: Long) {


        if(i>0 && i <10){

            val storage = Firebase.storage

// Get a reference to the image file
            val imageRef = storage.reference.child("images/badges/bronze.png")

            imageRef.downloadUrl.addOnSuccessListener {downloadUri ->
                Glide.with(requireContext())
                    .load(downloadUri)
                    .into(profileBinding.locationsAddedView)
            }
        }
    }

    private fun setProfilePic() {
        val imageRef = storageRef.child("images/${userEmail}")

        imageRef.listAll()
            .addOnSuccessListener { listResult ->
                if (listResult.items.isNotEmpty()) {
                    // Get the first image in the list and load it into the ImageButton
                    val firstImageRef = listResult.items[0]
                    firstImageRef.downloadUrl
                        .addOnSuccessListener { downloadUri ->
                            // Load the image into the ImageButton
                            Glide.with(requireContext())
                                .load(downloadUri)
                                .into(profileBinding.imageView2)
                            profileBinding.textView10.isVisible=false
                        }
                        .addOnFailureListener { exception ->
                            // Handle any errors
                        }
                } else {
                    // There are no images in the directory
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }
}