package biz.fatez.com.betimecheckin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CheckinActivity extends AppCompatActivity {

    static boolean errored = false;
    Button btnCheckin;
    Button btnCheckout;
    TextView statusTV, af9;
    ProgressBar pgbCheckin;
    EditText editTextProject;
    int responeStatus;
    EditText editTextReason;
    TextView locationTV;
    String etProject;
    String etReason;

    String userAddress;
    String lat;
    String lng;
    String username;

    SharedPreferences sp;
    public static final String PREFS_NAME = "LoginPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);


        statusTV = (TextView) findViewById(R.id.tv_result2);
        editTextProject = (EditText) findViewById(R.id.ET_project);
        editTextReason = (EditText) findViewById(R.id.ET_reason);
        pgbCheckin = (ProgressBar) findViewById(R.id.progressBar2);
        btnCheckin = (Button) findViewById(R.id.btn_checkin);
        locationTV = (TextView) findViewById(R.id.locationTV);
        btnCheckout = (Button) findViewById(R.id.btn_checkout);

        af9 = (TextView) findViewById(R.id.afternine);
        af9.setTextColor(Color.parseColor("#F44336"));


        editTextProject.setTextColor(Color.parseColor("#000000"));
        editTextReason.setTextColor(Color.parseColor("#000000"));
        // get data from SP
        sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        userAddress = sp.getString("spUserAddress", "Location");
        lat = sp.getString("spLatitude", "Location");
        lng = sp.getString("spLongitude", "Location");

        SharedPreferences userAd = getSharedPreferences(PREFS_NAME, 0);
        username = userAd.getString("spUsername", "");
        if (username == null) {
            finish();
        }

        locationTV.setText(userAddress);

        btnCheckin.setTextColor(Color.parseColor("#ffffff"));
        btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (editTextReason.getText().length() != 0 && editTextReason.getText().toString() != "") {
                    statusTV.setText("");
                    etReason = editTextReason.getText().toString();
                    //Create instance for AsyncCallWS
                    AsyncCallCheckINWS task = new AsyncCallCheckINWS();
                    //Call execute
                    task.execute();
                } else {
                    Toast toast = Toast.makeText(CheckinActivity.this, "Please input reason!", Toast.LENGTH_LONG);
                    toast.show();
                }


            }
        });

        btnCheckout.setTextColor(Color.parseColor("#ffffff"));
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTV.setText("");
                //Create instance for AsyncCallWS
                AsyncCallCheckOutWS task = new AsyncCallCheckOutWS();
                //Call execute
                task.execute();
            }
        });

    }

    private class AsyncCallCheckINWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            //Call Web Method
            responeStatus = CheckinAF9WS.invokeCheckinWS(username, lat, lng, userAddress, etReason, "SetUserCheckin");
            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
            //Make Progress Bar invisible
            pgbCheckin.setVisibility(View.INVISIBLE);

            //Error status is false
            if (!errored) {
                //Based on Boolean value returned from WebService
                if (responeStatus == 1) {
                    Toast toast = Toast.makeText(CheckinActivity.this, "Check In Complete!", Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(CheckinActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else if (responeStatus == -1) {
                    //Set Error message
                    statusTV.setText("Data not complete");
                } else {
                    statusTV.setText("Failed, try again");
                }
                //Error status is true
            } else {
                statusTV.setText("Error occured in invoking webservice");
            }
            //Re-initialize Error Status to False
            errored = false;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pgbCheckin.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class AsyncCallCheckOutWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            //Call Web Method
            responeStatus = CheckoutWS.invokeCheckOutWS(username, lat, lng, userAddress, "SetUserCheckout");
            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
            //Make Progress Bar invisible
            pgbCheckin.setVisibility(View.INVISIBLE);


            //Error status is false
            if (!errored) {
                //Based on Boolean value returned from WebService
                if (responeStatus == 1) {
                    Toast toast = Toast.makeText(CheckinActivity.this, "Check Out Complete!", Toast.LENGTH_LONG);
                    toast.show();

                    Intent intent = new Intent(CheckinActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else if (responeStatus == -1) {
                    //Set Error message
                    statusTV.setText("Data not complete");
                } else {
                    statusTV.setText("Failed, try again");
                }
                //Error status is true
            } else {
                statusTV.setText("Error occured in invoking webservice");
            }
            //Re-initialize Error Status to False
            errored = false;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pgbCheckin.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        finish();
        return super.onKeyDown(keyCode, event);
    }
}
