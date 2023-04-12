package com.example.fypnewversion

class User(var name: String, var email: String, var points: Int, var locationsAdded: Int, var locationsVisited : Int) {
    fun createUserData(): HashMap<String, *> {
        val user = hashMapOf(
            "Name" to name,
            "Email" to email,
            "Points" to points,
            "Locations Added" to locationsAdded,
            "Locations Visited" to locationsVisited
        )
        return user
    }
}