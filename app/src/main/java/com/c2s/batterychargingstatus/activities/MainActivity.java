package com.c2s.batterychargingstatus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.c2s.batterychargingstatus.R;
import com.c2s.batterychargingstatus.adapter.MyRecylerViewAdapter;
import com.c2s.batterychargingstatus.database.SQLiteAdapter;
import com.c2s.batterychargingstatus.model.User;
import com.c2s.batterychargingstatus.restapi.RetrofitClient;
import com.c2s.batterychargingstatus.restapi.RetrofitService;
import com.c2s.batterychargingstatus.utils.ExitConformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    RecyclerView mRecyclerView;
    List<User> mUsersFromDb;
    private SQLiteAdapter mSqLiteAdapter;
    Timer mTimer = new Timer();
    private static final int REPEAT_EVERY_TEN_SEC = 10000;
    private MyRecylerViewAdapter mRecyleyViewAdapter;
    private List<User> mUsers;
    private String mJSONURLString = "";

    ArrayList<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mJSONURLString = "http://batterychargeservice.azurewebsites.net/api/battery/getdata?dateTime=" + getCurrentDate();
        setUpRecylerView();
        getDataFromDataBase();

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
                ExitConformation.exitFromTheApp(MainActivity.this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getDataFromDataBase() {
       // mUsersFromDb = mSqLiteAdapter.getBatteryStatus();
    }

    private void setUpRecylerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSqLiteAdapter = new SQLiteAdapter(MainActivity.this);
        mUsers = new ArrayList<>();
    }

    private void getDataFromServer() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mJSONURLString,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "response " + response);



                        //

                        //  [{"Battery1Voltage":1,"Battery2Voltage":1,"Battery1Tempatrature":1,"Battery2Temparature":1,"Battery1Soc":1,"Battery2Soc":1,"Current":1,"Time":"10/7/2017 10:50:19 AM"}]
                        String resp = response.replaceAll(Pattern.quote("["), "");
                        Log.e(TAG, "resp" + resp);

                        String resp1 = resp.replaceAll(Pattern.quote("]"), "");
                        Log.e(TAG, "resp" + resp1);

                        String resp2 = resp1.replaceAll(Pattern.quote("{"), "");
                        Log.e(TAG, "resp" + resp2);

                        String resp3 = resp2.replaceAll(Pattern.quote("}"), "");
                        Log.e(TAG, "resp" + resp3);

                        String[] arr = resp3.split(",");
                        List<String> stringList = new ArrayList<>(Arrays.asList(arr));

                        for (int i = 0; i <stringList.size() ; i++) {
                            Log.e(TAG, "stringList " + stringList.get(i).replaceAll(Pattern.quote("\""),""));
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse " + error.getMessage());
            }
        });


        queue.add(stringRequest);


    }


    private String getCurrentDate() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
        Log.e(TAG, "Current date" + df.format(c.getTime()));
        return df.format(c.getTime());
    }


    @Override
    public void onBackPressed() {
        ExitConformation.exitFromTheApp(MainActivity.this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDataFromServer();

      /*  mTimer.schedule(new TimerTask() {
            public void run() {
                if (NetworkHelper.hasNetwork(MainActivity.this)) {
                    //getBatteryStatus();

                    getDataFromServer();
                } else {
                    Toast.makeText(MainActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();

                }
            }
        }, 0, REPEAT_EVERY_TEN_SEC);*/
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
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getAllUsers() {
        RetrofitService service = RetrofitClient.getInstance().create(RetrofitService.class);
        Call<List<User>> call = service.getUserData(getCurrentDate());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                if (null != response.body() && 0 < response.body().size()) {
                    mUsers.addAll(0, response.body());
                 //   mSqLiteAdapter.addBatteryStatusData(mUsers);

                    if (null != mUsersFromDb && 0 < mUsersFromDb.size()) {
                        mUsers.addAll(mUsersFromDb);
                    }
                } else {
                    if (null != mUsersFromDb && 0 < mUsersFromDb.size()) {
                        mUsers = mUsersFromDb;
                    }
                }

                if (null != mUsers && 0 < mUsers.size()) {
                    setDataToRecylerView(mUsers);
                } else {
                    Toast.makeText(MainActivity.this, "User data is not available", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "User data is not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataToRecylerView(List<User> users) {

       /* mRecyleyViewAdapter = new MyRecylerViewAdapter(users);
        mRecyclerView.setAdapter(mRecyleyViewAdapter);
*/
    }


}
