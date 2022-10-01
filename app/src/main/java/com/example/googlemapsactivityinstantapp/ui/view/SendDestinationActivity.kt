package com.example.googlemapsactivityinstantapp.ui.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.googlemapsactivityinstantapp.R
import com.example.googlemapsactivityinstantapp.ui.MapsActivity
import com.example.googlemapsactivityinstantapp.ui.viewmodel.MapsViewModel

class SendDestinationActivity : AppCompatActivity() {

    private lateinit var longitude: String
    private lateinit var latitude: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_destination)

        findViewById<Button>(R.id.choose_btn).setOnClickListener {
            toMapActivity(this)
        }

        if (intent.getStringExtra("lat").toString() != "null") {
            locationChosen()
        }
    }

    private fun locationChosen() {
        latitude = intent.getStringExtra("lat").toString()
        longitude = intent.getStringExtra("long").toString()
        findViewById<Button>(R.id.send_btn).visibility = View.VISIBLE
        findViewById<Button>(R.id.send_btn).setOnClickListener {
            val viewModel = MapsViewModel()
            viewModel.sendMessage("https://www.googleapp.com/" + latitude + "/" + longitude, this)
        }
    }

    private fun toMapActivity(context: Context) {
        val intent = Intent(context!!, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(context!!, intent, null)
    }
}
