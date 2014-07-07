package com.korzh.poehali.network;

import android.os.Handler;

import com.korzh.poehali.util.C;
import com.korzh.poehali.util.U;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

/**
 * Created by vladimir on 7/1/2014.
 */
public class MessageConsumer{
    private final String TAG = getClass().getName();
    private boolean Running = true;

    private static RabbitMQHelper rabbitMQHelper = null;
    private byte[] mLastMessage;

    // An interface to be implemented by an object that is interested in messages(listener)
    public interface OnReceiveMessageHandler{
        public void onReceiveMessage(byte[] message);
    };

    //A reference to the listener, we can only have one at a time(for now)
    private OnReceiveMessageHandler mOnReceiveMessageHandler;

    public void setOnReceiveMessageHandler(OnReceiveMessageHandler handler){
        mOnReceiveMessageHandler = handler;
    };

    private Handler mMessageHandler = new Handler();
    private Handler mConsumeHandler = new Handler();

    // Create runnable for posting back to main thread
    final Runnable mReturnMessage = new Runnable() {
        public void run() {
            mOnReceiveMessageHandler.onReceiveMessage(mLastMessage);
        }
    };


    public void Stop(){
        Running = false;
    }

    public void Start(final String exchange, final String bindingKey) {
        new Thread(new Runnable() {
            public void run() {

                if (rabbitMQHelper == null) rabbitMQHelper = new RabbitMQHelper();

                try {
                    rabbitMQHelper.connect(C.MQ_CONSUMER_URI);
                    Channel channel = rabbitMQHelper.connection.createChannel();
                    String mQueue = channel.queueDeclare().getQueue();
                    channel.exchangeDeclare(exchange, "direct");
                    channel.queueBind(mQueue, exchange, bindingKey);
                    QueueingConsumer mySubscription = new QueueingConsumer(channel);
                    channel.basicConsume(mQueue, false, mySubscription);

                    while(Running){
                        QueueingConsumer.Delivery delivery;
                        delivery = mySubscription.nextDelivery();
                        mLastMessage = delivery.getBody();
                        mMessageHandler.post(mReturnMessage);
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    }
                } catch (Exception e) {
                    U.Log(getClass().getName(),"Error while consuming: "+e.getMessage());
                }

            }
        }).start();
    }


    public void Start(NetworkMessage msg){

        if (rabbitMQHelper == null) rabbitMQHelper = new RabbitMQHelper();

        try {
            rabbitMQHelper.connect(C.MQ_CONSUMER_URI);
            Channel channel = rabbitMQHelper.connection.createChannel();
            channel.queueDeclare(msg.getQueue(), msg.isDurable(), false, false, msg.getArguments());

            //channel.basicQos(1);

            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(msg.getQueue(), false, consumer);

            while (true) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                String message = new String(delivery.getBody());
                mLastMessage = delivery.getBody();
                mMessageHandler.post(mReturnMessage);

                channel.basicReject(delivery.getEnvelope().getDeliveryTag(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
