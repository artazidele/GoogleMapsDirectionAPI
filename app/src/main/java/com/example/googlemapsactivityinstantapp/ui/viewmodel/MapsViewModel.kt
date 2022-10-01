package com.example.googlemapsactivityinstantapp.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.lang.Exception

class MapsViewModel() : ViewModel() {

    private var currentLocation = LatLng(0.0, 0.0)

    fun sendMessage(messageText: String, context: Context) {
        Log.d("MESSAGE: ", messageText)
        val packageManager = context.packageManager
        var whatsAppExists = true
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            Log.d("Whatsapp ", "FOUND")
        } catch (e: Exception) {
            Log.d("Whatsapp ", "NOT FOUND")
            whatsAppExists = false
        }
        if (whatsAppExists == true) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_VIEW
            sendIntent.setData(Uri.parse("http://api.whatsapp.com/send?phone=PHONE_NUMBER&text=" + messageText))
            context.startActivity(sendIntent)
        }
    }

    fun getLocation(activity: Activity, onResult: (LatLng?) -> Unit) {
        viewModelScope.launch {
            if (ActivityCompat.checkSelfPermission(activity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    101)
                onResult(null)
            }

            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                    onResult(currentLocation)
                }
            }
        }
    }

    fun getDirectionURL(origin: LatLng, dest: LatLng): String {
        val link = "https://maps.googleapis.com/maps/api/directions/" +
                "json?origin=" + origin.latitude.toString() + "," + origin.longitude.toString() +
                "&destination=" + dest.latitude.toString() + "," + dest.longitude.toString() +
                "API_KEY"
        return link
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        Log.d("END: ", encoded.length.toString())
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }
}
