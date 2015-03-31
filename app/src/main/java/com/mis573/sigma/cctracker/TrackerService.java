package com.mis573.sigma.cctracker;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class TrackerService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mRunning;
    private String userId = "0";

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
        userId = intent.getStringExtra("userId");
        mGoogleApiClient.connect();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        writeLog("Google API Client disconnected.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Unsupported operation");
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
        Location mCurrentLocation;
        String mLastUpdateTime;
        mCurrentLocation = location;
        mLastUpdateTime = Long.toString(System.currentTimeMillis());

        try {
            Boolean result = new TrackTime(userId, "1", mLastUpdateTime,
                    String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()))
                    .execute((Void) null).get();
            Log.d("userId", userId);
            Log.d("expId", "1");
            Log.d("epoch", mLastUpdateTime);
            Log.d("Latitude", String.valueOf(mCurrentLocation.getLatitude()));
            Log.d("Longitude", String.valueOf(mCurrentLocation.getLongitude()));
            Log.d("result", result.toString());
        }
        catch(Exception e){
            Log.e("CCTracker", "Exception", e);
        }

        String text = mLastUpdateTime + " - " + String.valueOf(mCurrentLocation.getLatitude()) +
                ", " + String.valueOf(mCurrentLocation.getLongitude());
        writeLog(text);
    }

    protected void startLocationUpdates() {
        if (!mRunning) {
            mRunning = true;
            Toast.makeText(this, "Tracking Started", Toast.LENGTH_SHORT).show();
            writeLog("Starting location updates...");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (mRunning) {
            mRunning = false;
            Toast.makeText(this, "Tracking Stopped", Toast.LENGTH_SHORT).show();
            writeLog("Stopping location updates...");
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(15000);  //15 second interval
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

    public class TrackTime extends AsyncTask<Void, Void, Boolean> {

        private String mUserId;
        private String mExpId;
        private String mEpoch;
        private String mLat;
        private String mLon;

        TrackTime(String userId, String expId, String epoch, String lat, String lon) {
            mUserId = userId;
            mExpId = expId;
            mEpoch = epoch;
            mLat = lat;
            mLon = lon;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String link="http://www.stchiang.com/mis573/CCTracker/track_loc.php";
                String data  = URLEncoder.encode("emp_id", "UTF-8")
                        + "=" + URLEncoder.encode(mUserId, "UTF-8")
                        + "&" + URLEncoder.encode("exception_id", "UTF-8")
                        + "=" + URLEncoder.encode(mExpId, "UTF-8")
                        + "&" + URLEncoder.encode("epoch", "UTF-8")
                        + "=" + URLEncoder.encode(mEpoch, "UTF-8")
                        + "&" + URLEncoder.encode("latitude", "UTF-8")
                        + "=" + URLEncoder.encode(mLat, "UTF-8")
                        + "&" + URLEncoder.encode("longitude", "UTF-8")
                        + "=" + URLEncoder.encode(mLon, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                reader.close();

                return true;
            }
            catch(Exception e){
                Log.e("CCTracker", "Exception", e);
                return false;
            }
        }
    }

}