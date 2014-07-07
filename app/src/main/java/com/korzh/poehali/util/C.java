package com.korzh.poehali.util;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vladimir on 6/30/2014.
 */
public class C {
    public static final Boolean DEBUG_LOG = true;

    public static final int LOCATION_BROADCASTER_UPDATES_MILLIS = 30000;
    public static final int MAP_POSITION_UPD8_MILLIS = 0; // milliseconds  ~700 kb\day
    public static final int MAP_MIN_DISTANCE_UPD8 = 5;  // in meters, but we only use timed updates

    public static final LatLng LOCATION_KIEV = new LatLng(50.45, 30.523333);


    public static final double ORDER_SEARCH_RADIUS = 2000; // in meters



    // UserID:isFree@lat,lon
    public final static String LOCATION_FRAME_SEPARATOR = ",";
    public static final String USER_FRAME_SEPARATOR = ":";
    public static final String PACKET_FRAME_SEPARATOR = "@";
    public static final String ORDER_FRAME_SEPARATOR = "!";



//    MAP SETTINGS
    public static final int MAP_MODE_BIRDVIEW = 1;
    public static final int MAP_MODE_NAVIGATION = 2;
    public static final float MIN_BEARING_CHANGE_DISTANCE = 3.0f;
    public static final float NAVIGATION_MAP_ZOOM = 17.0f;
    public static final float DEFAULT_MAP_ZOOM = 12.0f;
    public static final float NAVIGATION_MAP_TILT = 45.0f;
    public static final float SEARCH_ADDRESS_MAP_ZOOM = 17.0f;
    public static final int SEARCH_CIRCLE_COLOR = Color.argb(40, 0, 0, 80);



//  MOTP Login endpoints
    public static final String MOTP_API_KEY = "0004-5bc5a9ce-53b8b0a1-749a-a2064ca1";
    public static final String MOTP_PRIVATE_KEY = "0009-5bc5a9ce-53b8b0a1-749d-d05c7754";
    public static final String MOTP_ENDPOINT_CALL = "https://api.mOTP.in/v1/"+MOTP_API_KEY+"/";
    public static final String MOTP_ENDPOINT_CONFIRM = "https://api.mOTP.in/v1/OTP/"+MOTP_API_KEY+"/";

//  MESSAGE QUEUE SETTINGS
    public static final String MQ_PRODUCER_URI = "amqp://TNULbwzS:bm_3GOIvL4qSupa4BdYryLYEtHFq5IJO@wet-dandelion-7.bigwig.lshift.net:11184/BnRXhLF-7nS7";
    public static final String MQ_CONSUMER_URI = "amqp://TNULbwzS:bm_3GOIvL4qSupa4BdYryLYEtHFq5IJO@wet-dandelion-7.bigwig.lshift.net:11185/BnRXhLF-7nS7";
    public static final String POLICE_API_ENDPOINT = "http://py-poehali.herokuapp.com";

    public static final String DEFAULT_POLICE_QUEUE = "police-ukraine-kiev";
    public static final String DEFAULT_LOCATIONS_EXCHANGE = "locations-ukraine-kiev";
    public static final String DEFAULT_ORDERS_EXCHANGE = "orders-ukraine-kiev";


//  UNUSED
    public static final Boolean USE_ENCRYPTION = false; //TODO normal way of back to back encryption
    public static final String ENCRYPTION_KEY = "@ED5a4f228dBd121";

    public static final String CURRENT_LOCATION = "Киев, Украина";








    public static final String PREFERENCES_FILENAME = "Settings";
    public static final int REQUEST_CODE_VALIDATE_PHONE = 1;
    public static final int REQUEST_CODE_ADDRESS_INPUT = 2;
    public static final int REQUEST_CODE_NEW_ROUTE = 3;
}
