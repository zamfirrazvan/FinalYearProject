package com.example.fypnewversion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CompletableFuture

class Location(
    var locationName: String,
    var address: String,
    var postcode: String,
    var email: String,
    var phoneNumber: String,
    var locationType: String,
    var description: String,
    var selectedDate: String
) {

    fun clear() {
        locationName = ""
        address = ""
        postcode = ""
        email = ""
        phoneNumber = ""
        locationType = ""
        description = ""
        selectedDate = ""
    }

    fun createLocation(): HashMap<String, String> {
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
        return event
    }

    fun createLocationWithoutDate(): HashMap<String, String> {
        val event = hashMapOf(
            "Location Name" to locationName,
            "Address" to address,
            "Postcode" to postcode,
            "Email" to email,
            "Phone number" to phoneNumber,
            "Location Type" to locationType,
            "Description" to description
        )
        return event
    }

    @JvmName("setLocationName1")
    fun setLocationName(locationName: String) {
        this.locationName = locationName
    }

    @JvmName("getLocationName1")
    fun getLocationName(): String {
        return locationName
    }
}