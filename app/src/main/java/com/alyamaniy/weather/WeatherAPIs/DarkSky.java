package com.alyamaniy.weather.WeatherAPIs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.alyamaniy.weather.MainActivity;
import com.alyamaniy.weather.Tools.Tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DarkSky extends AsyncTask<String,Void,String> {

    private Context context;

    public DarkSky(Context context) {
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
            JSONArray hourly_weather_data = jsonObject.getJSONObject("hourly").getJSONArray("data");
            JSONArray daily_weather_data = jsonObject.getJSONObject("daily").getJSONArray("data");
            JSONObject current_weather = jsonObject.getJSONObject("currently");
            JSONObject weather_info = new JSONObject();

            JSONArray temp = new JSONArray();
            JSONArray icon = new JSONArray();
            JSONArray time = new JSONArray();
            JSONArray precipProbability = new JSONArray();

            JSONArray daily_time = new JSONArray();
            JSONArray daily_temp_min = new JSONArray();
            JSONArray daily_temp_max = new JSONArray();
            JSONArray daily_icon = new JSONArray();
            JSONArray daily_precipProbability = new JSONArray();

            for (int i = 0; i < 24; i++) {
                JSONObject data = hourly_weather_data.getJSONObject(i);

                temp.put(Tools.converFarToCel(data.getDouble("temperature")));
                time.put(Tools.convertUnixTime(data.getInt("time")));
                precipProbability.put(data.getString("precipProbability"));
                icon.put(data.getString("icon"));
            }

            for (int i = 0; i < daily_weather_data.length(); i++) {
                JSONObject data = daily_weather_data.getJSONObject(i);

                daily_temp_min.put(Tools.converFarToCel(data.getDouble("temperatureMin")));
                daily_temp_max.put(Tools.converFarToCel(data.getDouble("temperatureMax")));
                daily_time.put(Tools.convertUnixDate(data.getInt("time")));
                daily_precipProbability.put(data.getString("precipProbability"));
                daily_icon.put(data.getString("icon"));
            }

            weather_info.accumulate("temp", temp);
            weather_info.accumulate("time", time);
            weather_info.accumulate("precipProbability", precipProbability);
            weather_info.accumulate("icon", icon);
            weather_info.accumulate("daily_time", daily_time);
            weather_info.accumulate("daily_icon", daily_icon);
            weather_info.accumulate("daily_precipProbability", daily_precipProbability);
            weather_info.accumulate("daily_temp_max", daily_temp_max);
            weather_info.accumulate("daily_temp_min", daily_temp_min);
            weather_info.accumulate("current", current_weather);

            Log.i("Weather_json",current_weather.toString());
             ((MainActivity) context).onTaskCompleteDarkSKy(weather_info);
        } catch (Exception e) {
            Log.i("Weather_error",e.getMessage());

            e.printStackTrace();
        }

    }
}
