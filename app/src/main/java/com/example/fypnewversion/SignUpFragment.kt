package com.example.fypnewversion

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.fypnewversion.databinding.ActivitySignUpBinding
import com.example.fypnewversion.databinding.FragmentSignUpBinding
import com.example.fypnewversion.view.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignUpFragment : Fragment() {

    private lateinit var signUpBinding: FragmentSignUpBinding
    private lateinit var navController: NavController
    private lateinit var firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*        arguments?.let {
        }*/

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signUpBinding = FragmentSignUpBinding.inflate(inflater,container, false)
        return signUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()


        signUpBinding = FragmentSignUpBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()

        signUpBinding.SignIn.setOnClickListener {
            navController.navigate(R.id.action_firstBlankFragment_to_signInFragment)
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
                            //Move to the Sign In screen
                            navController.navigate(R.id.action_firstBlankFragment_to_signInFragment)
                        } else {
                            Toast.makeText(activity, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(activity, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "All fields must be filled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}