package com.example.bhanuprakash.locappv10;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String apikey = "AIzaSyBzbK7hznYp-wPSyUn_D0c3b4SE5IUkdH8";
    static final int REQUEST_LOCATION = 1;
    double lati;
    double longi;
    Location location;
    Geocoder geocoder;
    LocationManager locationManager;
    String cityName,description;
    String url, iconUrl;
    double avgtemp1,mintemp1,maxtemp1;
    TextView citytextview,appnametextview,infotextview;
    //MapView mapview;

    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    int i=0;

    //Toolbar toolbar;
    //private WebView picView;
    //ImageView iconimageview = (ImageView) findViewById(R.id.iconimageView);

    String info = "This app provides location awareness and weather information";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync((OnMapReadyCallback) MainActivity.this);
*/
        infotextview = (TextView) findViewById(R.id.infotextview);
        infotextview.setText(info);



        //toolbar = (Toolbar) findViewById(R.id.toolbar);
            //setSupportActionBar(toolbar);
            //toolbar.setTitle("LocAppV1.0");
            //toolbar.setSubtitle("City");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("Location Request");
        // set dialog message
        alertDialogBuilder
                .setMessage("LocApp would like to access your location.")
                .setCancelable(false)
                .setPositiveButton("Allow",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //Request Location Permission
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        //MainActivity.this.finish();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();




        //Get info button
        Button getinfobutton = (Button) findViewById(R.id.getinfobutton);
        getinfobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                // set title
                alertDialogBuilder.setTitle("Information");
                // set dialog message
                alertDialogBuilder
                        .setMessage("\nCity: " + cityName + "\n\nLongitude: " + longi + "\n\nLatitude: " + lati + "\n\nDescription: " + description + "\n\nAvg. Temp: " + String.format("%.2f", avgtemp1)+" °C" + "\n\nMin. Temp: " + String.format("%.2f", mintemp1) +" °C"+ "\n\nMax. Temp: " + String.format("%.2f", maxtemp1) +" °C")
                        .setCancelable(false)
                        .setPositiveButton("Dismiss",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //MainActivity.this.finish();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();

            }
        });

        //Get icon
        //Picasso.with(MainActivity.this).load(iconUrl).into((ImageView) findViewById(R.id.iconimageView));

        //Progress Bar
     /* mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setProgress(i);
        mCountDownTimer=new CountDownTimer(60000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
                i++;
                mProgressBar.setProgress((int)i/100);

            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                mProgressBar.setProgress(100);
            }
        };
        mCountDownTimer.start();*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Location Permission Granted", Toast.LENGTH_SHORT).show();

                    geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        //return;
                    }
                    //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    //Get Location Latitude and Longitude
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    lati = location.getLatitude();
                    longi = location.getLongitude();

                    citytextview = (TextView) findViewById(R.id.citytextView);
                    citytextview.setText(lati + "");

                    try {
                        List<Address> addresses = geocoder.getFromLocation(lati, longi, 1);
                        //Get the city name
                        cityName = addresses.get(0).getLocality();
                        //R.string.city_name = cityName;
                        citytextview = (TextView) findViewById(R.id.citytextView);
                        citytextview.setText(cityName + "");
                        url = "http://api.openweathermap.org/data/2.5/weather?q="+cityName.trim()+"&appid=d74c31253da375897bc4a3d55e4f50f9&units=Imperial";
                        findweather();

                        //InputStream content = (InputStream)iconUrl.getContent();
                        //Drawable d = Drawable.createFromStream(content , "src");
                        //iconimageView.setImageDrawable(d);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //String cityName = addresses.get(0).getAddressLine(0);
                }
                else{
                    //Request Location Permission again if denied
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
                break;
        }
    }

    public void findweather() {
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject mainobject = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String avgtemp = String.valueOf(mainobject.getDouble("temp"));
                    String mintemp = String.valueOf(mainobject.getDouble("temp_min"));
                    String maxtemp = String.valueOf(mainobject.getDouble("temp_max"));
                    description = object.getString("description");
                    //String city = response.getString("Name");

                    //Get icon
                    String icon = object.getString("icon");
                    iconUrl = "http://openweathermap.org/img/w/" + icon + ".png";
                    //Picasso.get().load(iconUrl).into(iconimageview);


                    avgtemp1 = (Double.parseDouble(avgtemp) - 32) / 1.8000 ;
                    mintemp1 = (Double.parseDouble(mintemp) - 32) / 1.8000 ;
                    maxtemp1 = (Double.parseDouble(maxtemp) - 32) / 1.8000 ;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Do you want to exit?");
        // alert.setMessage("Message");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Your action here
                finish();
                System.exit(0);
            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
        alert.show();
    }


}

