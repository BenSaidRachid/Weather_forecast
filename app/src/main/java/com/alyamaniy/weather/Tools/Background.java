package com.alyamaniy.weather.Tools;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;

import com.alyamaniy.weather.R;

import java.util.Random;

public class Background {

    public Background(Context context, DrawerLayout drawerLayout) {
        Random random = new Random();
        int background = random.nextInt(6);

        switch (background) {
            case 0:
                drawerLayout.setBackground(context.getResources().getDrawable(R.drawable.city1));
                break;
            case 1:
                drawerLayout.setBackground(context.getResources().getDrawable(R.drawable.city2));
                break;
            case 2:
                drawerLayout.setBackground(context.getResources().getDrawable(R.drawable.city3));
                break;
            case 4:
                drawerLayout.setBackground(context.getResources().getDrawable(R.drawable.beach1));
                break;
            case 5:
                drawerLayout.setBackground(context.getResources().getDrawable(R.drawable.beach2));
                break;
            default:
                drawerLayout.setBackground(context.getResources().getDrawable(R.drawable.beach3));
                break;
        }
    }
}
