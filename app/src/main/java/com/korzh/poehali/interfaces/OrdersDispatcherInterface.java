package com.korzh.poehali.interfaces;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.speech.tts.TextToSpeech;

import com.korzh.poehali.network.MessageConsumer;
import com.korzh.poehali.network.packets.OrderPacket;
import com.korzh.poehali.util.C;
import com.korzh.poehali.util.G;
import com.korzh.poehali.util.U;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by futurewife on 08.07.14.
 */
public class OrdersDispatcherInterface {

    private Context context = null;
    private MessageConsumer orderConsumer = null;

    MessageConsumer.OnReceiveMessageHandler onNewOrderReceive = new MessageConsumer.OnReceiveMessageHandler() {
        public void onReceiveMessage(byte[] message) {
            String data = new String(message);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                U.Log(getClass().getName(), "Error reading json");
            }
            OrderPacket order = new OrderPacket(jsonObject);
            U.Log("Order received", order.toString());

            Location myLocation = G.getInstance().getLastKnownLocation();
            if (order.isWithinRange(myLocation)) {
                // TODO
            }
        }
    };

    public void StartListener(){
        U.Log(getClass().getName(),"Order Consumer Started");
        orderConsumer = new MessageConsumer();
        orderConsumer.setOnReceiveMessageHandler(onNewOrderReceive);
        orderConsumer.Start(C.DEFAULT_ORDERS_EXCHANGE, "");
    }
    public void StopListener(){
        U.Log(getClass().getName(),"Order Consumer Stopped");
        if (orderConsumer != null) orderConsumer.Stop();
    }



}
