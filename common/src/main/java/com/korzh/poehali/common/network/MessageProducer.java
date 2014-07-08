package com.korzh.poehali.common.network;

import android.os.AsyncTask;

import com.korzh.poehali.common.util.U;
import com.rabbitmq.client.Channel;

/**
 * Created by vladimir on 6/30/2014.
 */
public class MessageProducer extends AsyncTask<NetworkMessage, Void, String> {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected String doInBackground(NetworkMessage... msgs) {
        NetworkMessage msg = msgs[0];

        try {
            RabbitMQHelper rabbitMQHelper = RabbitMQHelper.getInstance();
            Channel channel = rabbitMQHelper.connection.createChannel();
            if (!msg.getExchange().equals("")) channel.exchangeDeclare(msg.getExchange(), msg.getExchangeType());
            if (!msg.getQueue().equals("")) channel.queueDeclare(msg.getQueue(), msg.isDurable(), false, false, msg.getArguments());
            channel.basicPublish(msg.getExchange(), msg.getQueue(), msg.getMessageProperties(), msg.getData());
            channel.close();
            String st = new String(msg.getData(), "UTF8");
            U.Log(TAG, "Message sent: " + st);
        } catch (Exception e) {
            U.Log(getClass().getSimpleName(), "Error sending message: "+e.getMessage());
        }

        return null;
    }
}