package com.example.googlemapsactivityinstantapp.ui.view

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.googlemapsactivityinstantapp.data.GetDirection
import com.example.googlemapsactivityinstantapp.databinding.ActivityShowMapsBinding
import com.example.googlemapsactivityinstantapp.ui.viewmodel.MapsViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ShowMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityShowMapsBinding
    private val permissionCode = 101

    private lateinit var currentLatLng: LatLng
    private lateinit var destinationLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = intent.data
        if (uri != null) {
            val latitude = uri.pathSegments[uri.pathSegments.size - 2]
            val longitude = uri.pathSegments[uri.pathSegments.size - 1]
            destinationLatLng = LatLng(latitude.toDouble(), longitude.toDouble())
            val viewModel = MapsViewModel()
            viewModel.getLocation(this) { latLng ->
                if (latLng?.latitude != null) {
                    currentLatLng = latLng
                    val mapFragment = supportFragmentManager
                        .findFragmentById(com.example.googlemapsactivityinstantapp.R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val viewModel = MapsViewModel()
                viewModel.getLocation(this) { latLng ->
                    if (latLng?.latitude != null) {
                        currentLatLng = latLng
                        val mapFragment = supportFragmentManager
                            .findFragmentById(com.example.googlemapsactivityinstantapp.R.id.map) as SupportMapFragment
                        mapFragment.getMapAsync(this)
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val markerOptions = MarkerOptions()
            .position(currentLatLng)
            .title("Location now")

        val markerDestinationOptions = MarkerOptions()
            .position(destinationLatLng)
            .title("Destination")
            .draggable(true)

        mMap?.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))
        mMap?.addMarker(markerOptions)
        mMap?.addMarker(markerDestinationOptions)

        val viewModel = MapsViewModel()
        val urll = viewModel.getDirectionURL(currentLatLng, destinationLatLng)
        GetDirection(urll, mMap).execute()
    }
}
