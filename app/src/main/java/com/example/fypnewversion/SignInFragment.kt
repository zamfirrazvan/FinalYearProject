package com.example.fypnewversion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.fypnewversion.databinding.FragmentSignInBinding
import com.example.fypnewversion.databinding.FragmentSignUpBinding
import com.example.fypnewversion.view.MainActivity
import com.example.fypnewversion.view.SignUpActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignInFragment : Fragment() {

    private lateinit var signInBinding: FragmentSignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*        arguments?.let {

        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInBinding = FragmentSignInBinding.inflate(inflater,container,false)
        return signInBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController=findNavController()
        signInBinding = FragmentSignInBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()

        signInBinding.signUpButton.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_firstBlankFragment2)
        }

        signInBinding.signInButton.setOnClickListener {
            val email = signInBinding.emailLogin.text.toString()
            val pass = signInBinding.passwordLogin.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        navController.navigate(R.id.action_signInFragment_to_mapsFragment)
                    } else {
                        Toast.makeText(activity, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(activity, "All fields must be filled", Toast.LENGTH_SHORT).show()
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)


        signInBinding.googleButton.setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }
    private fun handleResults(task: Task<GoogleSignInAccount>?) {
        if (task!!.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if(account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(activity, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful)
            {
                navController.navigate(R.id.action_signInFragment_to_mapsFragment)
            } else {
                Toast.makeText(activity, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}