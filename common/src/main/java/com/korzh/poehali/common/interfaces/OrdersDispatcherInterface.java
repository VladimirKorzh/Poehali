package com.korzh.poehali.common.interfaces;

import android.location.Location;
import android.os.Handler;

import com.korzh.poehali.common.network.MessageConsumer;
import com.korzh.poehali.common.network.MessageProducer;
import com.korzh.poehali.common.network.NetworkMessage;
import com.korzh.poehali.common.network.packets.OrderPacket;
import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.QueueingConsumer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by futurewife on 08.07.14.
 */
public class OrdersDispatcherInterface {

    private MessageConsumer ordersListener = null;
    private MessageConsumer repliesListener = null;



    MessageConsumer.OnReceiveMessageHandler onNewReplyReceive = new MessageConsumer.OnReceiveMessageHandler() {
        public void onReceiveMessage(QueueingConsumer.Delivery delivery) {
            //TODO
        }
    };



    MessageConsumer.OnReceiveMessageHandler onNewOrderReceive = new MessageConsumer.OnReceiveMessageHandler() {
        public void onReceiveMessage(QueueingConsumer.Delivery delivery) {
            byte[] message = delivery.getBody();
            String data = new String(message);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                U.Log(getClass().getSimpleName(), "Error reading json");
            }
            OrderPacket order = new OrderPacket(jsonObject);
            U.Log(getClass().getSimpleName(), "New order received: "+order.toString());

            Location myLocation = G.getInstance().getLastKnownLocation();
            if (order.isWithinRange(myLocation)) {
                // TODO
            }
        }
    };


    public void StartRepliesListener(){
        U.Log(getClass().getSimpleName(),"Replies Listener Started");
        repliesListener = new MessageConsumer();
        repliesListener.setOnReceiveMessageHandler(null);
        repliesListener.Start("", "", G.getInstance().userId+" replies");
    }
    public void StopRepliesListener(){
        U.Log(getClass().getSimpleName(),"Replies Listener Stopped");
        if (repliesListener != null) repliesListener.Stop();
    }

    public void StartOrdersListener(){
        U.Log(getClass().getSimpleName(),"Order Listener Started");
        ordersListener = new MessageConsumer();
        ordersListener.setOnReceiveMessageHandler(onNewOrderReceive);
        ordersListener.Start(C.DEFAULT_ORDERS_EXCHANGE, "", G.getInstance()+" orders");
    }
    public void StopOrdersListener(){
        U.Log(getClass().getSimpleName(),"Order Listener Stopped");
        if (ordersListener != null) ordersListener.Stop();
    }

    public void AnnounceOrder(final OrderPacket pkt){
        Handler postOrder = new Handler();
        final Runnable uploadLocation = new Runnable() {
            public void run() {
                NetworkMessage msg = new NetworkMessage();
                msg.setExchange(C.DEFAULT_ORDERS_EXCHANGE);
                msg.setExchangeType("direct");

                //set callback in msg properties
                AMQP.BasicProperties props = new AMQP.BasicProperties
                        .Builder()
                        .replyTo(String.valueOf(G.getInstance().userId+" replies"))
                        .build();

                msg.setData(pkt.toString());
                MessageProducer messageProducer = new MessageProducer();
                messageProducer.execute(msg);
            }
        };
        postOrder.post(uploadLocation);
    }
}
