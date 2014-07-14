package com.korzh.poehali.common.network;

import android.os.Handler;

import com.korzh.poehali.common.network.packets.OrderPacket;
import com.korzh.poehali.common.network.packets.UserLocationPacket;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by vladimir on 7/12/2014.
 */
public class MessageConsumer{

    public MessageConsumer(){
        registerCallback(mLocationStasher);
        registerCallback(mOrderStasher);
    }

    //  List to hold all of the current MQ bindings our queue has
    public ArrayList<String> binds = new ArrayList<String>();

    //  Control of execution
    public void Stop(){
        Running = false;
    }

    //  An interface to be implemented by an object that is interested in messages(listener)
    public interface OnReceiveMessageHandler{
        public void onReceive(JSONObject obj);
        public String getType();
    };

    public void registerCallback(OnReceiveMessageHandler callback){
        mOnReceiveCallbacks.add(callback);
    }

    public void removeCallback(OnReceiveMessageHandler callback){
        mOnReceiveCallbacks.remove(callback);
    }

    public ArrayList<UserLocationPacket> getReceivedLocations() {
        return receivedLocations;
    }

    public ArrayList<OrderPacket> getReceivedOrders() {
        return receivedOrders;
    }




    //  Logging tag
    private final String TAG = "MessageConsumer";

    //  Variable for thread execution control
    private boolean Running = true;

    //  Structures that store messages
    private ArrayList<UserLocationPacket> receivedLocations = new ArrayList<UserLocationPacket>();
    private ArrayList<OrderPacket> receivedOrders = new ArrayList<OrderPacket>();

    //  Handler for all of spawned runnables
    private Handler handler = new Handler();

    //  Registered callbacks
    private ArrayList<OnReceiveMessageHandler> mOnReceiveCallbacks = new ArrayList<OnReceiveMessageHandler>();

    //  Function that takes care of firing all the callbacks
    private void HandleIncomingPacket(final QueueingConsumer.Delivery delivery){
        // log the message tha we have received
        U.Log(TAG, delivery.getEnvelope().getExchange()+"."+delivery.getEnvelope().getRoutingKey() + " " +delivery.getBody().length + " bytes received msg: " + new String(delivery.getBody()));
        String msgType = delivery.getEnvelope().getRoutingKey().split(".")[0];

        // fire all matching listeners
        for (final OnReceiveMessageHandler call : mOnReceiveCallbacks){
            if (call.getType().equals(msgType)){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String msg = new String(delivery.getBody());
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(msg);
                        } catch (JSONException e) {
                            U.Log("LocationStasher", "Error reading JSON.");
                        }
                        call.onReceive(obj);
                    }
                });
            }
        }
    }

    private OnReceiveMessageHandler mLocationStasher = new OnReceiveMessageHandler() {
        @Override
        public void onReceive(JSONObject obj) {
            final UserLocationPacket pkt = new UserLocationPacket(obj);
            receivedLocations.add(pkt);
            Runnable expire = new Runnable(){
                public void run(){
                    receivedLocations.remove(pkt);
                }
            };
            // post an expiration timer
            handler.postDelayed(expire, C.LOCATION_BROADCASTER_UPDATES_MILLIS);
        }

        @Override
        public String getType() {
            return "location";
        }
    };
    private OnReceiveMessageHandler mOrderStasher = new OnReceiveMessageHandler() {
        @Override
        public void onReceive(JSONObject obj) {
            final OrderPacket pkt = new OrderPacket(obj);

            if (pkt.getAction() != OrderPacket.CANCEL) {
                // unless its a cancel packet => update records
                Iterator<OrderPacket> iter = receivedOrders.iterator();
                while (iter.hasNext()) {
                    OrderPacket o = iter.next();
                    // if the new packet is an update
                    if (o.getUserFrame().getUserId() == pkt.getUserFrame().getUserId()) {
                        o.UpdateOrder(pkt);
                        return;
                    }
                }
                // if we don't have info about that one => add it
                receivedOrders.add(pkt);
            }
            else {
                // this is a cancel packet -> remove records
                receivedOrders.remove(pkt);
            }
        }

        @Override
        public String getType() {
            return "order";
        }
    };



    public void Start(final String exchange) {
        Running = true;

        new Thread( new Runnable() {
            public void run() {

                while (Running) {
                    RabbitMQHelper rabbitMQHelper = RabbitMQHelper.getInstance();
                    Connection connection = RabbitMQHelper.getInstance().getConnection();

                    // if we are connected -> start consuming
                    if (connection != null) {
                        Channel channel = null;
                        try {
                            channel = connection.createChannel();
                            channel.addShutdownListener(new ShutdownListener() {
                                @Override
                                public void shutdownCompleted(ShutdownSignalException cause) {
                                    if (!cause.isInitiatedByApplication()) {
                                        U.Log(TAG, "Connection to MQ lost. Reason: " + cause.getReason());
                                    }
                                }
                            });
                            String queueName = G.getInstance().userId;
                            String mQueue = channel.queueDeclare(queueName, false, false, true, null).getQueue();
                            channel.exchangeDeclare(exchange, "topic");
                            channel.queuePurge(mQueue);
                            if (binds != null){
                                for (String b : binds) channel.queueBind(mQueue, exchange, b);
                            }

                            QueueingConsumer mySubscription = new QueueingConsumer(channel);
                            channel.basicConsume(mQueue, false, mySubscription);

                            while (Running) {
                                QueueingConsumer.Delivery delivery;
                                delivery = mySubscription.nextDelivery();

                                HandleIncomingPacket(delivery);

                                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                            }

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
