package com.korzh.poehali.network;

import com.korzh.poehali.util.U;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import java.io.IOException;

/**
 * Created by vladimir on 7/1/2014.
 */
public class RabbitMQHelper {

    private static final String TAG = "RabbitMQHelper";

    public Connection connection = null;

    public boolean isConnected(){
        if (connection == null) return false;
        if (connection.isOpen()) return true;
        return false;
    }


    public void connect(String uri){
        if (!isConnected()) {
            U.Log(TAG, "Creating connection to Rabbit MQ Server");
            try {
                disconnect();
                ConnectionFactory connectionFactory = new ConnectionFactory();
                connectionFactory.setUri(uri);
                connectionFactory.setRequestedHeartbeat(60);
                connection = connectionFactory.newConnection();
                connection.addShutdownListener(new ShutdownListener() {
                    @Override
                    public void shutdownCompleted(ShutdownSignalException e) {
                        connection = null;
                    }
                });
            } catch (Exception e) {
                U.Log(getClass().getName(),"Error connecting to MQ: " + e.getMessage());
            }
        }
    }

    public void disconnect(){
        if (connection != null){
            U.Log(TAG, "Disconnecting from Rabbit MQ server");
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
