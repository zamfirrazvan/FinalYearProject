package com.example.fypnewversion.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fypnewversion.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpBinding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        functions = Firebase.functions

        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signUpBinding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        signUpBinding.SignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        signUpBinding.signUp.setOnClickListener {
            val email = signUpBinding.emailBox.text.toString()
            val pass = signUpBinding.passwordBox.text.toString()
            val confirmPass = signUpBinding.passwordBox2.text.toString()
            val name = signUpBinding.nameBox.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass.equals(confirmPass)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {

                            //Register in the database
                            val user = hashMapOf(
                                "Name" to name,
                                "Email" to email,
                                "Points" to 0,
                                "Visited" to 0,
                                "Added" to 0
                            )

                            db.collection("users").document(email).set(user).addOnSuccessListener {
                                Toast.makeText(this,"Succesfully Added!", Toast.LENGTH_SHORT).show()
                            }

                            //Move to the Sign In screen
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callAddUserToFirestoreLogin(name: String, email: String) : Task<String> {
        val data = hashMapOf("name" to name, "email" to email)

        return functions.getHttpsCallable("addUserToFirestoreLogin").call(data)
            .continueWith { task ->
            val result = task.result?.data as String
                result
        }
    }
}