package com.example.capstoneprojectmd.ui.run

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.capstoneprojectmd.R
import com.example.capstoneprojectmd.databinding.FragmentRunBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import android.graphics.Color
import java.util.concurrent.TimeUnit

class RunFragment : Fragment(R.layout.fragment_run), OnMapReadyCallback {

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var isTracking = false
    private var allLatLng = ArrayList<LatLng>()
    private var boundsBuilder = LatLngBounds.Builder()

    private var startTime: Long = 0L
    private var totalDistance: Float = 0f

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                    getMyLastLocation()
                }
                else -> {
                    Toast.makeText(requireContext(), "Location permissions denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val resolutionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                AppCompatActivity.RESULT_OK -> Log.i(TAG, "onActivityResult: All location settings are satisfied.")
                AppCompatActivity.RESULT_CANCELED -> Toast.makeText(
                    requireContext(),
                    "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentRunBinding.bind(view)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.btnStart.setOnClickListener {
            if (!isTracking) {
                resetTrackingData()
                updateTrackingStatus(true)
                startLocationUpdates()
            } else {
                updateTrackingStatus(false)
                stopLocationUpdates()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getMyLastLocation()
        createLocationRequest()
        createLocationCallback()
    }

    private fun resetTrackingData() {
        totalDistance = 0f
        startTime = 0L
        allLatLng.clear()
        boundsBuilder = LatLngBounds.Builder()
        binding.tvDistance.text = getString(R.string.distance_placeholder)
        binding.tvDuration.text = getString(R.string.duration_placeholder)
        mMap.clear()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    showStartMarker(location)
                } else {
                    Toast.makeText(requireContext(), "Location is not found. Try Again", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(MarkerOptions().position(startLocation).title(getString(R.string.start_point)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(1))
            .apply {
                setMaxUpdateDelayMillis(TimeUnit.SECONDS.toMillis(1))
            }.build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(IntentSenderRequest.Builder(exception.resolution).build())
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(requireContext(), sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val lastLatLng = LatLng(location.latitude, location.longitude)
                    allLatLng.add(lastLatLng)
                    mMap.addPolyline(
                        PolylineOptions()
                            .color(Color.CYAN)
                            .width(10f)
                            .addAll(allLatLng)
                    )
                    boundsBuilder.include(lastLatLng)
                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
                }
            }
        }
    }

    private fun startLocationUpdates() {
        startTime = System.currentTimeMillis()
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (exception: SecurityException) {
            Log.e(TAG, "Error : ${exception.message}")
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)

        val endTime = System.currentTimeMillis()
        val durationInMillis = endTime - startTime
        val durationInMinutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis)
        calculateTotalDistance()

        binding.tvDistance.text = "Jarak: %.2f meter".format(totalDistance)
        binding.tvDuration.text = "Durasi: %d menit".format(durationInMinutes)

        Toast.makeText(
            requireContext(),
            "Jarak: %.2f meter, Durasi: %d menit".format(totalDistance, durationInMinutes),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun calculateTotalDistance() {
        totalDistance = 0f
        for (i in 1 until allLatLng.size) {
            val result = FloatArray(1)
            val startPoint = allLatLng[i - 1]
            val endPoint = allLatLng[i]
            Location.distanceBetween(
                startPoint.latitude, startPoint.longitude,
                endPoint.latitude, endPoint.longitude,
                result
            )
            totalDistance += result[0]
        }
    }

    private fun updateTrackingStatus(newStatus: Boolean) {
        isTracking = newStatus
        binding.btnStart.text = if (isTracking) {
            getString(R.string.stop_running)
        } else {
            getString(R.string.start_running)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "RunFragment"
    }
}
