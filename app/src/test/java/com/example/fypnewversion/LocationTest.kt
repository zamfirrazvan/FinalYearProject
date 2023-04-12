package com.example.fypnewversion

import org.junit.Test

import org.junit.Assert.*

class LocationTest {

    @Test
    fun getLocationName1() {
        val location = Location("test","address","postcode","email","phone","type","desc","");
        location.setLocationName("test")
        assertEquals("test", location.getLocationName())
    }

    @Test
    fun createLocationWithoutDate() {
        val location = Location("loc name","address","postcode","email","phone","type","desc","");
        val locationMap = location.createLocationWithoutDate()
        assertEquals(7, locationMap.size)
        assertEquals("loc name", locationMap["Location Name"]);
        assertEquals(null, locationMap["Date"]);
    }

    @Test
    fun createLocation() {
        val location = Location("loc name","address","postcode","email","phone","type","desc","01/01/2021");
        val locationMap = location.createLocation()
        assertEquals(8, locationMap.size)
        assertEquals("loc name", locationMap["Location Name"]);
        assertEquals("01/01/2021", locationMap["Date"]);
    }

    @Test
    fun clearLocation() {
        val location = Location("loc name","address","postcode","email","phone","type","desc","");
        location.clear()
        assertEquals("", location.getLocationName())
    }
}