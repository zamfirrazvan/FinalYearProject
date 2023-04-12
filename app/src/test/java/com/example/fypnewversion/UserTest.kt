package com.example.fypnewversion

import org.junit.Test

import org.junit.Assert.*

class UserTest {

    @Test
    fun creaTeUserData() {
        val user = User("John", "test@yahoo.com", 0, 0, 0);
        val userMap = user.createUserData()
        assertEquals(userMap["Name"], "John")
    }
}