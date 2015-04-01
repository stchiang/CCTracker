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
    private ArrayList<String> employeeList;
    private String empId;

    private Spinner empSpinner;
    private Spinner dateSpinner;

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

        List<String> entries = new ArrayList<String>();
        try {
            employeeList = new GetEmployeeList(userId).execute((Void) null).get();
            for (int i = 0; i < employeeList.size(); i++) {
                String[] employee = employeeList.get(i).split(",");
                entries.add(employee[1]);
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
        dateSpinner.setVisibility(View.VISIBLE);

        empId = employeeList.get(pos).split(",")[0];


        List<String> entries = new ArrayList<String>();
        entries.add(empId);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, entries);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(dataAdapter);



/*
        Toast.makeText(parent.getContext(), "EmpId : " + empId,
                Toast.LENGTH_SHORT).show();
                */


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
}
