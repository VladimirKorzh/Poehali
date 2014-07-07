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
//import com.korzh.poehali.network.packets.frames.LocationFrame;
//import com.korzh.poehali.network.packets.frames.OrderDetailsFrame;
//import com.korzh.poehali.network.packets.frames.UserFrame;
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
//
//
//
//
//
//
//
//    public void onButtonClick(View v) {
//        switch (v.getId()){
////            case R.id.btnStartLocationBroadcast:
////                locationBroadcaster.Start();
////                break;
////            case R.id.btnStopLocationBroadcast:
////                locationBroadcaster.Stop();
////                break;
//
//            case R.id.btnStartLocationConsumer:
//
//                break;
//
//            case R.id.btnStopLocationConsumer:
//
//                break;
//            case R.id.btnStartOrderConsumer:
//                U.Log(TAG,"Order Consumer Started");
//                orderConsumer = new MessageConsumer();
//                orderConsumer.setOnReceiveMessageHandler(new MessageConsumer.OnReceiveMessageHandler() {
//                    public void onReceiveMessage(byte[] message) {
//                        String data = new String(message);
//                        OrderPacket order = new OrderPacket(data);
//                        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
//                        Location myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//                        U.Log("Order received", " Origin: "+order.getUserLocationFrameOrigin().getAddress()+
//                                                " \nDestination: "+order.getUserLocationFrameDestination().getAddress()+
//                                                " \nWithin range: "+order.isWithinRange(myLocation));
//
//
//                        if (order.isWithinRange(myLocation) || true) {
//                            G.getInstance().textToSpeech.speak(order.getUserLocationFrameOrigin().getAddress(), TextToSpeech.QUEUE_FLUSH, null);
//                            G.getInstance().textToSpeech.speak(" на " +order.getUserLocationFrameDestination().getAddress(), TextToSpeech.QUEUE_ADD, null);
//                            G.getInstance().textToSpeech.speak(" цена " + (int) order.getOrderDetailsFrame().getPrice() +" гривен.", TextToSpeech.QUEUE_ADD, null);
//                            AlertDialog alertDialog = new AlertDialog.Builder(c).create();
//
//                            // Setting Dialog Title
//                            alertDialog.setTitle("New order in range");
//
//                            // Setting Dialog Message
//                            alertDialog.setMessage("A: " + order.getUserLocationFrameOrigin().getAddress() +
//                                    "\nB: " + order.getUserLocationFrameDestination().getAddress() +
//                                    "\n$: " + order.getOrderDetailsFrame().getPrice());
//
//                            // Setting Icon to Dialog
//                            alertDialog.setIcon(android.R.drawable.ic_dialog_map);
//
//                            // Setting OK Button
//                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            });
//
//                            // Showing Alert Message
//                            alertDialog.show();
//                        }
//
//                    }
//                });
//                orderConsumer.Start(C.DEFAULT_ORDERS_EXCHANGE, "");
//                break;
//            case R.id.btnStopOrderConsumer:
//                U.Log(TAG,"Order Consumer Stopped");
//                if (orderConsumer != null) orderConsumer.Stop();
//                break;
//
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
//                        UserFrame user = new UserFrame(true);
//                        LocationFrame origin = new LocationFrame(C.LOCATION_KIEV.latitude,C.LOCATION_KIEV.longitude);
//                        LocationFrame destination = new LocationFrame(myLocation.getLatitude(),myLocation.getLongitude());
//                        OrderDetailsFrame orderDetails = new OrderDetailsFrame(36.0f);
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
//                        UserFrame user = new UserFrame(true);
//                        LocationFrame origin = new LocationFrame(myLocation.getLatitude(),myLocation.getLongitude());
//                        LocationFrame destination = new LocationFrame(C.LOCATION_KIEV.latitude,C.LOCATION_KIEV.longitude);
//                        OrderDetailsFrame orderDetails = new OrderDetailsFrame(36.0f);
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
//
//                Handler handler2 = new Handler();
//                Runnable placeRandomOrder = new Runnable() {
//                    public void run() {
//                        NetworkMessage msg = new NetworkMessage();
//                        msg.setExchange(C.DEFAULT_ORDERS_EXCHANGE);
//                        msg.setExchangeType("direct");
//
//                        UserFrame user = new UserFrame(true);
//                        LocationFrame origin = U.getRandomLocation(C.LOCATION_KIEV.latitude,C.LOCATION_KIEV.longitude,20000);
//                        LocationFrame destination = U.getRandomLocation(C.LOCATION_KIEV.latitude,C.LOCATION_KIEV.longitude,20000);
//                        Random rnd = new Random();
//                        OrderDetailsFrame orderDetails = new OrderDetailsFrame(rnd.nextInt(50)+36);
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
//                LocationFrame locationFrame;
//                for (int i=0; i<10; ++i){
//                    locationFrame = U.getRandomLocation(C.LOCATION_KIEV.latitude,C.LOCATION_KIEV.longitude,20000);
//                    NetworkMessage msg = new NetworkMessage();
//                    msg.setExchange(C.DEFAULT_LOCATIONS_EXCHANGE);
//                    msg.setExchangeType("direct");
//
//                    Random rnd = new Random();
//                    UserFrame user = new UserFrame(rnd.nextBoolean());
//                    UserLocationPacket pkt = new UserLocationPacket(user,locationFrame);
//
//                    msg.setData(pkt.toString());
//                    MessageProducer messageProducer = new MessageProducer();
//                    messageProducer.execute(msg);
//                }
//                break;
//
//            case R.id.btnStartPoliceConsumer:
////                U.Log(TAG, "Police Consumer Started");
////                policeConsumer = new MessageConsumer();
////                policeConsumer.setOnReceiveMessageHandler(new MessageConsumer.OnReceiveMessageHandler() {
////                    public void onReceiveMessage(byte[] message) {
////                        String data = new String(message);
////                        PolicePacket policePacket = new PolicePacket(data);
////                        U.Log(TAG, "Received location: " + policePacket.toString());
////                        new TaxiDriverMarker(googleMap, policePacket);
////                    }
////                });
////                NetworkMessage msg = new NetworkMessage();
////                msg.setQueue(C.DEFAULT_POLICE_QUEUE);
////                msg.setDurable(true);
////                msg.setMessageProperties(MessageProperties.PERSISTENT_TEXT_PLAIN);
////                policeConsumer.Start(msg);
//                break;
//            case R.id.btnStopPoliceConsumer:
//                U.Log(TAG, "Police Consumer Stopped");
//                if (policeConsumer != null) policeConsumer.Stop();
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
