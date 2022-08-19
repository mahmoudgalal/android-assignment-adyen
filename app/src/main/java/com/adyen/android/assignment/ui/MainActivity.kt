package com.adyen.android.assignment.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.android.assignment.R
import com.adyen.android.assignment.databinding.MainActivityBinding
import com.adyen.android.assignment.ui.adapters.PlacesAdapter
import com.adyen.android.assignment.ui.viewmodels.PlacesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val placesViewModel: PlacesViewModel by viewModels()
    private lateinit var binding: MainActivityBinding
    private lateinit var placesAdapter: PlacesAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the workflow
                Log.d(TAG, "Loading Nearby Places ")
                placesViewModel.loadNearbyPlaces()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.d(TAG, "You have to enable location")
                Snackbar.make(
                    findViewById(R.id.placesRecycler),
                    "You have to enable location permission for the app to work correctly",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Ok") {}.show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        placesViewModel.associatedLifeCycle = lifecycle
        setContentView(view)
        initRecyclerAndObservers()
        checkPermission()
    }

    private fun initRecyclerAndObservers() {
        placesAdapter = PlacesAdapter()
        with(binding.placesRecycler) {
            this.adapter = placesAdapter
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            val divider = DividerItemDecoration(
                this@MainActivity,
                DividerItemDecoration.VERTICAL
            )
            divider.setDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.item_separator
                )!!
            )
            addItemDecoration(divider)
        }
        with(placesViewModel) {
            allPlaces.observe(this@MainActivity) {
                Log.d(TAG, "Updating Places :$it")
                placesAdapter.updatePlacesList(it)
            }
            error.observe(this@MainActivity) {
                showError(it)
            }
            loading.observe(this@MainActivity) {
                binding.progressIndicator.visibility = if (it) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showError(error: String) {
        Snackbar.make(findViewById(R.id.placesRecycler), "Error:$error", Snackbar.LENGTH_INDEFINITE)
            .setAction("Ok") {}
            .show()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged..")
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use location now.
                Log.d(TAG, "Loading Nearby Places ")
                placesViewModel.loadNearbyPlaces()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                AlertDialog.Builder(this).setTitle("Message")
                    .setMessage("This app requires approximate location permission to be able to retrieve all nearby places to you")
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Allow") { dialog, which ->
                        requestPermissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        dialog.dismiss()
                    }
                    .show()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
