package com.korzh.poehali.network;

import android.os.AsyncTask;

import com.korzh.poehali.util.C;
import com.korzh.poehali.util.U;
import com.rabbitmq.client.Channel;

/**
 * Created by vladimir on 6/30/2014.
 */
public class MessageProducer extends AsyncTask<NetworkMessage, Void, String> {

    private final String TAG = getClass().getName();

    private static RabbitMQHelper rabbitMQHelper = null;

    @Override
    protected  void onPreExecute(){
        if (rabbitMQHelper == null) rabbitMQHelper = new RabbitMQHelper();
    }

    @Override
    protected String doInBackground(NetworkMessage... msgs) {
        NetworkMessage msg = msgs[0];

        try {
            rabbitMQHelper.connect(C.MQ_PRODUCER_URI);
            Channel channel = rabbitMQHelper.connection.createChannel();
            if (!msg.getExchange().equals("")) channel.exchangeDeclare(msg.getExchange(), msg.getExchangeType());
            if (!msg.getQueue().equals("")) channel.queueDeclare(msg.getQueue(), msg.isDurable(), false, false, msg.getArguments());
            channel.basicPublish(msg.getExchange(), msg.getQueue(), msg.getMessageProperties(), msg.getData());
            channel.close();
            String st = new String(msg.getData(), "UTF8");
            U.Log(TAG, "Message sent: " + st);
        } catch (Exception e) {
            U.Log(getClass().getName(), "Error sending message: "+e.getMessage());
        }

        return null;
    }
}