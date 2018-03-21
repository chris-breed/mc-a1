package com.assignment1.chris.utilityapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    String result = "";

    String url = "https://free.currencyconverterapi.com/api/v5/convert?q=";

    public MainActivity() throws MalformedURLException {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText editTop = findViewById(R.id.edit_top);
        final EditText editBottom = findViewById(R.id.edit_bottom);

        final Spinner spinnerTop = findViewById(R.id.spinner_top);
        final Spinner spinnerBottom = findViewById(R.id.spinner_bottom);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTop.setAdapter(adapter);
        spinnerBottom.setAdapter(adapter);

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

                String from;
                String to;
                from = spinnerTop.getSelectedItem().toString().split(",")[0];
                to = spinnerBottom.getSelectedItem().toString().split(",")[0];
                Log.i("Volley", from);

                final String newUrl = url + from + "_" + to;

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, newUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Volley", "onResponse() called, " + newUrl);
                        Log.i("Volley", response);
                        result = response;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley", "onErrorResponse() called");
                    }
                });

                queue.add(stringRequest);

                Log.i("Volley", result);
            }
        });


    }


}
