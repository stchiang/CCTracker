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
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ManagerActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private String userId = "0";
    private ArrayList<String> employeeList;
    private ArrayList<String> timesheetList;
    private String empId;

    private Spinner empSpinner;
    private Spinner dateSpinner;

    HashMap<String, ArrayList<String>> timesheets;

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

        empSpinner = (Spinner)findViewById(R.id.emp_spinner);
        dateSpinner = (Spinner)findViewById(R.id.date_spinner);
        dateSpinner.setVisibility(View.INVISIBLE);
        addItemsOnSpinner();
    }

    public void addItemsOnSpinner() {
        empSpinner.setOnItemSelectedListener(this);
        dateSpinner.setOnItemSelectedListener(this);

        List<String> entries = new ArrayList<String>();
        try {
            employeeList = new GetEmployeeList(userId).execute((Void) null).get();
            for (int i = 0; i < employeeList.size(); i++) {
                entries.add(employeeList.get(i).split(",")[1]);
            }
        }
        catch (Exception e) {
            Log.e("CCTracker","Exception", e);
            entries.add("error");
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, entries);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        empSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.emp_spinner) {
            dateSpinner.setVisibility(View.VISIBLE);

            empId = employeeList.get(pos).split(",")[0];
            List<String> entries = new ArrayList<String>();

            //initialze timesheets hashmap each time a new employee is selected from emp_spinner
            timesheets = new HashMap<String, ArrayList<String>>();

            try {
                timesheetList = new GetTimesheets(empId).execute((Void) null).get();
                for (int i = 0; i < timesheetList.size(); i++) {
                    String timeId = timesheetList.get(i).split(",")[0];
                    String epoch = timesheetList.get(i).split(",")[1];
                    String[] date = new Date(Long.parseLong(epoch)).toString().split(" ");
                    String formatted_date = date[1] + " " + date[2] + " " + date[5];

                    if (timesheets.get(formatted_date) == null) {
                        ArrayList<String> temp = new ArrayList<String>();
                        temp.add(timeId);
                        timesheets.put(formatted_date, temp);
                        entries.add(formatted_date);
                    }
                    else {
                        timesheets.get(formatted_date).add(timeId);
                    }
                }
            }
            catch (Exception e) {
                Log.e("CCTracker","Exception", e);
                entries.add("error");
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, entries);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dateSpinner.setAdapter(dataAdapter);
        }
        else if (spinner.getId() == R.id.date_spinner) {
            //Toast.makeText(this, "position: " + pos, Toast.LENGTH_SHORT).show();
        }
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
        /*
        else if (id == R.id.action_exit) {
            //exit application
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    // Method to generate timesheet when button is pressed
    public void generateTimesheet(View view) {
        ArrayList<String> timesheetIds = timesheets.get(dateSpinner.getSelectedItem().toString());
        String temp = "";
        for (int i=0; i < timesheetIds.size(); i++) {
            temp += timesheetIds.get(i) + " ";
        }
        Toast.makeText(this, "Timesheet IDs: " + temp, Toast.LENGTH_LONG).show();
    }

    //ASyncTask classes for interfacing with MySQL database

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

    public class GetTimesheets extends AsyncTask<Void, Void, ArrayList<String>> {

        private final String mUserId;

        GetTimesheets(String userId) {
            mUserId = userId;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            try {
                String link="http://www.stchiang.com/mis573/CCTracker/get_timesheets.php";
                String data  = URLEncoder.encode("employee_id", "UTF-8")
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
