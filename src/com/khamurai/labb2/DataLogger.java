package com.khamurai.labb2;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataLogger implements Runnable {

    String tempTopic = "KYH/Temp";
    String managerTopic = "KYH/Response";

    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void run() {
        int qos = 2;
        String broker = "tcp://broker.hivemq.com:1883";
        String clientId = "DataLogger";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            while(sampleClient.isConnected()) {
                subscribe(tempTopic, qos, sampleClient);
                subscribe(managerTopic, qos, sampleClient);
                Thread.sleep(1000);
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

    void subscribe(String topic, int qos, MqttClient client) throws MqttException {
        MqttCallback callback = new MqttCallback() {

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("DATALOGGER GOT MESSAGE: " + message);
                writeToFile(topic, message.toString());
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

    void writeToFile(String topic, String message) {
        String date = LocalDateTime.now().format(timeFormatter);
        String finalData = date + " | " + topic + " | " + message + "\n";
        try {
            FileWriter fileWriter = new FileWriter("logger.txt", true);
            fileWriter.write(finalData);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
