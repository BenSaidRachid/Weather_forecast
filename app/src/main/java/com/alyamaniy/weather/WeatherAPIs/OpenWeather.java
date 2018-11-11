package com.alyamaniy.weather.WeatherAPIs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.alyamaniy.weather.MainActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenWeather extends AsyncTask<String,Void,String> {
    private Context context;

    public OpenWeather(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while (data != -1) {
                char current = (char) data;
                result += current;
                data = reader.read();
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject temperature = jsonObject.getJSONObject("main");
            JSONObject desc = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject sys = jsonObject.getJSONObject("sys");
            JSONObject wind = jsonObject.getJSONObject("wind");
            JSONObject weather_info = new JSONObject();

            Log.i("Weather_api_json", temperature.toString());


            String temp = temperature.getString("temp");
            String temp_min = temperature.getString("temp_min");
            String temp_max = temperature.getString("temp_max");
            String wind_speed = wind.getString("speed");
            String wind_direc = wind.getString("deg");
            String icon = desc.getString("icon");
            String sunset = sys.getString("sunset");
            String sunrise = sys.getString("sunrise");

            weather_info.put("temp", temp);
            weather_info.put("temp_min", temp_min);
            weather_info.put("temp_max", temp_max);
            weather_info.put("wind", wind_speed);
            weather_info.put("wind_direction", wind_direc);
            weather_info.put("icon", icon);
            weather_info.put("sunset", sunset);
            weather_info.put("sunrise", sunrise);


            ((MainActivity) context).onTaskCompleteOpenWeater(weather_info);
        } catch (Exception e) {
            Log.i("Weather_error",e.getMessage());

            e.printStackTrace();
        }

    }
}
