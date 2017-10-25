package com.c2s.batterychargingstatus.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.c2s.batterychargingstatus.R;
import com.c2s.batterychargingstatus.database.SQLiteAdapter;
import com.c2s.batterychargingstatus.model.Battery;
import com.c2s.batterychargingstatus.utils.Config;
import com.c2s.batterychargingstatus.utils.ExitConformation;
import com.c2s.batterychargingstatus.utils.NetworkHelper;
import com.c2s.batterychargingstatus.utils.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import static com.c2s.batterychargingstatus.utils.DateHelper.getCurrentDate;

public class BatterChargingStatusActivity extends AppCompatActivity {

    private static final String TAG = "BatterChargingStatusActivity";
    Timer mTimer = new Timer();
    private static final int REPEAT_EVERY_TEN_SEC = 10000;
    private String mJSONURLString;
    private LinearLayout mLinearLayout;

    private SQLiteAdapter mSqLiteAdapter;
    private String mJsonTime;
    List<Battery> mBatteries;
    private RequestQueue mRequestQueue;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batter_charging_status);
        mJSONURLString = "http://batterychargeservice.azurewebsites.net/api/battery/getdata?dateTime=" + getCurrentDate();
        mLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        mSqLiteAdapter = new SQLiteAdapter(BatterChargingStatusActivity.this);
        mSqLiteAdapter.getBatteryStatus();
        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

        mBatteries = mSqLiteAdapter.getBatteryStatus();

        if (null != mBatteries && 0 < mBatteries.size()) {

            for (int i = 0; i < mBatteries.size(); i++) {
                if (null != mBatteries.get(i).getJson()) {
                    List<String> data = getDataArrList(mBatteries.get(i).getJson(), false);
                    setDataToLinearLayout(data, false);
                }
            }
        }


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications


                   // displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                  //  txtMessage.setText(message);
                }
            }
        };

    }


    @Override
    public void onBackPressed() {
        ExitConformation.exitFromTheApp(BatterChargingStatusActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTimer.schedule(new TimerTask() {
            public void run() {
                if (NetworkHelper.hasNetwork(BatterChargingStatusActivity.this)) {
                    getDataFromServer();
                } else {
                    Toast.makeText(BatterChargingStatusActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();

                }
            }
        }, 0, REPEAT_EVERY_TEN_SEC);

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationHelper.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void getDataFromServer() {

        if (null == mRequestQueue) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mJSONURLString,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<String> data = getDataArrList(response, true);
                        if (null != response && null != data && 0 < data.size()) {

                            setDataToLinearLayout(data, true);
                            Battery battery = new Battery();
                            battery.setId((int) System.currentTimeMillis());
                            battery.setJson(response);
                            battery.setDate(mJsonTime);
                            mSqLiteAdapter.addBatteryStatusData(battery);
                            mJsonTime="";
                        } else {

                            if (!(null != mBatteries && 0 < mBatteries.size()))
                                Toast.makeText(BatterChargingStatusActivity.this, "Data is not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        mRequestQueue.add(stringRequest);
    }

    private void setDataToLinearLayout(List<String> data, boolean isFromServer) {

        LinearLayout linearLayout = new LinearLayout(BatterChargingStatusActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 0);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setBackground(ContextCompat.getDrawable(BatterChargingStatusActivity.this, R.drawable.bg_elevation));
        linearLayout.setPadding(10, 10, 10, 10);

        for (int i = 0; i < data.size(); i++) {
            TextView tv1 = new TextView(BatterChargingStatusActivity.this);
            tv1.setText(data.get(i));
            tv1.setPadding(5, 5, 5, 5);
            tv1.setTextSize(16);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(2, 2, 2, 2);
            tv1.setLayoutParams(lp);
            tv1.setTextColor(ContextCompat.getColor(BatterChargingStatusActivity.this, android.R.color.black));
            linearLayout.addView(tv1);
        }

        if (isFromServer) {
            mLinearLayout.addView(linearLayout, 0);
        } else {
            mLinearLayout.addView(linearLayout);
        }

    }

    private List<String> getDataArrList(String response, boolean isFromServer) {

        String resp = response.replaceAll(Pattern.quote("["), "");
        String resp1 = resp.replaceAll(Pattern.quote("]"), "");
        String resp2 = resp1.replaceAll(Pattern.quote("{"), "");
        String resp3 = resp2.replaceAll(Pattern.quote("}"), "");

        String[] arr = resp3.split(",");
        List<String> stringList = new ArrayList<>(Arrays.asList(arr));
        mJsonTime = "";

        for (int i = 0; i < stringList.size(); i++) {
            String element = stringList.get(i).replaceAll(Pattern.quote("\""), "");
            stringList.set(i, element);

            if (isFromServer && element.equalsIgnoreCase("Time")) {

                String[] time = element.split(":");
                mJsonTime = time[1];

            }
        }

        return stringList;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.exit_menu:
                ExitConformation.exitFromTheApp(BatterChargingStatusActivity.this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
