package biz.fatez.com.betimecheckin;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "LoginPrefs";

    final String PREFNAME = "SamplePreferences";
    final String USERNAME = "UserName";


    //Set Error Status
    static boolean errored = false;
    Button b;
    TextView statusTV;
    EditText userNameET, passWordET;
    ProgressBar webservicePG;
    String editTextUsername;
    boolean loginStatus;
    String editTextPassword;

    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getString("logged", "").toString().equals("logged")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }


        //Name Text control
        userNameET = (EditText) findViewById(R.id.editText_username);
        passWordET = (EditText) findViewById(R.id.editText_password);
        userNameET.setTextColor(Color.parseColor("#000000"));
        passWordET.setTextColor(Color.parseColor("#000000"));
        //Display Text control
        statusTV = (TextView) findViewById(R.id.tv_result);
        //Button to trigger web service invocation
        b = (Button) findViewById(R.id.btn_login);
        //Display progress bar until web service invocation completes
        webservicePG = (ProgressBar) findViewById(R.id.progressBar1);

        //Button Click Listener
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Check if text controls are not empty
                if (userNameET.getText().length() != 0 && userNameET.getText().toString() != "") {
                    if (passWordET.getText().length() != 0 && passWordET.getText().toString() != "") {
                        editTextUsername = userNameET.getText().toString();
                        editTextPassword = passWordET.getText().toString();

                        statusTV.setText("");

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor2 = settings.edit();
                        editor2.putString("logged", "logged");
                        editor2.putString("spUsername",editTextUsername);
                        editor2.commit();

                        //Create instance for AsyncCallWS
                        AsyncCallWS task = new AsyncCallWS();
                        //Call execute
                        task.execute();
                    }
                    //If Password text control is empty
                    else {
                        statusTV.setText("Please enter Password");
                    }
                    //If Username text control is empty
                } else {
                    statusTV.setText("Please enter Username");
                }
            }
        });


        SharedPreferences sp;

        sp = getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        editor = sp.edit();

        userNameET = (EditText)findViewById(R.id.editText_username);
        userNameET.setText(sp.getString(USERNAME, ""));
        userNameET.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) {
                editor.putString(USERNAME, s.toString());
                editor.commit();
            }
        });


    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Call Web Method
            loginStatus = WebService.invokeLoginWS(editTextUsername, editTextPassword, "GetUserLogin_1");
             //loginStatus = WebService.invokeLoginWS(editTextUsername, editTextPassword, "GetUserLogin");
            return null;
        }
        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
            //Make Progress Bar invisible
            webservicePG.setVisibility(View.INVISIBLE);
            Intent intObj = new Intent(LoginActivity.this, MainActivity.class);
            intObj.putExtra("sUsername",editTextUsername);
            //Error status is false
            if (!errored) {
                //Based on Boolean value returned from WebService
                if (loginStatus==true) {
                    //Navigate to Home Screen
                    startActivity(intObj);
                } else {
                    //Set Error message
                    statusTV.setText("Login Failed, try again");
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
            webservicePG.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
