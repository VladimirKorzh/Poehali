package com.korzh.poehali.common.network;

import android.os.Handler;

import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import java.util.ArrayList;

/**
 * Created by vladimir on 7/12/2014.
 */
public class MessageConsumer{
    private boolean Running = true;

    private final String TAG = "MessageConsumer";

    private static RabbitMQHelper rabbitMQHelper = null;
    private QueueingConsumer.Delivery mLastDelivery;
    public ArrayList<String> binds = new ArrayList<String>();

    // An interface to be implemented by an object that is interested in messages(listener)
    public interface OnReceiveMessageHandler{
        public void onReceiveMessage(QueueingConsumer.Delivery delivery);
    };

    //A reference to the listener, we can only have one at a time(for now)
    private OnReceiveMessageHandler mOnReceiveMessageHandler = new OnReceiveMessageHandler() {
        @Override
        public void onReceiveMessage(QueueingConsumer.Delivery delivery) {
            //U.Log(TAG, delivery.getEnvelope().getExchange()+"."+delivery.getEnvelope().getRoutingKey() + " " +delivery.getBody().length + " bytes received msg: " + new String(delivery.getBody()));
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

    public MessageConsumer(){}

    public void Stop(){
        Running = false;
    }


    public void Start(final String exchange) {
        Running = true;

        new Thread( new Runnable() {
            public void run() {

                while (Running) {
                    rabbitMQHelper = RabbitMQHelper.getInstance();
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
                                mLastDelivery = delivery;
                                U.Log(TAG, delivery.getEnvelope().getExchange()+"."+delivery.getEnvelope().getRoutingKey() + " " +delivery.getBody().length + " bytes received msg: " + new String(delivery.getBody()));
                                mMessageHandler.post(mReturnDelivery);
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
