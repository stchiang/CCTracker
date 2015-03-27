package com.mis573.sigma.cctracker;

import android.content.Intent;
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

        String fname = "";

        try {
            String link="http://www.stchiang.com/mis573/CCTracker/get_fname.php";
            String data  = URLEncoder.encode("p_id", "UTF-8")
                    + "=" + URLEncoder.encode(userId, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            fname = reader.readLine();
            reader.close();

        }
        catch(Exception e){
            Log.e("CCTracker", "Exception", e);
        }

        TextView tv = (TextView)findViewById(R.id.u_id);
        tv.setText("Welcome " + fname + "!");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifes.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(intent);
        }
        else if (id == R.id.action_exit) {
            //exit application
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    // Method to start the service
    public void startService(View view) {
        Intent intent = new Intent(this, TrackerService.class);
        startService(intent);
    }

    // Method to stop the service
    public void stopService(View view) {
        Intent intent = new Intent(this, TrackerService.class);
        stopService(intent);
    }

}