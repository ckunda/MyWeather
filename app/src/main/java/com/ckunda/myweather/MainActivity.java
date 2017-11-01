package com.ckunda.myweather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity {

    // Request Codes:
    final int REQUEST_CODE = 123; // Request Code for permission request callback
    final int NEW_CITY_CODE = 456; // Request code for starting new activity for result callback

    // Base URL for the OpenWeatherMap API. More secure https is a premium feature =(
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

    // App ID to use OpenWeather data
    final String APP_ID = "e72ca729af228beabd5d20e3b7749713";

    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;

    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    final String LOGCAT_TAG = "MyWeather";

    // Set LOCATION_PROVIDER here. Using GPS_Provider for Fine Location (good for emulator):
    // Recommend using LocationManager.NETWORK_PROVIDER on physical devices (reliable & fast!)
    final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    // Member Variables:
    boolean mUseLocation = true;
//    TextView mCityLabel;
//    ImageView mWeatherImage;
//    TextView mTemperatureLabel;

    // Declaring a LocationManager and a LocationListener here:
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    ScrollView scrollView;
    TableLayout tlWeatherL;
    TableRow trCity1TopL;
    TextView textViewCityL;
    ImageButton buttonTempL;
    ImageButton buttonNullL;
    ImageButton buttonRemoveL;
    TableRow trCity1BottomL;
    GridLayout gridLayoutL;
    ImageButton imageGraphicL;
    TextView textViewConditionL;
    TextView textViewTempL;
    TextView textViewRainL;
    TextView textViewHighL;
    TextView textViewSpacerL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.scrollView);
        tlWeatherL = findViewById(R.id.tlWeatherL);
        trCity1TopL = findViewById(R.id.trCity1TopL);
        textViewCityL = findViewById(R.id.textViewCityL);
        buttonTempL = findViewById(R.id.buttonTempL);
        buttonNullL = findViewById(R.id.buttonNullL);
        buttonRemoveL = findViewById(R.id.buttonRemoveL);
        trCity1BottomL = findViewById(R.id.trCity1BottomL);
        gridLayoutL = findViewById(R.id.gridLayoutL);
        imageGraphicL = findViewById(R.id.imageGraphicL);
        textViewConditionL = findViewById(R.id.textViewConditionL);
        textViewTempL = findViewById(R.id.textViewTempL);
        textViewRainL = findViewById(R.id.textViewRainL);
        textViewHighL = findViewById(R.id.textViewHighL);
        textViewSpacerL = findViewById(R.id.textViewSpacerL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemAdd:
                Toast.makeText(getApplicationContext(), getString(R.string.strAddCity), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.mnuItemFC:
                Toast.makeText(getApplicationContext(), getString(R.string.strFC), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOGCAT_TAG, "onResume() called");
        if(mUseLocation) getWeatherForCurrentLocation();
        getWeatherForNewCity("Naperville");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mLocationManager != null) mLocationManager.removeUpdates(mLocationListener);
    }

    // Location Listener callbacks here, when the location has changed.
    private void getWeatherForCurrentLocation() {

        Log.d(LOGCAT_TAG, "Getting weather for current location");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.d(LOGCAT_TAG, "onLocationChanged() callback received");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                Log.d(LOGCAT_TAG, "longitude is: " + longitude);
                Log.d(LOGCAT_TAG, "latitude is: " + latitude);

                // Providing 'lat' and 'lon' (spelling: Not 'long') parameter values
                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                getWeather(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Log statements to help you debug your app.
                Log.d(LOGCAT_TAG, "onStatusChanged() callback received. Status: " + status);
                Log.d(LOGCAT_TAG, "2 means AVAILABLE, 1: TEMPORARILY_UNAVAILABLE, 0: OUT_OF_SERVICE");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(LOGCAT_TAG, "onProviderEnabled() callback received. Provider: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(LOGCAT_TAG, "onProviderDisabled() callback received. Provider: " + provider);
            }
        };

        // This is the permission check to access (fine) location.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        // Some additional log statements to help you debug
        Log.d(LOGCAT_TAG, "Location Provider used: "
                + mLocationManager.getProvider(LOCATION_PROVIDER).getName());
        Log.d(LOGCAT_TAG, "Location Provider is enabled: "
                + mLocationManager.isProviderEnabled(LOCATION_PROVIDER));
        Log.d(LOGCAT_TAG, "Last known location (if any): "
                + mLocationManager.getLastKnownLocation(LOCATION_PROVIDER));
        Log.d(LOGCAT_TAG, "Requesting location updates");

        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);

    }

    // Configuring the parameters when a new city has been entered:
    private void getWeatherForNewCity(String city) {
        Log.d(LOGCAT_TAG, "Getting weather for new city: " + city);
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        getWeather(params);
    }

    // This is the callback that's received when the permission is granted (or denied)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Checking against the request code we specified earlier.
        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOGCAT_TAG, "onRequestPermissionsResult(): Permission granted!");

                // Getting weather only if we were granted permission.
                getWeatherForCurrentLocation();
            } else {
                Log.d(LOGCAT_TAG, "Permission denied =( ");
            }
        }

    }

    // This is the actual networking code. Parameters are already configured.
    private void getWeather(RequestParams params) {

        // AsyncHttpClient belongs to the loopj dependency.
        AsyncHttpClient client = new AsyncHttpClient();

        // Making an HTTP GET request by providing a URL and the parameters.
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d(LOGCAT_TAG, "Success! JSON: " + response.toString());
                Log.d(LOGCAT_TAG, "Status code " + statusCode);
                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
                updateUI(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {

                Log.e(LOGCAT_TAG, "Fail " + e.toString());
                Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();

                Log.d(LOGCAT_TAG, "Status code " + statusCode);
                Log.d(LOGCAT_TAG, "Here's what we got instead " + response.toString());
            }
        });
    }

    // Updates the information shown on screen.
    private void updateUI(WeatherDataModel weather) {
        textViewTempL.setText(weather.getTemperature());
        textViewHighL.setText(weather.getMin() + " / " + weather.getMax());
        textViewCityL.setText(weather.getCity());
        textViewConditionL.setText(weather.getCondition());
//        textViewRainL.setText("Chance of Rain: " + weather.getRain());

        // Update the icon based on the resource id of the image in the drawable folder.
        int resourceID = getResources().getIdentifier(weather.getIconName(), "drawable", getPackageName());
        imageGraphicL.setImageResource(resourceID);
    }
}
