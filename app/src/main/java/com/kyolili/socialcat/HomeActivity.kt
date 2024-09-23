package com.kyolili.socialcat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kyolili.socialcat.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Toolbar
        setSupportActionBar(binding.appBarMain.toolbar)

        // Initialize the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up BottomNavigationView
        binding.appBarMain.contentMain.bottomNavView?.let {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_camera, R.id.navigation_profile
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }

        // Listener to change toolbar visibility and title based on the fragment
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_profile -> {
                    // Show the toolbar and BottomNavigationView with "SocialCat" as title
                    binding.appBarMain.toolbar.visibility = View.VISIBLE
                    binding.appBarMain.contentMain.bottomNavView?.visibility = View.VISIBLE

                    supportActionBar?.apply {
                        title = "SocialCat"
                        setDisplayShowTitleEnabled(true)
                        setDisplayUseLogoEnabled(false) // Remove logo
                    }
                }
                R.id.navigation_camera -> {
                    // Hide toolbar and BottomNavigationView for camera screen
                    binding.appBarMain.toolbar.visibility = View.GONE
                    binding.appBarMain.contentMain.bottomNavView?.visibility = View.GONE
                }
                R.id.postDetailFragment -> {
                    // Show toolbar with the username as title
                    binding.appBarMain.toolbar.visibility = View.VISIBLE
                    binding.appBarMain.contentMain.bottomNavView?.visibility = View.GONE

                    val username = arguments?.getString("username") ?: "Details"
                    supportActionBar?.apply {
                        title = username
                        setDisplayShowTitleEnabled(true)
                        setDisplayUseLogoEnabled(false) // Remove logo
                    }
                }
                else -> {
                    // Show toolbar and BottomNavigationView by default on other fragments
                    binding.appBarMain.toolbar.visibility = View.VISIBLE
                    binding.appBarMain.contentMain.bottomNavView?.visibility = View.VISIBLE

                    supportActionBar?.apply {
                        title = "SocialCat"
                        setDisplayShowTitleEnabled(true)
                        setDisplayUseLogoEnabled(false)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
