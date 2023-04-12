package com.example.fypnewversion

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.fypnewversion.databinding.FragmentDetailedViewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class DetailedViewFragment : Fragment(){
    private lateinit var markId: String
    private lateinit var binding: FragmentDetailedViewBinding
    private var db = Firebase.firestore
    private val storageRef = FirebaseStorage.getInstance().reference
    private lateinit var geocoder: Geocoder
    private lateinit var location: LatLng


    companion object {
        private const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            markId = bundle.getString("markerId").toString()
        }

        val locations = db.collection("locations").document(markId).get().addOnSuccessListener {
            if(it.exists()) {
                val documentData = it.data
                val locationName = documentData?.get("Location Name").toString()
                val address = documentData?.get("Address").toString()
                val postcode = documentData?.get("Postcode").toString()
                val phoneNumber = documentData?.get("Phone number").toString()
                val email = documentData?.get("Email").toString()
                val description = documentData?.get("Description").toString()

                //Get the location from the postcode
                val addresses = geocoder.getFromLocationName(postcode,1)
                location = LatLng(addresses?.get(0)!!.latitude, addresses?.get(0)!!.longitude)

                binding.locationNameLabel.text = locationName
                binding.addressLabel.text = address
                binding.postcodeLabel.text = postcode
                binding.phoneNumberLabel.text = phoneNumber
                binding.emailLabel.text = email
                binding.descriptionLabel.text=description

                if (documentData?.get("Date") !=null){
                    binding.dateLabel.text=documentData.get("Date").toString()
                    binding.dateLabel.isVisible=true
                }
                else {
                    binding.dateLabel.isVisible=false
                }

            } else {
                Toast.makeText(requireContext(),"Data was not found", Toast.LENGTH_SHORT)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Document not found", Toast.LENGTH_SHORT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailedViewBinding.inflate(inflater, container, false)

        binding.locationView.setOnClickListener{
            pickImageFromGallery()
            setLocationPic()
        }
        return binding.root
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geocoder = Geocoder(requireContext(), Locale.UK)

        setLocationPic()

        val callback = OnMapReadyCallback { googleMap ->
            googleMap.addMarker(MarkerOptions().position(location))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,11f))
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setLocationPic() {
        val imageRef = storageRef.child("images/${markId}")

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
                                .into(binding.locationView)
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
            }    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Get the URI of the selected image
            val imageUri = data?.data
            if (imageUri != null) {
                //Delete old images
                val pastImageRef = storageRef.child("images/${markId}")
                pastImageRef.listAll().addOnSuccessListener { listResult ->
                    listResult.items.forEach { file ->
                        file.delete()
                    }
                }
                binding.locationView.setImageURI(imageUri)

                // Upload the image to Firebase Storage
                val imageRef = storageRef.child("images/${markId}/${imageUri.lastPathSegment}")
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
}