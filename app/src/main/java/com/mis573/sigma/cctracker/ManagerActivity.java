package com.mis573.sigma.cctracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class ManagerActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private String userId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }

        String result = "null";
        try {
            result = new FirstNameTask(userId).execute((Void) null).get();
        }
        catch (Exception e) {
            Log.e("CCTracker", "Exception", e);
        }

        TextView tv = (TextView) findViewById(R.id.u_id);
        tv.setText("Welcome " + result + "!");
        addItemsOnSpinner();
    }

    public void addItemsOnSpinner() {
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        try {
            ArrayList<String> employeeList = new GetEmployeeList(userId).execute((Void) null).get();
            for (int i = 0; i < employeeList.size(); i++) {
                String[] employee = employeeList.get(i).split(",");
                categories.add(employee[1]);
            }
        }
        catch (Exception e) {
            Log.e("CCTracker","Exception", e);
            categories.add("error");
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + pos,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
            ManagerActivity.this.startActivity(intent);
        }
        else if (id == R.id.action_exit) {
            //exit application
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //do nothing
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
                String data  = URLEncoder.encode("p_id", "UTF-8")
                        + "=" + URLEncoder.encode(mUserId, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String fname = reader.readLine();
                reader.close();

                if (fname.equals("null")) {
                    return "null";
                }
                else {
                    return fname;
                }

            }
            catch(Exception e){
                Log.e("CCTracker", "Exception", e);
                return "null";
            }
        }
    }

    public class GetEmployeeList extends AsyncTask<Void, Void, ArrayList<String>> {

        private final String mUserId;

        GetEmployeeList(String userId) {
            mUserId = userId;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            try {
                String link="http://www.stchiang.com/mis573/CCTracker/get_employees.php";
                String data  = URLEncoder.encode("manager_id", "UTF-8")
                        + "=" + URLEncoder.encode(mUserId, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                ArrayList<String> list = new ArrayList<String>();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                reader.close();

                return list;
            }
            catch(Exception e){
                Log.e("CCTracker", "Exception", e);
                return new ArrayList<String>();
            }
        }
    }
}
