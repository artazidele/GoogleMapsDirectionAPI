package com.example.googlemapsactivityinstantapp.data

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import com.example.googlemapsactivityinstantapp.data.model.MapData
import com.example.googlemapsactivityinstantapp.ui.viewmodel.MapsViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

@SuppressLint("StaticFieldLeak")
class GetDirection(val url : String, val mMap: GoogleMap) : AsyncTask<Void, Void, List<List<LatLng>>>() {
    override fun doInBackground(vararg params: Void?): List<List<LatLng>> {

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val data = response.body!!.string()
        val viewModel = MapsViewModel()

        Log.d("END: ", data.toString())

        val result = ArrayList<List<LatLng>>()
        try {
            val respObj = Gson().fromJson(data, MapData::class.java)
            val path = ArrayList<LatLng>()
            for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                path.addAll(viewModel.decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
            }
            result.add(path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    override fun onPostExecute(result: List<List<LatLng>>) {
        val lineoption = PolylineOptions()
        for (i in result.indices) {
            lineoption.addAll(result[i])
            lineoption.width(10f)
            lineoption.color(Color.RED)
            lineoption.geodesic(true)
        }
        mMap.addPolyline(lineoption)
    }
}
