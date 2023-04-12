package com.example.fypnewversion

import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.fypnewversion.databinding.FragmentLocationDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.HashMap as HashMap1


class LocationDetailsFragment : Fragment() {

    private lateinit var postcode: String
    private lateinit var locationDetailsBinding: FragmentLocationDetailsBinding
    private lateinit var navController: NavController
    private lateinit var functions: FirebaseFunctions
    private var userEmail = FirebaseAuth.getInstance().currentUser?.email.toString().trim()

    private val options = arrayOf(
        "Restaurant", "Charging station", "Recycling center",
        "Garbage bin", "Group activity"
    )
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            postcode = bundle.getString("postcode")!!
        }

        functions = Firebase.functions
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        locationDetailsBinding = FragmentLocationDetailsBinding.inflate(inflater, container, false)

        val today = Calendar.getInstance()
        locationDetailsBinding.datePicker.init(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
        }

        return locationDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        locationDetailsBinding.locationTypeDropdown.adapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, options)
        locationDetailsBinding.locationTypeDropdown.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (locationDetailsBinding.locationTypeDropdown.selectedItem.toString()
                            .equals("Group activity")
                    )
                        locationDetailsBinding.datePicker.isVisible = true
                    else {
                        locationDetailsBinding.datePicker.isVisible = false
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    locationDetailsBinding.instructions.setTextColor(Color.RED)
                }
            }

        locationDetailsBinding.saveButton.setOnClickListener {
            val locationName = locationDetailsBinding.nameTextField.text.toString()
            val address = locationDetailsBinding.addressTextField.text.toString()
            val email = locationDetailsBinding.emailTextField.text.toString()
            val phoneNumber = locationDetailsBinding.phoneTextField.text.toString()
            val locationType = locationDetailsBinding.locationTypeDropdown.selectedItem.toString()
            val description = locationDetailsBinding.descriptionField.text.toString()

            val calendar = Calendar.getInstance().apply {
                set(
                    locationDetailsBinding.datePicker.year,
                    locationDetailsBinding.datePicker.month,
                    locationDetailsBinding.datePicker.dayOfMonth
                )
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = dateFormat.format(calendar.time)
            if (locationDetailsBinding.datePicker.isVisible) {

                val event = hashMapOf(
                    "Location Name" to locationName,
                    "Address" to address,
                    "Postcode" to postcode,
                    "Email" to email,
                    "Phone number" to phoneNumber,
                    "Location Type" to locationType,
                    "Description" to description,
                    "Date" to selectedDate
                )
                db.collection("locations").document().set(event).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Succesfully Added!", Toast.LENGTH_SHORT)
                        .show()

                    navController.navigate(R.id.mapsFragment)
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Failed to add the location!",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            } else {
                val event = hashMapOf(
                    "Location Name" to locationName,
                    "Address" to address,
                    "Postcode" to postcode,
                    "Email" to email,
                    "Phone number" to phoneNumber,
                    "Location Type" to locationType,
                    "Description" to description
                )

                db.collection("locations").document().set(event).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Succesfully Added!", Toast.LENGTH_SHORT)
                        .show()
                    pointUser()
                    navController.navigate(R.id.mapsFragment)
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Failed to add the location!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun pointUser() {
        val db = Firebase.firestore

        val userRef = db.collection("users").document(userEmail)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            if (snapshot.exists()) {
                val locationsAdded = snapshot.getLong("Added") ?: 0
                transaction.update(userRef, "Added", locationsAdded + 1)
            }
            null // return null to indicate transaction completed successfully
        }.addOnSuccessListener {
            // Handle success
        }.addOnFailureListener { e ->
            // Handle error
        }
    }
}