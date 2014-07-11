package com.korzh.poehali.common.network;

import android.os.AsyncTask;

import com.korzh.poehali.common.util.U;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by vladimir on 6/30/2014.
 */
public class MessageProducer extends AsyncTask<NetworkMessage, Void, String> {

    private final String TAG = "MessageProducer";
    private volatile SortedSet<Long> unconfirmedSet =
            Collections.synchronizedSortedSet(new TreeSet());


    @Override
    protected String doInBackground(NetworkMessage... msgs) {
        NetworkMessage msg = msgs[0];


            RabbitMQHelper rabbitMQHelper = RabbitMQHelper.getInstance();

            // if we have lost our connection -> reconnect
            if (rabbitMQHelper.connection == null) rabbitMQHelper.connect();

            // we proceed only if we are currently connected
            if (rabbitMQHelper.connection != null) {
                try {
                    Channel channel = rabbitMQHelper.connection.createChannel();
                    channel.addConfirmListener(new ConfirmListener() {
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
                    });
                    channel.confirmSelect();
                    if (!msg.getExchange().equals(""))
                        channel.exchangeDeclare(msg.getExchange(), msg.getExchangeType());
                    if (!msg.getQueue().equals(""))
                        channel.queueDeclare(msg.getQueue(), msg.isDurable(), false, false, msg.getArguments());
                    channel.basicPublish(msg.getExchange(), msg.getQueue(), msg.getMessageProperties(), msg.getData());
                    channel.waitForConfirms();
                    channel.close();
                    String st = new String(msg.getData(), "UTF8");
                    U.Log(TAG, "Message sent: " + st);
                }
                catch (Exception e) {
                    U.Log(getClass().getSimpleName(), "Error sending message: "+ e.getMessage());
                }
            }
            else{
                U.Log(TAG, "Message cannot be sent, due to connectivity problem.");
            }
        return null;
    }
}