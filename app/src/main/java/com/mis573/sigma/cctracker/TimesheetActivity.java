package com.mis573.sigma.cctracker;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
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

        populateEntries();
    }

    public void populateEntries() {
        final int N = 10; // total number of textviews to add
        LinearLayout ln = (LinearLayout) this.findViewById(R.id.entries);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFF61B329); // Changes this drawbale to use a single color instead of a gradient
        gd.setStroke(3, 0xFF000000);

        for (int i = 0; i < N; i++) {
            // create a new textview
            TextView row = new TextView(this);

            // set some properties of rowTextView or something
            row.setText("  This is row #" + i);
            row.setPadding(0, 8, 0, 8;
            row.setTextSize(18);
            row.setBackground(gd);
            // add the textview to the linearlayout
            ln.addView(row);

        }

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
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
