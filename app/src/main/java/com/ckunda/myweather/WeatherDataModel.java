package com.ckunda.myweather;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataModel {

    // Member variables that hold our relevant weather information.
    private String mTemperature;
    private String mMin;
    private String mMax;
    private String mTemperatureC;
    private String mMinC;
    private String mMaxC;
    private String mCity;
    private String mIconName;
    private String txtCondition;
    private int mCondition;
    private String mRain;

    // Create a WeatherDataModel from a JSON.
    // We will call this instead of the standard constructor.
    public static WeatherDataModel fromJson(JSONObject jsonObject) {

        // JSON parsing is risky business. Need to surround the parsing code with a try-catch block.
        try {
            WeatherDataModel weatherData = new WeatherDataModel();

            weatherData.mCity = jsonObject.getString("name");
            weatherData.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.mIconName = updateWeatherIcon(weatherData.mCondition);
            weatherData.txtCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            int roundedValue = (int) Math.rint(tempResult);
            weatherData.mTemperatureC = Integer.toString(roundedValue);

            roundedValue = (int) Math.rint(((tempResult * 9) / 5) + 32);
            weatherData.mTemperature = Integer.toString(roundedValue);

            tempResult = jsonObject.getJSONObject("main").getDouble("temp_min") - 273.15;
            roundedValue = (int) Math.rint(tempResult);
            weatherData.mMinC = Integer.toString(roundedValue);

            roundedValue = (int) Math.rint(((tempResult * 9) / 5) + 32);
            weatherData.mMin = Integer.toString(roundedValue);

            tempResult = jsonObject.getJSONObject("main").getDouble("temp_max") - 273.15;
            roundedValue = (int) Math.rint(tempResult);
            weatherData.mMaxC = Integer.toString(roundedValue);

            roundedValue = (int) Math.rint(((tempResult * 9) / 5) + 32);
            weatherData.mMax = Integer.toString(roundedValue);

            weatherData.mRain = "0%";
//            weatherData.mRain = jsonObject.getString("rain");

            return weatherData;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get the weather image name from OpenWeatherMap's condition (marked by a number code)
    private static String updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }

    // Getter methods for temperature, city, and icon name:

    public String getTemperature() {return mTemperature + "°";}

    public String getMin() {
        return mMin + "°";
    }

    public String getMax() {
        return mMax + "°";
    }

    public String getTemperatureC() {return mTemperatureC + "°";}

    public String getMinC() {return mMinC + "°";}

    public String getMaxC() {return mMaxC + "°";}

    public String getCity() {
        return mCity;
    }

    public String getIconName() {
        return mIconName;
    }

    public String getCondition() {
        return txtCondition;
    }

    public String getRain() {
        return "  ";
    }

}
