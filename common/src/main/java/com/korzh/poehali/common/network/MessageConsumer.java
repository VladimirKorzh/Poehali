package com.korzh.poehali.common.network;

import android.os.Handler;

import com.korzh.poehali.common.util.G;
import com.korzh.poehali.common.util.U;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

/**
 * Created by vladimir on 7/1/2014.
 */
public class MessageConsumer{
    private boolean Running = true;

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
    public void Start(final String exchange, final String bindingKey){
        Start(exchange,bindingKey, G.getInstance().userId+" "+counter);
        counter++;
        U.Log(getClass().getSimpleName(), "WARNING Using old format for queue name!");
    }

    public void Start(final String exchange, final String bindingKey, final String queueName) {
        new Thread(new Runnable() {
            public void run() {

                rabbitMQHelper = RabbitMQHelper.getInstance();

                try {
                    Channel channel = rabbitMQHelper.connection.createChannel();
                    String mQueue;
                    mQueue = channel.queueDeclare(queueName, false, false, true, null).getQueue();
                    channel.exchangeDeclare(exchange, "direct");
                    channel.queueBind(mQueue, exchange, bindingKey);
                    QueueingConsumer mySubscription = new QueueingConsumer(channel);
                    channel.basicConsume(mQueue, false, mySubscription);

                    while(Running){
                        QueueingConsumer.Delivery delivery;
                        delivery = mySubscription.nextDelivery();
                        mLastDelivery = delivery;
                        mMessageHandler.post(mReturnDelivery);
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    }
                    channel.close();
                } catch (Exception e) {
                    U.Log(getClass().getSimpleName(),"Error while consuming: "+e.getMessage());
                }
            }
        }).start();
    }
}
