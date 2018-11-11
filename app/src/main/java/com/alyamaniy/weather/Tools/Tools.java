package com.alyamaniy.weather.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.alyamaniy.weather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public  class Tools {

    private static final String backgroundPreference  = "isBackgroundSelected";
    private static final String statusPreference  = "isStatusSelected";

    public static void setBackgroundToggle(SharedPreferences sharedPreferences, boolean isChecked) {
        sharedPreferences.edit().putBoolean(backgroundPreference, isChecked).apply();
    }

    public static boolean getBackgroundPreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(backgroundPreference, true);
    }

    public static void setStatusToggle(SharedPreferences sharedPreferences, boolean isChecked) {
        sharedPreferences.edit().putBoolean(statusPreference, isChecked).apply();
    }

    public static boolean getStatusPreference(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(statusPreference, true);
    }

    public static View getMenuItem(Menu menu, String menuName) {
        switch (menuName) {
            case "background":
                return menu.findItem(R.id.background).getActionView();
            case "status":
                return menu.findItem(R.id.status).getActionView();
            default:
                return null;
        }
    }

    public static boolean setToggle(View view, String menuName, SharedPreferences sharedPreferences) {
        if(view != null) {
            LinearLayout  layout = (LinearLayout) view;
            Switch switchButton = Tools.getSwitch(layout);
            switch (menuName) {
                case "background":
                    if(getBackgroundPreference(sharedPreferences)) {
                        switchButton.setChecked(true);
                    } else {
                        switchButton.setChecked(false);
                    }
                case "status":
                    if(getStatusPreference(sharedPreferences)) {
                        switchButton.setChecked(true);
                    } else {
                        switchButton.setChecked(false);
                    }
                default:
                    return false;
            }
        }
        return false;
    }

    public static Switch getSwitch(LinearLayout linearLayout) {
        return (Switch) linearLayout.getChildAt(0);
    }

    public static String getIcon(String icon) {
        return "http://openweathermap.org/img/w/"+ icon +".png";
    }

    public static String convertMeterToKilometer(double speed) {
        return String.valueOf(Math.round(speed * 3.6));
    }

    public static String degreeToCardinal(int angle) {
        int directions = 8;

        int degree = 360 / directions;
        angle = angle + degree / 2;

        if (angle >= 0 * degree && angle < 1 * degree)
            return "North";
        else if (angle >= 1 * degree && angle < 2 * degree)
            return "North East";
        else if (angle >= 2 * degree && angle < 3 * degree)
            return "East";
        else if (angle >= 3 * degree && angle < 4 * degree)
            return "South East";
        else if (angle >= 4 * degree && angle < 5 * degree)
            return "South";
        else if (angle >= 5 * degree && angle < 6 * degree)
            return "South West";
        else if (angle >= 6 * degree && angle < 7 * degree)
            return "West";
        else
            return "North West";
    }

    public static String roundString(String value) {
        return String.valueOf(Math.round(Float.valueOf(value)));
    }

    public static String convertUnixTime(long seconds) {
        Date date = new Date(seconds * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:00 a", Locale.getDefault());
        return sdf.format(date);
    }

    public static String convertUnixDate(long seconds) {
        Date date = new Date(seconds * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());
        return sdf.format(date);
    }

    public static String converFarToCel(Double temp) {
        return String.valueOf(Math.round(((temp - 32) / 1.8)));
    }

    public static void addJsonToList(JSONArray jsonArray, List<String> list) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            String value = jsonArray.getString(i);
            list.add(value);
        }
    }

    public static String isMorning(String time, String icon, boolean isCheating) {
        boolean isMorning = false;
        if(isCheating) {
            return (icon.startsWith("wi_day_")) ? icon.replace("wi_day_","wi_night_")
                    : icon.replace("wi_night_","wi_day_");
        }

        if(icon.contains("wind")) {
            icon = icon.replaceAll("wind","windy");
        }

        if(icon.indexOf("day") > 0) {
            isMorning = true;
            icon = icon.replaceAll("-day","");
        } else if(icon.indexOf("night") > 0){
            icon = icon.replaceAll("-night","");
        }
        icon = icon.replaceAll("-","_");
        if(!time.contains(":")) {
            return (isMorning) ? "wi_day_" + icon : "wi_night_" + icon;
        }
        time = time.substring(0, time.indexOf(":"));
        return ((Integer.valueOf(time) > 5 && Integer.valueOf(time) < 18) || isMorning) ? "wi_day_" + icon : "wi_night_" + icon;
    }

    public static int getId(Context context, String id) {
        return context.getResources().getIdentifier(id, "string", context.getPackageName());
    }

    public static String convertDecToPerc(String dec) {
        return (!dec.equals("0")) ? String.valueOf(Math.round(Float.valueOf(dec) * 100)) : dec;
    }

    public static String getDetailCurrent(JSONObject curentWeather, int indexField) throws JSONException{
        switch (indexField) {
            case 0:
                return "Humidity: " + convertDecToPerc(curentWeather.getString("humidity")) + " %";
            case 1:
                return "Chance of rain: " + convertDecToPerc(curentWeather.getString("precipProbability"))+ " %";
            case 2:
                return "Precipitation: " + curentWeather.getString("precipIntensity")+ " mm";
            case 3:
                return "Wind gust: " +curentWeather.getString("windGust")+ " m/s";
            case 4:
                return "Dew point: " + converFarToCel(curentWeather.getDouble("dewPoint"));
            case 5:
                return "Cloud cover: " + convertDecToPerc(curentWeather.getString("cloudCover"))+ " %";
            case 6:
                return "Ultraviolet Index: " + curentWeather.getString("uvIndex");
            default:
                return "Pressure: " + Math.round(curentWeather.getDouble("pressure"))+ " mbar";
        }
    }

    public static int setBarHeight(int min, int max) {
        return (max - min) * 35;
    }
}
