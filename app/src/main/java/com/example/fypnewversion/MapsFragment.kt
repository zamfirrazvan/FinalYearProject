package com.example.fypnewversion

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.fypnewversion.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.math.max


class MapsFragment : Fragment(), OnMapReadyCallback {

    private var vectorDrawable: Drawable? = null
    private lateinit var mapsBinding: FragmentMapsBinding
    private lateinit var navController: NavController
    private var db = Firebase.firestore
    private lateinit var geocoder: Geocoder
    private lateinit var client: FusedLocationProviderClient
    private lateinit var clientLocation: LatLng
    private var currentLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap


//    private val callback = OnMapReadyCallback { googleMap ->
//
//        with(googleMap.uiSettings) {
//            isZoomControlsEnabled = true
//        }
//
//        val locations = db.collection("locations").get().addOnSuccessListener { result ->
//            for (mark in result) {
//                val postcode = mark.data.get("Postcode").toString()
//                val addresses = geocoder.getFromLocationName(postcode, 1)
//                val address = addresses?.get(0)
//                if (address != null) {
//                    googleMap.addMarker(
//                        MarkerOptions().position(
//                            LatLng(
//                                address.latitude,
//                                address.longitude
//                            )
//                        )
//                    )
//                    googleMap.moveCamera(
//                        CameraUpdateFactory.newLatLng(
//                            LatLng(
//                                address.latitude,
//                                address.longitude
//                            )
//                        )
//                    )
//                }
//
//            }
//        }
//
//        mapsBinding.locationButton.setOnClickListener {
//            if (checkLocationPermission() != null) {
//                googleMap.addMarker(MarkerOptions().position(clientLocation!!))
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clientLocation,15f))
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        fusedLocationProviderClient =
//            LocationServices.getFusedLocationProviderClient(requireActivity())
//    }
//
//    private fun checkLocationPermission(): LatLng? {
//        val task = fusedLocationProviderClient.lastLocation
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            )
//            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                101
//            )
//            clientLocation = LatLng(task.result.latitude, task.result.longitude)
//            return clientLocation
//        }
//        task.addOnSuccessListener {
//            if (it != null) {
//                Toast.makeText(
//                    requireContext(),
//                    "${it.latitude} ${it.latitude}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//        return null
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        mapsBinding = FragmentMapsBinding.inflate(inflater, container, false)
//        client = LocationServices.getFusedLocationProviderClient(activity)
//        return mapsBinding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        geocoder = Geocoder(requireContext(), Locale.UK)
//        mapFragment?.getMapAsync(callback)
//
//        navController = findNavController()
//
//        mapsBinding.profileButton.setOnClickListener {
//            navController.navigate(R.id.action_mapsFragment_to_profileFragment)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapsBinding = FragmentMapsBinding.inflate(inflater, container, false)
        client = LocationServices.getFusedLocationProviderClient(activity)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return mapsBinding.root
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        with(googleMap.uiSettings) {
            isZoomGesturesEnabled = true
        }

        val locations = db.collection("locations").get().addOnSuccessListener { result ->
            for (mark in result) {
                val markId = mark.id
                val postcode = mark.data.get("Postcode").toString()
                val type = mark.data.get("Location Type").toString()
                val addresses = geocoder.getFromLocationName(postcode, 1)
                val address = addresses?.get(0)
                if (address != null) {

                    val addressLatLng = LatLng(address.latitude, address.longitude)

                    googleMap.addMarker(
                        MarkerOptions().position(addressLatLng)
                            .icon(BitmapFromVector(requireContext(), type))
                    )?.title = markId

//                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(addressLatLng))
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addressLatLng,5.7f))

                    googleMap.setOnMarkerClickListener {
                        val bundle = bundleOf("markerId" to it.title)
                        navController.navigate(
                            R.id.action_mapsFragment_to_detailedViewFragment,
                            bundle
                        )
                        true
                    }
                }
            }
        }

        mapsBinding.locationButton.setOnClickListener {
            checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        val priority = LocationRequest.QUALITY_HIGH_ACCURACY
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationProviderClient.getCurrentLocation(priority, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                Log.d("Location", "location is found: $location")
            }
            .addOnFailureListener { exception ->
                Log.d("Location", "Oops location failed with exception: $exception")
            }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                clientLocation = LatLng(location.latitude, location.longitude)
                Toast.makeText(
                    requireContext(),
                    "${location.latitude} ${location.longitude}",
                    Toast.LENGTH_SHORT
                ).show()

                this.googleMap.addMarker(MarkerOptions().position(clientLocation))
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clientLocation, 15f))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(requireContext(), Locale.UK)
        navController = findNavController()

        mapsBinding.profileButton.setOnClickListener {
            navController.navigate(R.id.action_mapsFragment_to_profileFragment)
        }
    }

    private fun BitmapFromVector(context: Context, locationType: String): BitmapDescriptor? {
        // below line is use to generate a drawable.

        when(locationType){
            "Restaurant" -> vectorDrawable = ContextCompat.getDrawable(context, R.drawable.baseline_restaurant)
            "Charging station" -> vectorDrawable = ContextCompat.getDrawable(context, R.drawable.charging_station_solid)
            "Recycling center" -> vectorDrawable = ContextCompat.getDrawable(context, R.drawable.baseline_recycling_24)
            "Garbage bin" -> vectorDrawable = ContextCompat.getDrawable(context, R.drawable.trash_solid)
            "Group activity" -> vectorDrawable = ContextCompat.getDrawable(context, R.drawable.baseline_groups)
        }


        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable!!.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable!!.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable!!.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}