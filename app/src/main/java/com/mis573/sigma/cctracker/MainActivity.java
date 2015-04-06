package com.mis573.sigma.cctracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends ActionBarActivity {

    private String userId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        String result = "null";
        try {
            result = new FirstNameTask(userId).execute((Void) null).get();
        }
        catch(Exception e){
            Log.e("CCTracker", "Exception", e);
        }

        TextView tv = (TextView)findViewById(R.id.u_id);
        tv.setText("Welcome " + result + "!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to start the service when button is pressed
    public void startService(View view) {
        Intent intent = new Intent(this, TrackerService.class);
        intent.putExtra("userId", userId);
        startService(intent);
    }

    // Method to stop the service when button is pressed
    public void stopService(View view) {
        Intent intent = new Intent(this, TrackerService.class);
        stopService(intent);
    }

    public class FirstNameTask extends AsyncTask<Void, Void, String> {

        private final String mUserId;

        FirstNameTask(String userId) {
            mUserId = userId;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String link="http://www.stchiang.com/mis573/CCTracker/get_fname.php";
                String data  = URLEncoder.encode("emp_id", "UTF-8")
                        + "=" + URLEncoder.encode(mUserId, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String fName = reader.readLine();
                reader.close();

                if (fName.equals("null")) {
                    return "null";
                }
                else {
                    return fName;
                }

            }
            catch(Exception e){
                Log.e("CCTracker", "Exception", e);
                return "null";
            }
        }
    }
}