package com.example.fypnewversion

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
class LocationDetailsFragmentTest {

    private lateinit var db: FirebaseFirestore

    @Before
    fun setUp() {
        db = mock(FirebaseFirestore::class.java)
        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .permitAll()
                .build()
        )
    }

    @Test
    fun testSaveButton() {
        val scenario = launchFragmentInContainer<LocationDetailsFragment>()

        scenario.moveToState(Lifecycle.State.CREATED)

        // Find and interact with views using Espresso
        onView(withId(R.id.saveButton)).perform(click())

        scenario.onFragment { fragment ->
            // Verify that the FirebaseFirestore object is called with the expected data
            verify(db).collection("locations").document().set(hashMapOf(
                "Location Name" to "",
                "Address" to "",
                "Postcode" to "",
                "Email" to "",
                "Phone number" to "",
                "Location Type" to "Restaurant",
                "Description" to ""
            ))
        }

        scenario.moveToState(Lifecycle.State.DESTROYED)
    }
}