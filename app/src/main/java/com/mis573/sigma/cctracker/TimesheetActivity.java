package com.mis573.sigma.cctracker;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TimesheetActivity extends ActionBarActivity {

    private String date;
    private String empId;
    private String timesheetIds;

    private List<String> timesheetIds_list;

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

        getTimesheetArray();
        populateEntries();
    }

    public void getTimesheetArray() {
        timesheetIds_list = new ArrayList<String>(Arrays.asList(timesheetIds.split(" ")));
        Collections.sort(timesheetIds_list, new myComparator());

    }

    public void populateEntries() {
        LinearLayout ln = (LinearLayout) this.findViewById(R.id.entries);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFF61B329); // Changes this drawable to use a single color instead of a gradient
        gd.setStroke(3, 0xFF000000);

        for (int i = 0; i < timesheetIds_list.size(); i++) {
            // create a new textview
            TextView row = new TextView(this);

            // set some properties of rowTextView or something
            row.setText("  " + timesheetIds_list.get(i));
            row.setPadding(0, 8, 0, 8);
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
    public class myComparator implements Comparator<String>{
        public int compare(String s1, String s2) {
            return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));

        }
    }

}
