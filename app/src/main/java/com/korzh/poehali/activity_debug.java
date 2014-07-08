//package com.korzh.poehali;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.Color;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.speech.tts.TextToSpeech;
//import android.support.v7.app.ActionBarActivity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.Circle;
//import com.google.android.gms.maps.model.CircleOptions;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.korzh.poehali.interfaces.LocationBroadcaster;
//import com.korzh.poehali.network.MessageConsumer;
//import com.korzh.poehali.network.MessageProducer;
//import com.korzh.poehali.network.NetworkMessage;
//import com.korzh.poehali.network.packets.OrderPacket;
//import com.korzh.poehali.network.packets.UserLocationPacket;
//import com.korzh.poehali.network.packets.frames.LocationJson;
//import com.korzh.poehali.network.packets.frames.OrderDetailsJson;
//import com.korzh.poehali.network.packets.frames.UserJson;
//import com.korzh.poehali.util.C;
//import com.korzh.poehali.util.G;
//import com.korzh.poehali.util.U;
//
//import java.util.Random;
//
//
//public class activity_debug extends ActionBarActivity {
//    private final String TAG = getClass().getName();
//    private Context c;
//
//    private LocationBroadcaster locationBroadcaster = null;
//
//    private MessageConsumer orderConsumer = null;
//    private MessageConsumer policeConsumer = null;
//
//    private GoogleMap googleMap = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_debug);
//        c = this;
//
//        // global handler singleton
//        new G(c);
//
////        locationBroadcaster = new LocationBroadcaster(c);
//        setUpMapIfNeeded();
//    }
//
//
//
//
//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (googleMap == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//                    .getMap();
//            // Check if we were successful in obtaining the map.
//            if (googleMap != null) {
//                setUpMap();
//            }
//        }
//    }
//
//    private void setUpMap() {
//        // Hide the zoom controls as the button panel will cover it.
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
//        googleMap.getUiSettings().setAllGesturesEnabled(false);
//        googleMap.setTrafficEnabled(true);
//
//        // Uses a colored icon.
//        final Marker mKiev = googleMap.addMarker(new MarkerOptions()
//                                .position(C.LOCATION_KIEV)
//                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//
//        Circle circle = googleMap.addCircle(new CircleOptions()
//                .center(C.LOCATION_KIEV)
//                .radius(C.ORDER_SEARCH_RADIUS)
//                .strokeWidth(0)
//                .strokeColor(Color.LTGRAY)
//                .fillColor(C.SEARCH_CIRCLE_COLOR));
//
//
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(C.LOCATION_KIEV, C.DEFAULT_MAP_ZOOM));
//    }

//            case R.id.btnCreateOrderFarAway:
//
//                Handler handler = new Handler();
//                Runnable placeOrderFarAway = new Runnable(){
//                    public void run(){
//                        NetworkMessage msg = new NetworkMessage();
//                        msg.setExchange(C.DEFAULT_ORDERS_EXCHANGE);
//                        msg.setExchangeType("direct");
//                        G g = G.getInstance();
//                        Location myLocation = g.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//                        UserJson user = new UserJson(true);
//                        LocationJson origin = new LocationJson(C.LOCATION_KIEV.latitude,C.LOCATION_KIEV.longitude);
//                        LocationJson destination = new LocationJson(myLocation.getLatitude(),myLocation.getLongitude());
//                        OrderDetailsJson orderDetails = new OrderDetailsJson(36.0f);
//
//                        OrderPacket order = new OrderPacket(user,origin,destination,orderDetails);
//
//                        msg.setData(order.toString());
//                        MessageProducer messageProducer = new MessageProducer();
//                        messageProducer.execute(msg);
//                    }
//                };
//                handler.post(placeOrderFarAway);
//                break;
//
//            case R.id.btnCreateOrderNearBy:
//                Handler handler1 = new Handler();
//                Runnable placeOrderNearBy = new Runnable() {
//                    public void run() {
//                        NetworkMessage msg = new NetworkMessage();
//                        msg.setExchange(C.DEFAULT_ORDERS_EXCHANGE);
//                        msg.setExchangeType("direct");
//                        G g = G.getInstance();
//                        Location myLocation = g.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//                        UserJson user = new UserJson(true);
//                        LocationJson origin = new LocationJson(myLocation.getLatitude(),myLocation.getLongitude());
//                        LocationJson destination = new LocationJson(C.LOCATION_KIEV.latitude,C.LOCATION_KIEV.longitude);
//                        OrderDetailsJson orderDetails = new OrderDetailsJson(36.0f);
//
//                        OrderPacket order = new OrderPacket(user,origin,destination,orderDetails);
//
//                        msg.setData(order.toString());
//                        MessageProducer messageProducer = new MessageProducer();
//                        messageProducer.execute(msg);
//                    }
//                };
//                handler1.post(placeOrderNearBy);
//                break;
//
//            case R.id.btnCreateOrderRandom:
//                Handler handler2 = new Handler();
//                Runnable placeRandomOrder = new Runnable() {
//                    public void run() {
//                        NetworkMessage msg = new NetworkMessage();
//                        msg.setExchange(C.DEFAULT_ORDERS_EXCHANGE);
//                        msg.setExchangeType("direct");
//
//                        UserJson user = new UserJson(true);
//                        LocationJson origin = U.getRandomLocation(C.LOCATION_KIEV.latitude,C.LOCATION_KIEV.longitude,20000);
//                        LocationJson destination = U.getRandomLocation(C.LOCATION_KIEV.latitude,C.LOCATION_KIEV.longitude,20000);
//                        Random rnd = new Random();
//                        OrderDetailsJson orderDetails = new OrderDetailsJson(rnd.nextInt(50)+36);
//
//                        OrderPacket order = new OrderPacket(user,origin,destination,orderDetails);
//
//                        msg.setData(order.toString());
//                        MessageProducer messageProducer = new MessageProducer();
//                        messageProducer.execute(msg);
//                    }
//                };
//                handler2.post(placeRandomOrder);
//                break;
//
//            case R.id.btnCreateRandomTaxiDriversLocations:
//                LocationJson locationFrame;
//                for (int i=0; i<10; ++i){
//                    locationFrame = U.getRandomLocation(C.LOCATION_KIEV.latitude,C.LOCATION_KIEV.longitude,20000);
//                    NetworkMessage msg = new NetworkMessage();
//                    msg.setExchange(C.DEFAULT_LOCATIONS_EXCHANGE);
//                    msg.setExchangeType("direct");
//
//                    Random rnd = new Random();
//                    UserJson user = new UserJson(rnd.nextBoolean());
//                    UserLocationPacket pkt = new UserLocationPacket(user,locationFrame);
//
//                    msg.setData(pkt.toString());
//                    MessageProducer messageProducer = new MessageProducer();
//                    messageProducer.execute(msg);
//                }
//                break;
//            case R.id.btnSendRandomPoliceLocations:
////                Handler handler3 = new Handler();
////                Runnable SendPoliceLocation = new Runnable() {
////                    public void run() {
////                        for (int i=0; i<10; ++i) {
////
////                        }
////                    }
////                };
////                handler3.post(SendPoliceLocation);
//                break;
//
//        }
//    }
//
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.test, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}
