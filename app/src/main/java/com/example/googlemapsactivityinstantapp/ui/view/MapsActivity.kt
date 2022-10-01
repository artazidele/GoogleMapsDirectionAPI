package com.example.googlemapsactivityinstantapp.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.googlemapsactivityinstantapp.R
import com.example.googlemapsactivityinstantapp.data.GetDirection

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.googlemapsactivityinstantapp.databinding.ActivityMapsBinding
import com.example.googlemapsactivityinstantapp.ui.view.SendDestinationActivity
import com.example.googlemapsactivityinstantapp.ui.viewmodel.MapsViewModel
import com.google.android.gms.maps.model.Marker

class MapsActivity : AppCompatActivity(), GoogleMap.OnMarkerDragListener,
    GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val permissionCode = 101

    private lateinit var currentLatLng: LatLng
    private lateinit var destinationLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViewById<Button>(R.id.destination_chosen_btn).setOnClickListener {
            toSendActivity(this)
        }

        val viewModel = MapsViewModel()
        viewModel.getLocation(this) { latLng ->
            if (latLng?.latitude != null) {
                currentLatLng = latLng
                destinationLatLng = LatLng(currentLatLng.latitude, currentLatLng.longitude + 0.005)
                val mapFragment = supportFragmentManager
                    .findFragmentById(com.example.googlemapsactivityinstantapp.R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
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
                        destinationLatLng = LatLng(currentLatLng.latitude, currentLatLng.longitude + 0.005)
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

        updateMap()

        mMap.setOnMarkerClickListener(this)
        mMap.setOnMarkerDragListener(this)
    }

    private fun updateMap() {
        val markerOptions = MarkerOptions()
            .position(currentLatLng)
            .title("My Location")

        val markerDestionOptions = MarkerOptions()
            .position(destinationLatLng)
            .title("Destination")
            .draggable(true)

        mMap?.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))
        mMap?.addMarker(markerOptions)
        mMap?.addMarker(markerDestionOptions)
    }

    override fun onMarkerDrag(p0: Marker) {
        //
    }

    override fun onMarkerDragEnd(p0: Marker) {
        mMap.clear()

        val viewModel = MapsViewModel()
        destinationLatLng = LatLng(p0.position.latitude, p0.position.longitude)
        val urll = viewModel.getDirectionURL(currentLatLng, destinationLatLng)
        updateMap()
        GetDirection(urll, mMap).execute()
    }

    override fun onMarkerDragStart(p0: Marker) {

    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

    private fun toSendActivity(context: Context) {
        val intent = Intent(context!!, SendDestinationActivity::class.java)
        intent.putExtra("lat", destinationLatLng.latitude.toString())
        intent.putExtra("long", destinationLatLng.longitude.toString())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(context!!, intent, null)
    }
}
