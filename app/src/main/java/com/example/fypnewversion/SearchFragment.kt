package com.example.fypnewversion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.MultiAutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.fypnewversion.databinding.FragmentSearchBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SearchFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val locationsRef = db.collection("locations")
    private val locationNames = mutableListOf<String>()
    private lateinit var searchBinding: FragmentSearchBinding
    private lateinit var searchBar: MultiAutoCompleteTextView
    val locations = mutableListOf<String>()
    var hashMap : HashMap<String, String> = HashMap()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchBinding = FragmentSearchBinding.inflate(inflater, container, false)

        searchBar = searchBinding.multiAutoCompleteTextView

        navController = findNavController()

        // Initialize the adapter for the search bar
        initSearchBarAdapter()

        return searchBinding.root
    }

    private fun initSearchBarAdapter() {
        // Create an adapter for the search bar
        adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        ) {
            override fun getFilter(): Filter {
                return object : Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        val results = FilterResults()
                        val query = constraint?.toString() ?: ""
                        val filteredLocations = mutableListOf<String>()

                        // Filter the locations based on the user input
                        for (location in locations) {
                            if (location.contains(query, ignoreCase = true)) {
                                filteredLocations.add(location)
                            }
                        }

                        results.values = filteredLocations
                        results.count = filteredLocations.size
                        return results
                    }

                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        if (results != null && results.count > 0) {
                            adapter.clear()
                            adapter.addAll(results.values as MutableList<String>)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }


        // Set the adapter for the search bar
        searchBar.setAdapter(adapter)

        // Set the tokenizer for the search bar
        searchBar.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        // Set the item click listener for the search bar
        searchBar.setOnItemClickListener { parent, view, position, id ->
            // Handle the click event here
            val selectedLocation = parent.getItemAtPosition(position) as String
            openLocationDetails(selectedLocation)
        }
    }

    private fun openLocationDetails(location: String) {
        val bundle = bundleOf("markerId" to hashMap.get(location).toString())
        navController.navigate(R.id.action_searchFragment_to_detailedViewFragment, bundle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firestore = Firebase.firestore

        // Retrieve the locations from Firestore
        firestore.collection("locations").get().addOnSuccessListener { documents ->

            for (document in documents) {
                val documentId = document.id
                val locationName = document.data.get("Location Name").toString()
                val locationPostcode = document.data.get("Postcode").toString()
                val locationDescription = document.data.get("Description").toString()
                val parcelableArray = locationDescription.split("\\s+".toRegex())

                locations.add(locationName)
                locations.add(locationPostcode)
                locations.addAll(locationDescription.split("\\s+".toRegex()))

                hashMap.put(locationName,documentId)
                hashMap.put(locationPostcode,documentId)

                parcelableArray.forEach {
                    hashMap.put(it,documentId)
                }
            }
        }
    }
}