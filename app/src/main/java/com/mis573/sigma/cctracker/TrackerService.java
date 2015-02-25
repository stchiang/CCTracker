package com.mis573.sigma.cctracker;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class TrackerService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private boolean mRunning;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private String mLastUpdateTime;

    @Override
    public void onCreate() {
        super.onCreate();

        mRunning = false;

        createLocationRequest();
        buildGoogleApiClient();
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "Tracking Started", Toast.LENGTH_SHORT).show();
        writeLog("Connecting Google API Client...");
        mGoogleApiClient.connect();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Tracking Stopped", Toast.LENGTH_SHORT).show();
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        writeLog("Google API Client disconnected.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        writeLog("Google API Client connected!");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        writeLog("Google API Client connection suspended.");
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        writeLog("Google API Client connection failed.");
        Toast.makeText(this, "Service Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        String text = mLastUpdateTime + " - " + String.valueOf(mCurrentLocation.getLatitude()) +
                ", " + String.valueOf(mCurrentLocation.getLongitude());
        writeLog(text);
    }

    //Other functions

    protected void startLocationUpdates() {
        mRunning = true;
        writeLog("Starting location updates...");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        mRunning = false;
        writeLog("Stopping location updates...");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public boolean isRunning() {
        return mRunning;
    }

    private void writeLog(String text) {
        File logFile = new File("sdcard/tracker_log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}