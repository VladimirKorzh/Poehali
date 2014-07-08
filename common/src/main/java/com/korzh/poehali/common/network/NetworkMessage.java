package com.korzh.poehali.common.network;

import com.rabbitmq.client.AMQP;

import java.util.Map;

/**
 * Created by vladimir on 6/30/2014.
 */
public class NetworkMessage {
    private String queue = "";
    private boolean durable = false;
    private AMQP.BasicProperties messageProperties = null;
    private Map<String, Object> arguments = null;

    private String exchange = "";
    private String exchangeType = "";
    private String data = "";
    private String routingKey = "";

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public byte[] getData() {
        return data.getBytes();
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public AMQP.BasicProperties getMessageProperties() {
        return messageProperties;
    }

    public void setMessageProperties(AMQP.BasicProperties messageProperties) {
        this.messageProperties = messageProperties;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }
}
