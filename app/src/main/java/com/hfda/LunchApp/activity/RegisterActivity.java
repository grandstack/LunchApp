package com.hfda.LunchApp.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hfda.LunchApp.R;
import com.hfda.LunchApp.app.AppConfig;
import com.hfda.LunchApp.app.AppController;
import com.hfda.LunchApp.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hfda.LunchApp.R.string.moreInfo;
import static com.hfda.LunchApp.R.string.register;
import static com.hfda.LunchApp.R.string.successfullReg;
import static com.hfda.LunchApp.R.string.allInfo;


public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private CheckBox chkStudent;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPassword;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        txtEmail = (EditText) findViewById(R.id.txtNewUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        chkStudent = (CheckBox) findViewById(R.id.chkStudent);

        //creates session manager
        session = new SessionManager(getApplicationContext());

        getSupportActionBar().setTitle(register);


        //check if user is alerady logged in. This should not happen in this activity,
        // but we check for safety
        if (session.isLoggedIn()) {
            //User is logged in, send him to main activity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        //Register button click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //String name = txtName.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                boolean student = chkStudent.isChecked();
                Integer intStudent;

                if (student) {
                    intStudent = 1;
                } else {
                    intStudent = 0;
                }


                if (!email.isEmpty() && !password.isEmpty()) {
                    if(email.length() > 3 && password.length() > 3){
                        newUser(email, password, intStudent);
                    }else{
                        Toast.makeText(getApplicationContext(),
                                moreInfo, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            allInfo, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void newUser(final String email, final String password, final Integer student) {

        // Tag used to cancel the request
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Laupet", "Register Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        // User successfully stored in MySQL
                        Toast.makeText(getApplicationContext(), successfullReg, Toast.LENGTH_LONG).show();

                        //Send user to loginactivity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error in registration
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Laupet", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("student", String.valueOf(student));
                Log.d("Laupet", "student: " + String.valueOf(student));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
