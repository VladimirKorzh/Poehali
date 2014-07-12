package com.korzh.poehali.common.network;

import android.location.Location;
import android.os.Handler;

import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by futurewife on 12.07.14.
 */
public class MQBinding {

    private MessageConsumer messageConsumer = null;
    private String targetExchange = null;
    private Location cityCenter = null;
    private ArrayList<String> bindingKeys = null;

    public void Listen(){
        messageConsumer = new MessageConsumer();
        messageConsumer.Start(targetExchange, bindingKeys);
    }
    public void Stop(){
        if (messageConsumer != null) messageConsumer.Stop();
    }


    public void ChangeBinds(ArrayList<String> newBindings){
        rabbitMQHelper = RabbitMQHelper.getInstance();

        // if we don't have our connection -> reconnect
        if (rabbitMQHelper.connection == null) rabbitMQHelper.connect();

        if (rabbitMQHelper.connection != null) {
            Channel channel = rabbitMQHelper.connection.createChannel();

            if (bindingKeys != null) {
                for (String oldB : bindingKeys) {
                    if (newBindings.contains(oldB)) {
                        bindingKeys.remove(oldB);
                    }
                }

                // unbind old
                for (String old : bindingKeys){
                    channel.unbind(G.getInstance().userId, targetExchange, old);
                    U.Log("BINDER", "unbound key: "+ old);
                }

                bindingKeys = newBindings;
            }
                // bind new
                for (String n : newBindings){
                    channel.bind(G.getInstance().userId, targetExchange, n);
                    U.Log("BINDER", "bound key: "+ n);
                }


            channel.close();
        }
    }


    public void SendMessage(String msg, String type, final Location senderLocation){

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
                if (rabbitMQHelper.connection == null) rabbitMQHelper.connect();

                // we proceed only if we are currently connected
                if (rabbitMQHelper.connection == null) {
                    U.Log(TAG, "Message cannot be sent, due to connectivity problem.");
                }
                else{
                    Channel channel = rabbitMQHelper.connection.createChannel();
                    channel.addConfirmListener(confirmListener);
                    channel.confirmSelect();
                    channel.exchangeDeclare(targetExchange, "topic");

                    String routingKey = type+getMessageRoutingKey(cityCenter, senderLocation);
                    channel.basicPublish(targetExchange, routingKey, null, msg.getBytes());
                    channel.waitForConfirms();
                    channel.close();
                    U.Log(TAG, msg.getBytes().length+" bytes message sent: " + msg);
                }
            }
        }).start();
    }


    private class MessageConsumer{
        private boolean Running = true;

        private final String TAG = "MessageConsumer";

        private static RabbitMQHelper rabbitMQHelper = null;
        private QueueingConsumer.Delivery mLastDelivery;

        // An interface to be implemented by an object that is interested in messages(listener)
        public interface OnReceiveMessageHandler{
            public void onReceiveMessage(QueueingConsumer.Delivery delivery);
        };

        //A reference to the listener, we can only have one at a time(for now)
        private OnReceiveMessageHandler mOnReceiveMessageHandler = new OnReceiveMessageHandler() {
            @Override
            public void onReceiveMessage(QueueingConsumer.Delivery delivery) {
                U.Log(TAG, delivery.getBody().length +" bytes received msg: "+new String(delivery.getBody()));
            }
        };

        public void setOnReceiveMessageHandler(OnReceiveMessageHandler handler){
            mOnReceiveMessageHandler = handler;
        };

        private Handler mMessageHandler = new Handler();

        // Create runnable for posting back to main thread
        final Runnable mReturnDelivery = new Runnable() {
            public void run() {
                mOnReceiveMessageHandler.onReceiveMessage(mLastDelivery);
            }
        };

        public void Stop(){
            Running = false;
        }


        public void Start(final String exchange, final ArrayList<String> bindingKeys) {
            Running = true;

            new Thread( new Runnable() {
                public void run() {

                    while (Running) {
                        rabbitMQHelper = RabbitMQHelper.getInstance();

                        // if we don't have our connection -> reconnect
                        if (rabbitMQHelper.connection == null) rabbitMQHelper.connect();

                        // if we are connected -> start consuming
                        if (rabbitMQHelper.connection != null) {
                            try {
                                Channel channel = rabbitMQHelper.connection.createChannel();
                                channel.addShutdownListener(new ShutdownListener() {
                                    @Override
                                    public void shutdownCompleted(ShutdownSignalException cause) {
                                        if (!cause.isInitiatedByApplication()) {
                                            U.Log(TAG, "Connection to MQ lost. Reason: " + cause.getReason());
                                        }
                                    }
                                });
                                String queueName = G.getInstance().userId;
                                String mQueue = channel.queueDeclare(queueName, false, false, true, true, null).getQueue();
                                channel.exchangeDeclare(exchange, "topic");

                                QueueingConsumer mySubscription = new QueueingConsumer(channel);
                                channel.basicConsume(mQueue, false, mySubscription);

                                while (Running) {
                                    QueueingConsumer.Delivery delivery;
                                    delivery = mySubscription.nextDelivery();
                                    mLastDelivery = delivery;
                                    mMessageHandler.post(mReturnDelivery);
                                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                                }
                                channel.close();
                            } catch (Exception e) {
                                U.Log(TAG, "Error while consuming: " + e.getMessage());
                            }
                        } else {
                            U.Log(TAG, "Consumer is not working, due to connectivity problems.");

                            try {
                                Thread.sleep(C.MQ_CONSUMER_AUTORECONNECT_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }















    private String getBearingString(float bearing){
        if (bearing >= 0 && bearing < 45)     return "NE";
        if (bearing >= 45 && bearing < 90)    return "EN";
        if (bearing >= 90 && bearing < 135)   return "ES";
        if (bearing >= 135 && bearing < 180)  return "SE";
        if (bearing >= 180 && bearing < 225)  return "SW";
        if (bearing >= 225 && bearing < 270)  return "WS";
        if (bearing >= 270 && bearing < 315)  return "WN";
        if (bearing >= 315 && bearing < 0)    return "NW";

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
        for (int i = D - R; i <= R; ++i) {

            if (i==0) continue;


            if (i>0) {
                // far away from center -> add only the directions
                for (String b : getCloseBearings(bearing)) {
                    result.add("." + String.valueOf(i) + "." + b);
                }
            }
            else result.add("." + String.valueOf(Math.abs(i)) + ".*");
        }
        return result;
    }

}
