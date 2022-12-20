package com.android.parth

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*


class Utils {


    companion object {


        private val TAG: String = "LocationService"

        private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
        private val INTERVAL: Long = 2000
        private val FASTEST_INTERVAL: Long = 1000
        lateinit var mLastLocation: Location
        internal lateinit var mLocationRequest: LocationRequest
        private val MY_PERMISSIONS_REQUEST_LOCATION = 99
        lateinit var locationInterface: LocationInterface


        fun displayLocationSettingsRequest(context: Context, activity: Activity) {
            val googleApiClient = GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build()
            googleApiClient.connect()
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 10000
            locationRequest.fastestInterval = 10000 / 2.toLong()
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)
            val result: PendingResult<LocationSettingsResult> =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback { result ->
                val status: Status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                        Log.i("SelectLocation", "All location settings are satisfied.")

                        getLocationData()


                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            "SelectLocation",
                            "Location settings are not satisfied. Show the user a dialog to upgrade location settings"
                        )
                        try {
                            status.startResolutionForResult(
                                activity,
                                MY_PERMISSIONS_REQUEST_LOCATION
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Log.i("SelectLocation", "PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                        "SelectLocation",
                        "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                    )

                    LocationSettingsStatusCodes.CANCELED -> {
                        Log.i(
                            "SelectLocation",
                            "Cancelled"
                        )
                    }
                }
            }
        }

        private fun getLocationData() {
            locationInterface = (ApplicationClass.mInstance.activity) as LocationInterface

            mLocationRequest = LocationRequest()
            startLocationUpdates()
        }

        private fun startLocationUpdates() {
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest.interval = INTERVAL
            mLocationRequest.fastestInterval = FASTEST_INTERVAL

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest)
            val locationSettingsRequest = builder.build()

            val settingsClient =
                LocationServices.getSettingsClient(ApplicationClass.mInstance.activity)
            settingsClient.checkLocationSettings(locationSettingsRequest)

            mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(ApplicationClass.mInstance.activity)


            if (ActivityCompat.checkSelfPermission(
                    ApplicationClass.mInstance.activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    ApplicationClass.mInstance.activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            Looper.myLooper()?.let {
                mFusedLocationProviderClient!!.requestLocationUpdates(
                    mLocationRequest, mLocationCallback,
                    it
                )
            }
        }

        private val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                locationResult.lastLocation
                onLocationChanged(locationResult.lastLocation)
            }
        }

        fun onLocationChanged(location: Location) {

            mLastLocation = location
            Log.d(TAG, "${mLastLocation.latitude}")
            Log.d(TAG, "${mLastLocation.longitude}")

            Toast.makeText(
                ApplicationClass.mInstance.activity,
                "Location LatLng:-" + mLastLocation.latitude + " : " + mLastLocation.longitude,
                Toast.LENGTH_LONG
            ).show()

            locationInterface.onLocationChange(mLastLocation.latitude, mLastLocation.longitude)

            mFusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
        }

    }



}
