package com.korzh.poehali.common.network;

import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.U;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 * Created by vladimir on 7/1/2014.
 */
public class RabbitMQHelper {

    public Connection connection = null;

    private static RabbitMQHelper instance = null;

    public RabbitMQHelper(){
        instance = this;
    }

    public static RabbitMQHelper getInstance(){
        if (instance == null) {
            instance = new RabbitMQHelper();
            instance.connect();
        }
        return instance;
    }

    public void connect(){
        U.Log(getClass().getSimpleName(), "Creating connection to Rabbit MQ Server");
        try {
            disconnect();
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setUri(C.MQ_CONSUMER_URI);

            // sends keep alive packets every so often
            connectionFactory.setRequestedHeartbeat(C.MQ_CONNECTION_HEARTBEAT);
            connectionFactory.setAutomaticRecoveryEnabled(true);

            connection = connectionFactory.newConnection();

        } catch (Exception e) {
            U.Log(getClass().getSimpleName(),"Error connecting to MQ: " + e.getMessage());
        }
    }

    public void disconnect(){
        if (connection != null){
            U.Log(getClass().getSimpleName(), "Disconnecting from Rabbit MQ server");
            try {
                connection.close();
            } catch (IOException e) {
                U.Log(getClass().getSimpleName(), "Error disconnecting from MQ server");
            }
        }
    }



}
