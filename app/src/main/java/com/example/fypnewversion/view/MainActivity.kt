package com.example.fypnewversion.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fypnewversion.R
import com.example.fypnewversion.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.bottomNavigationView.background = null

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = mainBinding.bottomNavigationView
        val menu = bottomNavigationView.menu

        /*
        All the buttons from the bottom menu are getting indexed
         */
        val homeItem = menu.getItem(0)
        val searchItem = menu.getItem(1)
        val scanItem = menu.getItem(3)
        val settingsItem = menu.getItem(4)

        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                homeItem.itemId -> {
                    navController.navigate(R.id.mapsFragment)
                }
                searchItem.itemId -> {
                    navController.navigate(R.id.searchFragment)
                }
                scanItem.itemId -> {
                    navController.navigate(R.id.scanFragment)
                }
                settingsItem.itemId -> {
                    navController.navigate(R.id.settingsFragment)
                }
            }
            true
        }

        mainBinding.addButton.setOnClickListener {
            navController.navigate(R.id.addLocationFragment)
        }
    }
}