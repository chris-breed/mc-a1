package com.assignment1.chris.utilityapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.Locale;

public class MainActivity extends AppCompatActivity /*implements GestureDetector.OnGestureListener */ {

    double conversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String defaultCurrency = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("defaultCurrency", "falseValue");

        final String url = "https://free.currencyconverterapi.com/api/v5/convert?q=";

        // Conversion Texts
        final EditText editTop = findViewById(R.id.edit_top);
        final TextView editBottom = findViewById(R.id.edit_bottom);
        // Currency Spinners
        final Spinner spinnerTop = findViewById(R.id.spinner_top);
        final Spinner spinnerBottom = findViewById(R.id.spinner_bottom);

        // Initialises the Spinners with the currencies, from string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTop.setAdapter(adapter);
        spinnerBottom.setAdapter(adapter);

        // Top spinner default value
        if (!defaultCurrency.equals("falseValue")) {
            int spinnerPosition = adapter.getPosition(defaultCurrency);
            spinnerTop.setSelection(spinnerPosition);
        }

        // Buttons and their listeners
        Button btn_swap = findViewById(R.id.btn_swap);
        btn_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float tempTop;

                try {
                    tempTop = Float.parseFloat(editTop.getText().toString());
                } catch (NumberFormatException e) {
                    tempTop = 0;
                }

                float tempBottom;
                try {
                    tempBottom = Float.parseFloat(editBottom.getText().toString().replace("$", ""));
                } catch (NumberFormatException e) {
                    tempBottom = 0;
                }

                editTop.setText(String.format("%s", tempBottom));
                editBottom.setText(String.format("%s", tempTop));
            }
        });

        Button convert = findViewById(R.id.btn_convert);
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send request for conversion

                editBottom.setText(R.string.txt_converting);

                final String to = spinnerBottom.getSelectedItem().toString().split(",")[0];
                final String from = spinnerTop.getSelectedItem().toString().split(",")[0];
//                Log.i("Volley", "Converting from: " + from + " to " + to + ".");

                String newUrl = generateURL(url, from, to);

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, newUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.i("Volley", response);

                        try {
                            conversion = getConversion(to, from, response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        double convertedValue = convertValue(conversion);

                        editBottom.setText(correctStringForEditText(convertedValue));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley", "onErrorResponse() called");
                    }
                });
                queue.add(stringRequest);
            }



            private double getConversion(String to, String from, String response) throws JSONException {
                JSONObject json = new JSONObject(response);
                try {
                    conversion = json.getJSONObject("results").getJSONObject((from + "_" + to).toUpperCase()).getDouble("val");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Volley", conversion + "");

                return conversion;
            }

            private double convertValue(double conversion) { // Converts the value of the editText to be equal to conversion * value
                float originalValue = Float.parseFloat(editTop.getText().toString());
                return originalValue * conversion;
            }

            private String correctStringForEditText(double val) { // Readies the value to be inserted into TextView
                float decimalRemoved = Float.parseFloat(String.format(Locale.ENGLISH, "%.2f", val));
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

    private String generateURL(String url, String from, String to) {
        return (url + from.toUpperCase() + "_" + to.toUpperCase());
    }
}