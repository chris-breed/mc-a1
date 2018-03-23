package com.assignment1.chris.utilityapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
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

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    double conversion;
    EditText editTop;
    TextView editBottom;
    Spinner spinnerTop;
    Spinner spinnerBottom;

    final String url = "https://free.currencyconverterapi.com/api/v5/convert?q=";
    private GestureDetectorCompat detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detector = new GestureDetectorCompat(this, this);

        // shared preferences
        String defaultCurrency = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("defaultCurrency", "falseValue");
        String previousAmount = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("previousAmount", "falseValue");


        // Conversion Texts
        editTop = findViewById(R.id.edit_top);
        editBottom = findViewById(R.id.edit_bottom);
        // Currency Spinners
        spinnerTop = findViewById(R.id.spinner_top);
        spinnerBottom = findViewById(R.id.spinner_bottom);

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

        // Top editText value, From previous instance of app
        if (!previousAmount.equals("falseValue")) {
            editTop.setText(previousAmount);
        }

        // Buttons and their listeners
        Button btn_swap = findViewById(R.id.btn_swap);
        btn_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // swaps the editext and edittext bottom values with each other
                float tempTop;

                try {
                    tempTop = Float.parseFloat(editTop.getText().toString());
                } catch (NumberFormatException e) {
                    tempTop = 0;
                }

                float tempBottom;
                try {
                    tempBottom = Float.parseFloat(editBottom.getText().toString()
                            .replace("$", ""));
                } catch (NumberFormatException e) {
                    tempBottom = 0;
                }

                editTop.setText(String.format("%s", tempBottom));
                editBottom.setText(String.format("%s", tempTop));
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

    @Override
    protected void onStop() {
        super.onStop();

        // Saves current number in edittext for the next session
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                .putString("previousAmount", editTop.getText().toString()).apply();

    }

    private String generateURL(String url, String from, String to) {
        return (url + from.toUpperCase() + "_" + to.toUpperCase());
    }

    private double convertValue(double conversion) {
        // Converts the value of the editText to be equal to conversion * value
        float originalValue = Float.parseFloat(editTop.getText().toString());
        return originalValue * conversion;
    }

    private String correctStringForEditText(double val) { // Readies the value to be inserted into TextView
        float decimalRemoved = Float.parseFloat(String.format(Locale.ENGLISH, "%.2f", val));
        return String.format("$%s", decimalRemoved);
    }

    private double getConversion(String to, String from, String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        try {
            conversion = json.getJSONObject("results").getJSONObject((from + "_" + to)
                    .toUpperCase()).getDouble("val");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//                Log.i("Volley", conversion + "");

        return conversion;
    }

    public void convertButtonPress(View view) {
        editBottom.setText(R.string.txt_converting);

        final String to = spinnerBottom.getSelectedItem().toString().split(",")[0];
        final String from = spinnerTop.getSelectedItem().toString().split(",")[0];
//                Log.i("Volley", "Converting from: " + from + " to " + to + ".");

        String newUrl = generateURL(url, from, to);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, newUrl, new Response.Listener<String>() {
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
//                        Log.i("Volley", "onErrorResponse() called");
            }
        });
        queue.add(stringRequest);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        convertButtonPress(findViewById(android.R.id.content));
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
