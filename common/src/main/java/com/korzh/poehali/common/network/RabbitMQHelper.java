package com.korzh.poehali.common.network;

import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.U;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import java.io.IOException;

/**
 * Created by vladimir on 7/1/2014.
 */
public class RabbitMQHelper {

    private final String TAG = "RabbitMQHelper";

    private Connection connection = null;
    private static RabbitMQHelper instance = null;


    private static final int IDLE = 0;
    private static final int CONNECTING = 1;
    private static int status;

    private RabbitMQHelper(){
        instance = this;
    }

    public static RabbitMQHelper getInstance(){
        if (instance == null) {
            instance = new RabbitMQHelper();
            instance.connect();
        }
        return instance;
    }

    public Connection getConnection(){
        if (connection == null && status != CONNECTING){
            connect();
        }
        return connection;
    }


    private void connect(){
        try {
            status = CONNECTING;
            disconnect();
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setUri(C.MQ_CONSUMER_URI);

            // sends keep alive packets every so often
            connectionFactory.setRequestedHeartbeat(C.MQ_CONNECTION_HEARTBEAT);

            // set timeout
            //connectionFactory.setConnectionTimeout(C.MQ_CONNECTION_TIMEOUT);

            connection = connectionFactory.newConnection();
            connection.addShutdownListener(new ShutdownListener() {
                @Override
                public void shutdownCompleted(ShutdownSignalException cause) {
                    if (!cause.isInitiatedByApplication()) {
                        U.Log("RabbitMQHelper", "Connection to MQ lost. Reason: "+ cause.getReason());
                        connection = null;
                    }
                }
            });
            U.Log(TAG, "Connected to MQ.");
        } catch (Exception e) {
            U.Log(getClass().getSimpleName(),"Error connecting to MQ: " + e.getMessage());
        }
        finally {
            status = IDLE;
        }
    }

    public void disconnect(){
        if (connection != null){
            try {
                connection.close();
            } catch (IOException e) {
                U.Log(getClass().getSimpleName(), "Error disconnecting from MQ server");
            }
        }
    }
}
