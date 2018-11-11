package com.alyamaniy.weather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alyamaniy.weather.Tools.Background;
import com.alyamaniy.weather.Tools.Geolocation;
import com.alyamaniy.weather.Tools.Tools;
import com.alyamaniy.weather.WeatherAPIs.DarkSky;
import com.alyamaniy.weather.WeatherAPIs.OpenWeather;
import com.github.pwittchen.weathericonview.WeatherIconView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Geolocation geolocation;
    SharedPreferences sharedPreferences;
    DrawerLayout drawer;
    LinearLayout hourlyContainer;
    LinearLayout dailyContainer;
    LinearLayout detailCurrentWeather;
    TextView tempTextview;
    TextView minMaxTempTextview;
    TextView windTextview;
    TextView infoTextview;
    TextView time;
    TextView date;
    WeatherIconView iconDesc;
    WeatherIconView detailIcon;
    AppCompatTextView title;
    Menu menuNav;
    String temp = "";
    String minTemp = "";
    String maxTemp = "";
    String wind = "";
    String info = "";
    String icon = "";
    String sunset = "";
    String sunrise = "";
    String notificationIcon = "";

    List<String> tempHourly = new ArrayList<>();
    List<String> iconHourly  = new ArrayList<>();
    List<String> timeHourly  = new ArrayList<>();
    List<String> precipProbabilityHourly  = new ArrayList<>();


    List<String> tempMaxDaily = new ArrayList<>();
    List<String> tempMinDaily  = new ArrayList<>();
    List<String> iconDaily  = new ArrayList<>();
    List<String> timeDaily  = new ArrayList<>();
    List<String> precipProbabilityDaily  = new ArrayList<>();


    int notifId = 0;
    int iconId = 0;

    JSONObject curentWeather = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.toolbarTitle);
        drawer = findViewById(R.id.drawer_layout);
        windTextview = findViewById(R.id.wind);
        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
        tempTextview = findViewById(R.id.temp);
        minMaxTempTextview = findViewById(R.id.maxMinTemp);
        infoTextview = findViewById(R.id.info);
        iconDesc = findViewById(R.id.icon);
        detailIcon = findViewById(R.id.detail_icon);
        hourlyContainer = findViewById(R.id.hourly_container);
        dailyContainer = findViewById(R.id.daily_container);
        detailCurrentWeather = findViewById(R.id.detail_current_weather);

        title.setSelected(true);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        geolocation = new Geolocation(this, this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            title.setText(geolocation.getAddress());
        }

        initWeather();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hambur);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Toolbar title clicked", Toast.LENGTH_SHORT).show();
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        sharedPreferences = getSharedPreferences("com.alyamaniy.weather", Context.MODE_PRIVATE);

        menuNav = navigationView.getMenu();

        View item = Tools.getMenuItem(menuNav,"background");
        Tools.setToggle(item,"background",sharedPreferences);

        item = Tools.getMenuItem(menuNav,"status");
        Tools.setToggle(item,"status",sharedPreferences);

        setBackgroundLayout();
        setdate();
    }


    public void initWeather(){
        DarkSky darksky = new DarkSky(this);
        darksky.execute("https://api.darksky.net/forecast/a55d15f3e5641e4a13706f49f3407fba/"+geolocation.getLatitude()+","+geolocation.getLongitude());

        OpenWeather openWeather = new OpenWeather(this);
        openWeather.execute("http://api.openweathermap.org/data/2.5/weather?q="+geolocation.getCity()+"&APPID=c41e6395733d01cb3b41eb5bdec280ab&units=metric");

    }

    public void setdate() {
        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                date.setText(new SimpleDateFormat("EEE MMM d, yyyy", Locale.getDefault()).format(new Date()));
                someHandler.postDelayed(this, 1000);
            }
        }, 10);
    }

    public void onTaskCompleteOpenWeater(JSONObject response) {
        try {
            temp = Tools.roundString(response.getString("temp")) + "°";
            minTemp = "Max: " +  Tools.roundString(response.getString("temp_min")) +"°";
            maxTemp = "Min: " +  Tools.roundString(response.getString("temp_max")) +"°";
            wind = "Wind: " + Tools.degreeToCardinal(response.getInt("wind_direction"))
                    + " " + Tools.convertMeterToKilometer(response.getDouble("wind"))+" km/h";
            notificationIcon = Tools.getIcon(response.getString("icon"));
            sunrise = Tools.convertUnixTime(response.getLong("sunrise"));
            sunset = Tools.convertUnixTime(response.getLong("sunset"));
            addLayoutDetailCurent("Sunset: " + sunset, true);
            addLayoutDetailCurent("Sunrise: " + sunrise , true);

            setTemp();

        } catch (Exception e) {
            Log.i("error_api_call_open", temp);

            e.printStackTrace();
        }
    }

    public void onTaskCompleteDarkSKy(JSONObject response) {
        try {
            Tools.addJsonToList(response.getJSONArray("temp"),tempHourly);
            Tools.addJsonToList(response.getJSONArray("time"),timeHourly);
            Tools.addJsonToList(response.getJSONArray("precipProbability"),precipProbabilityHourly);
            Tools.addJsonToList(response.getJSONArray("icon"),iconHourly);

            Tools.addJsonToList(response.getJSONArray("daily_temp_max"),tempMaxDaily);
            Tools.addJsonToList(response.getJSONArray("daily_temp_min"),tempMinDaily);
            Tools.addJsonToList(response.getJSONArray("daily_time"),timeDaily);
            Tools.addJsonToList(response.getJSONArray("daily_precipProbability"),precipProbabilityDaily);
            Tools.addJsonToList(response.getJSONArray("daily_icon"),iconDaily);

            String format = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

            curentWeather = response.getJSONObject("current");

            temp = Tools.roundString(Tools.converFarToCel(response.getJSONObject("current").getDouble("temperature"))) + "°";
            info = response.getJSONObject("current").getString("summary");
            icon = Tools.getIcon(response.getJSONObject("current").getString("icon"));

            icon = Tools.isMorning(format, icon,false);
            iconId = Tools.getId(this, icon);

            if(iconId == 0) {
                icon = Tools.isMorning(format,icon, true);
                iconId = Tools.getId(this, icon);
            }

            for (int i = 0; i < timeHourly.size(); i++) {
                addLayoutHourly(timeHourly.get(i),precipProbabilityHourly.get(i),
                        iconHourly.get(i), tempHourly.get(i) + "°", i);
            }

            for (int i = 0; i < timeDaily.size(); i++) {
                addLayoutDaily(timeDaily.get(i),precipProbabilityDaily.get(i),
                        iconDaily.get(i), tempMaxDaily.get(i), tempMinDaily.get(i), i);
            }

            iconDesc.setIconResource(getString(iconId));

            detailIcon.setIconResource(getString(iconId));

            boolean isEmpty = false;

            for (int i = 0; i < 8; i++) {
                addLayoutDetailCurent(Tools.getDetailCurrent(curentWeather, i), isEmpty);
                isEmpty = true;
            }

            Log.i("weather_response_json", response.toString());

        } catch (Exception e) {
            Log.i("error_api_call_darksy", e.getMessage());
            e.printStackTrace();
        }
    }

    public void addLayoutHourly(String time, String chanceOfRain, String icon, String temp, int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.hourly_value, hourlyContainer, false);
        TextView timeHourly =  view.findViewById(R.id.temp_time_hourly );
        TextView tempHourly =  view.findViewById(R.id.temp_hourly );
        TextView chanceOfRainHourly =  view.findViewById(R.id.temp_chance_rain_hourly);
        WeatherIconView iconHourly = view.findViewById(R.id.icon_hourly);
        icon = Tools.isMorning(time,icon, false);
        iconId = Tools.getId(this, icon);

        if(iconId == 0) {
            icon = Tools.isMorning(time,icon, true);
            iconId = Tools.getId(this, icon);
        }

        iconHourly.setIconResource(getString(iconId));

        chanceOfRain = Tools.convertDecToPerc(chanceOfRain) + "%";
        timeHourly.setText(time);
        chanceOfRainHourly.setText(chanceOfRain);
        tempHourly.setText(temp);

        if(index == 0) {
            hourlyContainer.removeAllViews();
        }
        hourlyContainer.addView(view);
    }

    public void addLayoutDaily(String time, String chanceOfRain, String icon, String tempMax, String tempMin, int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.daily_value, dailyContainer, false);
        TextView timeDaily =  view.findViewById(R.id.temp_time_daily );
        TextView tempMaxDaily =  view.findViewById(R.id.temp_max_daily );
        TextView tempMinDaily =  view.findViewById(R.id.temp_min_daily );
        TextView chanceOfRainDaily =  view.findViewById(R.id.temp_chance_rain_daily);
        View bar =  view.findViewById(R.id.bar_temp_daily);
        LinearLayout.LayoutParams viewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        WeatherIconView iconDaily = view.findViewById(R.id.icon_daily);
        icon = Tools.isMorning(time,icon, false);
        iconId = Tools.getId(this, icon);

        viewLayoutParams.height = Tools.setBarHeight(Integer.valueOf(tempMin), Integer.valueOf(tempMax));
        viewLayoutParams.width = 35;

        if(iconId == 0) {
            icon = Tools.isMorning(time,icon, true);
            iconId = Tools.getId(this, icon);
        }

        iconDaily.setIconResource(getString(iconId));

        chanceOfRain = Tools.convertDecToPerc(chanceOfRain) + "%";
        timeDaily.setText(time);
        chanceOfRainDaily.setText(chanceOfRain);
        tempMaxDaily.setText(tempMax + "°");
        tempMinDaily.setText(tempMin + "°");
        bar.setLayoutParams(viewLayoutParams);

        if(index == 0) {
            dailyContainer.removeAllViews();
        }
        dailyContainer.addView(view);
    }

    public void addLayoutDetailCurent(String value, boolean isEmpty) {
        int margin = 10;

        TextView detailTextView = new TextView(this);
        TableRow.LayoutParams textviewParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f);
        detailTextView.setText(value);
        detailTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        detailTextView.setTextColor(Color.WHITE);
        textviewParams.setMargins(margin,margin,margin,margin);
        detailTextView.setLayoutParams(textviewParams);

        if(!isEmpty) {
            detailCurrentWeather.removeAllViews();
        }
        detailCurrentWeather.addView(detailTextView);
    }

    public void setTemp() {
        windTextview.setText(wind);
        infoTextview.setText(info);
        tempTextview.setText(temp);
        minMaxTempTextview.setText(maxTemp + " "+ minTemp);
    }

    public void toggle(View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        Switch switchState = (Switch) view;
        switch (parent.getId()) {
            case R.id.backgroundParent:
                Tools.setBackgroundToggle(sharedPreferences,switchState.isChecked());
                setBackgroundLayout();
                break;

            case R.id.statusParent:
                Tools.setStatusToggle(sharedPreferences,switchState.isChecked());
                setStatus();
                break;
            default:
                Toast.makeText(this," bye",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void setBackgroundLayout() {
        if(Tools.getBackgroundPreference(sharedPreferences)) {
            drawer.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        } else {
            new Background(this, drawer);
        }
    }

    public void setStatus() {
        final NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);

        contentView.setTextViewText(R.id.title, temp + " (" + info+")");
        contentView.setTextViewText(R.id.text, geolocation.getAddress());
        contentView.setTextViewText(R.id.time,new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));

        if(Tools.getStatusPreference(sharedPreferences)) {
            Notification notification  = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_sun)
                    .setOngoing(true)
                    .setContent(contentView)
                    .build();

            if(icon != "") {
                Picasso.get()
                        .load(notificationIcon)
                        .resize(160, 160)
                        .centerCrop()
                        .into(contentView,R.id.image,notifId,notification);
            }

            mNotification.notify(notifId, notification);
        } else {
            final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notifId);
        }
    }

    public void detectLocation() {
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);

        progressDialog.setMessage("Detecting current location ...");

        progressDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
            }
        }, 500);

        title.setText(geolocation.getAddress());

        initWeather();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_location) {
            detectLocation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.background:
                return true;
        }
        return true;
    }
}
