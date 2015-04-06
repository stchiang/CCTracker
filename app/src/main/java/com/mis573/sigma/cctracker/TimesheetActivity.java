package com.mis573.sigma.cctracker;

import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class TimesheetActivity extends ActionBarActivity {

    private String empId;
    private String timesheetIds;

    private List<String> timesheetIds_values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesheet);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            empId = extras.getString("empId");
            timesheetIds = extras.getString("timesheetIds");
        }

        getTimesheetArray();
        populateEntries();
    }

    public void getTimesheetArray() {
        timesheetIds_values = new ArrayList<String>();
        List<String> timesheetIds_list = new ArrayList<String>(Arrays.asList(timesheetIds.split(" ")));
        Collections.sort(timesheetIds_list, new myComparator());

        for (int i = 0; i < timesheetIds_list.size(); i++) {
            try {
                timesheetIds_values.add(new GetTimesheetEntry(timesheetIds_list.get(i)).execute((Void) null).get());
            }
            catch (Exception e) {
                Log.e("CCTracker","Exception", e);
                timesheetIds_values.add("error");
            }
        }

    }

    public void populateEntries() {
        LinearLayout ln = (LinearLayout) this.findViewById(R.id.entries);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFF61B329); // Changes this drawable to use a single color instead of a gradient
        gd.setStroke(3, 0xFF000000);

        for (int i = 0; i < timesheetIds_values.size(); i++) {

            String[] values = timesheetIds_values.get(i).split(",");
            String epoch = values[0];
            String latitude = values[1];
            String longitude = values[2];
            //String[] date  = new Date(Long.parseLong(epoch)).toString().split(" ");
            //String time = date[1] + " " + date[2] + " " + date[5];

            String time = new Date(Long.parseLong(epoch)).toString();

            // create a new textview
            TextView row = new TextView(this);

            // set some properties of rowTextView or something
            row.setText("  " + time);
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

    public class GetTimesheetEntry extends AsyncTask<Void, Void, String> {

        private final String mTimesheetId;

        GetTimesheetEntry(String timesheetId) {
            mTimesheetId = timesheetId;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String link="http://www.stchiang.com/mis573/CCTracker/get_timesheet_entry.php";
                String data  = URLEncoder.encode("timesheet_id", "UTF-8")
                        + "=" + URLEncoder.encode(mTimesheetId, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String entry = reader.readLine();
                reader.close();

                return entry;
            }
            catch(Exception e){
                Log.e("CCTracker", "Exception", e);
                return "null";
            }
        }
    }

}
