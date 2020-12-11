package com.khamurai.labb2;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TemperatureManager implements Runnable {

    String responseMessage = "";

    void subscribe(String topic, int qos, MqttClient client) throws MqttException {
        MqttCallback callback = new MqttCallback() {

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("RECEIVED FROM SUBSCRIPTION: " + message);
                checkTemperature(message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }

            @Override
            public void connectionLost(Throwable cause) {
                cause.printStackTrace();
            }
        };
        client.subscribe(topic);
        client.setCallback(callback);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String topic = "KYH/Temp";
        String responseTopic = "KYH/Response";
        int qos = 2;
        String broker = "tcp://broker.hivemq.com:1883";
        String clientId = "TempManager";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            subscribe(topic, qos, sampleClient);
            while(sampleClient.isConnected()) {
                Thread.sleep(1000);
                System.out.println("Publishing response: " + responseMessage);
                MqttMessage message = new MqttMessage(responseMessage.getBytes());
                message.setQos(qos);
                sampleClient.publish(responseTopic, message);
                System.out.println("TempManager Message published");
                Thread.sleep(59000);
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void checkTemperature(String temp) {
        double parsedTemp = Double.valueOf(temp);
        if(parsedTemp < 22.0) {
            responseMessage = "+";
        } else {
            responseMessage = "-";
        }
    }
}
