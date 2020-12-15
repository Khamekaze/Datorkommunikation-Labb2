package com.khamurai.labb2;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataLogger implements MqttCallback {

    String tempTopic = Constants.TOPIC_TEMP;
    String managerTopic = Constants.TOPIC_CONTROLLER;

    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public DataLogger() {
        String broker = Constants.BROKER_CONNECTION;
        String clientId = "DataLogger";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            sampleClient.setCallback(this);

            sampleClient.subscribe(tempTopic);
            sampleClient.subscribe(managerTopic);

            while (sampleClient.isConnected()) {

            }
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
        DataLogger logger = new DataLogger();
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

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println("DATALOGGER GOT MESSAGE: " + mqttMessage);
        writeToFile(s, mqttMessage.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
