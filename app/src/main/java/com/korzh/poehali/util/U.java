package com.korzh.poehali.util;

import android.util.Log;

import com.korzh.poehali.network.packets.frames.LocationJson;

import java.util.Random;

/**
 * Created by vladimir on 6/30/2014.
 */
public class U {
    public static void Log(String tag, String msg){
        if (C.DEBUG_LOG) Log.d(tag, msg);
    }

    public static LocationJson getRandomLocation(double y0, double x0, int radius) {
        // returns a random location within radius in meters

        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;
        return new LocationJson(foundLatitude, foundLongitude);
    }



}
