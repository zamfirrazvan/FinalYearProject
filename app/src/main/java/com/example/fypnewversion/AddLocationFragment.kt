package com.example.fypnewversion

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.fypnewversion.databinding.FragmentAddLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


class AddLocationFragment : Fragment() {

    private lateinit var geocoder: Geocoder
    private lateinit var addLocationBinding: FragmentAddLocationBinding
    private lateinit var navController: NavController
    private var ukLatLng = LatLng(53.20268553895021, -1.870717970872283)

    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ukLatLng, 7f))

        /*
        Listener when a location is selected on the map. It's later converted to a geocoder postcode
         */
        googleMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng) {
                googleMap.clear()
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7f))
                googleMap.addMarker(MarkerOptions().position(latLng))
               /* geocoder.getFromLocation(latLng.latitude,latLng.longitude,100,object: Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addLocationBinding.spinner.isNotEmpty()) {
                            addLocationBinding.spinner.adapter = null
                        }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            addresses
                        )
                        addLocationBinding.spinner.adapter = adapter
                    }

                    override fun onError(errorMessage: String?) {
                        super.onError(errorMessage)
                    }
                })*/
                val address = geocoder.getFromLocation(latLng.latitude,latLng.longitude,10)
                /*if (addLocationBinding.spinner.isNotEmpty()) {
                    addLocationBinding.spinner.adapter = null
                }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    addresses!!.toMutableList()
                )
                addLocationBinding.spinner.adapter = adapter
                addLocationBinding.addressField.setText(addresses?.get(0)?.getAddressLine(0).toString())*/
                addLocationBinding.postcodeField.setText(address?.get(0)?.postalCode.toString())
            }
        })

        addLocationBinding.postcodeField.doAfterTextChanged {
            googleMap.clear()
            val postcode = addLocationBinding.postcodeField.text.toString()
            try {
                val addresses = geocoder.getFromLocationName(postcode, 1)
                if (addresses != null && !addresses.isEmpty()) {
                    val address = addresses[0]
                    googleMap.addMarker(MarkerOptions().position(LatLng(address.latitude,address.longitude)))
                } else {
                    Toast.makeText(requireContext(), "Unable to geocode zipcode", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                // handle exception
            }
        }
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
        addLocationBinding = FragmentAddLocationBinding.inflate(inflater, container, false)
        return addLocationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        navController = findNavController()

        geocoder = Geocoder(requireContext(), Locale.UK)

        addLocationBinding.postcodeField.isVisible = true

        addLocationBinding.submitButton.setOnClickListener {
            var bundle = bundleOf("postcode" to addLocationBinding.postcodeField.text.toString())
            navController.navigate(R.id.action_addLocationFragment_to_locationDetailsFragment, bundle)
        }
    }
}