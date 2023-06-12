package com.example.memovie.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.example.memovie.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNav()
    }

    private fun setBottomNav() {
        val navView: BottomNavigationView = binding.navView
        val navController = Navigation.findNavController(binding.navHost)
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null
    }
}