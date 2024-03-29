package com.example.journey_dp

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.navigation.NavController

import androidx.navigation.fragment.NavHostFragment

import com.example.journey_dp.databinding.ActivityMainBinding
import com.example.journey_dp.ui.fragments.journey.PlanJourneyFragmentDirections

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
            if ((uri?.scheme == "https").and(uri?.host == "planjourney").and(uri?.path == "/map")){
                val sharedUrl = uri?.getQueryParameter("details")
                navController.navigate(
                    PlanJourneyFragmentDirections.actionPlanJourneyFragmentToPlanMapFragment(
                        id = 0L,
                        shared = sharedUrl!!,
                        flag = "share"
                    )
                )
            }
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}