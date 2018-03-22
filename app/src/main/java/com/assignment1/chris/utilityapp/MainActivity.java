package com.assignment1.chris.utilityapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    double conversion;

    public SharedPreferences preferences;

    public MainActivity() throws MalformedURLException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String url = "https://free.currencyconverterapi.com/api/v5/convert?q=";


        final EditText editTop = findViewById(R.id.edit_top);
        final TextView editBottom = findViewById(R.id.edit_bottom);

        final Spinner spinnerTop = findViewById(R.id.spinner_top);
        final Spinner spinnerBottom = findViewById(R.id.spinner_bottom);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTop.setAdapter(adapter);
        spinnerBottom.setAdapter(adapter);


        // Check preferences and set what needs to be set

        // Top spinner and default value
        String defaultCurrency = null;
        try {
            defaultCurrency = preferences.getString("defaultCurrency", "DEFAULT");
            int spinnerPosition = adapter.getPosition(defaultCurrency);
            spinnerTop.setSelection(spinnerPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button swap = findViewById(R.id.btn_swap);
        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempTop = Integer.parseInt(editTop.getText().toString());
                int tempBottom = Integer.parseInt(editBottom.getText().toString());

                editTop.setText("" + tempBottom);
                editBottom.setText("" + tempTop);
            }
        });

        Button convert = findViewById(R.id.btn_convert);
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send request for conversion

                final String from;
                final String to;
                from = spinnerTop.getSelectedItem().toString().split(",")[0];
                to = spinnerBottom.getSelectedItem().toString().split(",")[0];
                Log.i("Volley", "Converting from: " + from + " to " + to + ".");

                final String newUrl = url + from + "_" + to;

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, newUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.i("Volley", "onResponse() called, " + newUrl);
                        Log.i("Volley", response);

                        try {
                            JSONObject json = new JSONObject(response);
                            conversion = json.getJSONObject("results").getJSONObject((from + "_" + to).toUpperCase()).getDouble("val");
                            Log.i("Volley", conversion + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        float originalValue = Float.parseFloat(editTop.getText().toString());
                        double newValue = originalValue * conversion;

                        editBottom.setText(cleanUpValue(newValue));

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley", "onErrorResponse() called");
                    }
                });

                queue.add(stringRequest);

            }

            private String cleanUpValue(double val) {
                float decimalRemoved = Float.parseFloat(String.format("%.2f", val));

                return String.format("$%s", decimalRemoved);
            }
        });

        Button settings = findViewById(R.id.btn_config);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }


}

