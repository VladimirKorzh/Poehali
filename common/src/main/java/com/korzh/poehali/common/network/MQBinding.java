package com.korzh.poehali.common.network;

import android.location.Location;

import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by futurewife on 12.07.14.
 */
public class MQBinding {

    private MessageConsumer messageConsumer = null;

    public MQBinding(){
        messageConsumer = new MessageConsumer();
    }

    public Location getCityCenterFromExchange(String targetExchange){
        // TODO
        Location loc = new Location("temp");
        loc.setLatitude(C.LOCATION_KIEV.latitude);
        loc.setLongitude(C.LOCATION_KIEV.longitude);
        return loc;
    }

    public String getTargetExchange(){
        // TODO
        return "kiev";
    }

    public void Start(){
        messageConsumer.Start(getTargetExchange());
    }

    public void Stop(){
        if (messageConsumer != null) messageConsumer.Stop();
    }


    public void ChangeBinds(final ArrayList<String> newBindings){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RabbitMQHelper rabbitMQHelper = RabbitMQHelper.getInstance();

                // if we don't have our connection -> reconnect
                Connection connection = rabbitMQHelper.getConnection();

                if (connection != null) {
                    try {
                        Channel channel = connection.createChannel();

                        channel.queueDeclare(G.getInstance().userId, false, false, true, null).getQueue();
                        channel.queuePurge(G.getInstance().userId);
                        if (messageConsumer.binds != null) {

                            // remove duplicates that are present in both
                            Iterator<String> iter = messageConsumer.binds.iterator();
                            while (iter.hasNext()) {
                                if (newBindings.contains(iter.next())) {
                                    iter.remove();
                                }
                            }
                            // unbind the ones we don't need anymore
                            for (String old : messageConsumer.binds) {
                                channel.queueUnbind(G.getInstance().userId, getTargetExchange(), old);
                                U.Log("BINDER", "unbound key: " + old);
                            }

                            messageConsumer.binds = newBindings;
                        }
                        // bind new
                        for (String n : newBindings) {
                            channel.queueBind(G.getInstance().userId, getTargetExchange(), n);
                            U.Log("BINDER", "bound key: " + n);
                        }
                        channel.close();

                    } catch (IOException e) {
                        U.Log("BINDER", "Error when trying to bind " +e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void SendMessage(final String msg, final String type, final Location senderLocation){

        new Thread(new Runnable() {
            private final String TAG = "MessageSender";
            private volatile SortedSet<Long> unconfirmedSet =
                    Collections.synchronizedSortedSet(new TreeSet());

            ConfirmListener confirmListener = new ConfirmListener() {
                public void handleAck(long seqNo, boolean multiple) {
                    if (multiple) {
                        unconfirmedSet.headSet(seqNo + 1).clear();
                        U.Log(getClass().getSimpleName(), "Delivered up to: " + seqNo);
                    } else {
                        unconfirmedSet.remove(seqNo);
                        U.Log(getClass().getSimpleName(), "Delivered: " + seqNo);
                    }
                }

                public void handleNack(long seqNo, boolean multiple) {
                    if (multiple) {
                        U.Log(getClass().getSimpleName(), "Not delivered up to: " + seqNo);
                    } else {
                        U.Log(getClass().getSimpleName(), "Not delivered: " + seqNo);
                    }
                }
            };


            @Override
            public void run() {

                RabbitMQHelper rabbitMQHelper = RabbitMQHelper.getInstance();

                // if we have lost our connection -> reconnect
                Connection connection = rabbitMQHelper.getConnection();

                // we proceed only if we are currently connected
                if (connection == null) {
                    U.Log(TAG, "Message cannot be sent, due to connectivity problem.");
                }
                else{
                    try {
                        Channel channel = connection.createChannel();
                        channel.addConfirmListener(confirmListener);
                        channel.confirmSelect();
                        channel.exchangeDeclare(getTargetExchange(), "topic");

                        String routingKey = type+getMessageRoutingKey(getCityCenterFromExchange(getTargetExchange()), senderLocation);
                        channel.basicPublish(getTargetExchange(), routingKey, null, msg.getBytes());
                        channel.waitForConfirms();
                        channel.close();
                        U.Log(TAG, msg.getBytes().length+" bytes message sent: " + msg);
                    } catch (IOException e) {
                        U.Log(TAG, msg.getBytes().length+" bytes cannot be sent due to error: "+ msg);
                    } catch (InterruptedException e) {
                        U.Log(TAG, "Interrupted while trying to send: "+msg);
                    }
                }
            }
        }).start();
    }


















    private String getBearingString(float bearing){

        if (bearing < 0) bearing = 360 + bearing;
        U.Log("getBearingString", String.valueOf(bearing));

        if (bearing >= 0 && bearing < 45)     return "NE";
        if (bearing >= 45 && bearing < 90)    return "EN";
        if (bearing >= 90 && bearing < 135)   return "ES";
        if (bearing >= 135 && bearing < 180)  return "SE";
        if (bearing >= 180 && bearing < 225)  return "SW";
        if (bearing >= 225 && bearing < 270)  return "WS";
        if (bearing >= 270 && bearing < 315)  return "WN";
        if (bearing >= 315 && bearing < 360)    return "NW";

        return "*";
    }

    private ArrayList<String> getCloseBearings(float bearing){
        ArrayList<String> bearings = new ArrayList<String>();
        bearings.add(getBearingString(bearing-45));
        bearings.add(getBearingString(bearing+45));
        bearings.add(getBearingString(bearing));
        return  bearings;
    }


    public String getMessageRoutingKey(Location cityCenter, Location myLocation){

        float distance = myLocation.distanceTo(cityCenter);
        float bearing  = cityCenter.bearingTo(myLocation);

        int D = Math.round(distance/1000)+1;

        return "."+String.valueOf(D)+"."+getBearingString(bearing);
    }


    public ArrayList<String> getConsumerBindingKeysList(Location cityCenter, Location myLocation, int searchRadius){

        ArrayList<String> result = new ArrayList<String>();

        float distance = myLocation.distanceTo(cityCenter);
        float bearing  = cityCenter.bearingTo(myLocation);

        int D = Math.round(distance/1000)+1;
        int R = searchRadius;

        // if we are far away from center
        for (int i = D-R; i <= D+R; ++i) {

            if (i==0) continue;

            if (i>0) {
                // far away from center -> add only the directions
                for (String b : getCloseBearings(bearing)) {
                    result.add("*." + String.valueOf(i) + "." + b);
                }
            }
            else result.add("*." + String.valueOf(Math.abs(i)) + ".*");
        }
        return result;
    }

}
