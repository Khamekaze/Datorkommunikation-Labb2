package com.khamurai.labb2;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TemperatureManager implements MqttCallback {

    String responseMessage = "";
    String topic = Constants.TOPIC_TEMP;
    String responseTopic = Constants.TOPIC_CONTROLLER;
    MqttClient sampleClient = null;
    boolean connected = true;

    public TemperatureManager() {
        String broker = Constants.BROKER_CONNECTION;
        String clientId = "TempManager";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
            System.out.println("Connecting to broker: " + broker);

            sampleClient.connect(connOpts);

            if(sampleClient.isConnected()) {
                System.out.println("Connected");
            }
            sampleClient.setCallback(this);
            sampleClient.subscribe(topic);

            while(connected) {

            }
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TemperatureManager manager = new TemperatureManager();
    }

    String checkTemperature(String temp) {
        double parsedTemp = 0.0;
        try {
            parsedTemp = Double.parseDouble(temp);
        } catch (NumberFormatException n) {
            return "/";
        }

        if(parsedTemp < 22.0) {
            return "+";
        } else {
            return "-";
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println("RECEIVED FROM SUBSCRIPTION: " + mqttMessage);
        responseMessage = checkTemperature(mqttMessage.toString());
        MqttMessage newMessage = new MqttMessage(responseMessage.getBytes());
        sampleClient.publish(responseTopic, newMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
