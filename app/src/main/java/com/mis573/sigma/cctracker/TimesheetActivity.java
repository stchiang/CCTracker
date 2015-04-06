package com.mis573.sigma.cctracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class TimesheetActivity extends ActionBarActivity {

    private String date;
    private String empId;
    private String timesheetIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesheet);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            date = extras.getString("date");
            empId = extras.getString("empId");
            timesheetIds = extras.getString("timesheetIds");
        }

        Toast.makeText(this, date + " " + empId + " " + timesheetIds, Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Intent intent = new Intent(TimesheetActivity.this, LoginActivity.class);
            TimesheetActivity.this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
