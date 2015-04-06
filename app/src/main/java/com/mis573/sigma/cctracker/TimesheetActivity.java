package com.mis573.sigma.cctracker;

import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private String empName;
    private String date;
    private String timesheetIds;

    private List<String> timesheetIds_values;

    // NW and SE points for a bounding box around working area
    // coordinates are hard-coded for WPI
    // north west coordinate (42.280530, -71.815487)
    // south east coordinate (42.270842, -71.797475)

    private Coordinate nw = new Coordinate("42.280530", "-71.815487");
    private Coordinate se = new Coordinate("42.270842", "-71.797475");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesheet);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            empName = extras.getString("empName");
            date = extras.getString("date");
            timesheetIds = extras.getString("timesheetIds");
        }

        TextView tv = (TextView) findViewById(R.id.emp_name);
        tv.setText(empName);
        tv = (TextView) findViewById(R.id.date);
        tv.setText(date);

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
        gd.setColor(0xFF0000);
        gd.setStroke(3, 0xFF000000);

        for (int i = 0; i < timesheetIds_values.size(); i++) {
            String[] values = timesheetIds_values.get(i).split(",");
            String epoch = values[0];
            String latitude = values[1];
            String longitude = values[2];
            String id = values[3];

            String time[] = new Date(Long.parseLong(epoch)).toString().split(" ");
            String formatted_time = time[3] + " " + time[4];

            TextView row = new TextView(this);
            row.setText("  " + id + " " + formatted_time);
            row.setPadding(0, 8, 0, 8);
            row.setTextSize(16);
            if (inBoundingBox(latitude, longitude)) {
                row.setBackgroundResource(R.color.green);
            }
            else {
                row.setBackgroundResource(R.color.red);
            }
            ln.addView(row);
        }
    }

    public Boolean inBoundingBox (String latitude, String longitude) {
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        return (nw.mLatitude >= lat && lat >= se.mLatitude &&
                nw.mLongitude <= lon && lon <= se.mLongitude);
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

    public void goBack (View view) {
        finish();
    }

    public class myComparator implements Comparator<String>{
        public int compare(String s1, String s2) {
            return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));

        }
    }

    public class Coordinate {
        public double mLatitude;
        public double mLongitude;

        public Coordinate(String latitude, String longitude) {
            mLatitude = Double.parseDouble(latitude);
            mLongitude = Double.parseDouble(longitude);
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
