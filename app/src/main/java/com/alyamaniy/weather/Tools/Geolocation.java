package com.alyamaniy.weather.Tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class Geolocation {
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String knownName;
    private String latitude;
    private String longitude;
    private Geocoder geocoder;
    private List<Address> addresses;
    private Context context;
    private Activity activity;
    private LocationManager lm;
    private LocationListener locationListener;


    public Geolocation(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                UpdateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastLocation != null) {
                UpdateLocation(lastLocation);
            }
        }
    }

    private void UpdateLocation(Location location) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
            String addresse = "Nothing found";
            if(addresses != null && addresses.size() > 0) {
                setCity(addresses.get(0).getLocality());

                setState(addresses.get(0).getAdminArea());

                setCountry(addresses.get(0).getCountryName());

                setPostalCode(addresses.get(0).getPostalCode());

                setKnownName(addresses.get(0).getAddressLine(0));

                setAddress(addresses.get(0).getAddressLine(0));

                setLatitude(String.valueOf(location.getLatitude()));

                setLongitude(String.valueOf(location.getLongitude()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getKnownName() {
        return knownName;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    private void setCity(String city) {
        this.city = city;
    }

    private void setState(String state) {
        this.state = state;
    }

    private void setCountry(String country) {
        this.country = country;
    }

    private void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    private void setKnownName(String knownName) {
        this.knownName = knownName;
    }

    private void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    private void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
