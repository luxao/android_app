package com.example.journey_dp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.navigation.NavController

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

import com.example.journey_dp.databinding.ActivityMainBinding
import com.example.journey_dp.ui.fragments.journey.ProfileFragmentDirections


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if ((intent.action == Intent.ACTION_VIEW).and(intent.data != null)){
            val uri = intent?.data
            if ((uri?.scheme == "https").and(uri?.host == "").and(uri?.path == "/map")){
                val journeyId = uri?.getQueryParameter("id")?.toLong()
                navController.navigate(
                    ProfileFragmentDirections.actionProfileFragment2ToPlanMapFragment(
                        id = journeyId!!
                    )
                )
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}