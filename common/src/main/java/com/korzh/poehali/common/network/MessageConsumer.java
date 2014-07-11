package com.korzh.poehali.common.network;

import android.os.Handler;

import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.U;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Created by vladimir on 7/1/2014.
 */
public class MessageConsumer{
    private boolean Running = true;

    private final String TAG = "MessageConsumer";

    private static RabbitMQHelper rabbitMQHelper = null;
    private QueueingConsumer.Delivery mLastDelivery;

    // An interface to be implemented by an object that is interested in messages(listener)
    public interface OnReceiveMessageHandler{
        public void onReceiveMessage(QueueingConsumer.Delivery delivery);
    };

    //A reference to the listener, we can only have one at a time(for now)
    private OnReceiveMessageHandler mOnReceiveMessageHandler;

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


    int counter = 0;



    public void Start(final String exchange, final String bindingKey, final String queueName) {
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
                            String mQueue;
                            mQueue = channel.queueDeclare(queueName, false, false, true, null).getQueue();
                            channel.exchangeDeclare(exchange, "direct");
                            channel.queueBind(mQueue, exchange, bindingKey);
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
