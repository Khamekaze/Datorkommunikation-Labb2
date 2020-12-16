package com.khamurai.labb2;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TemperatureClient {

    public TemperatureClient() {
        String topic = Constants.TOPIC_TEMP;
        String content = "";
        int qos = 1;
        String broker = Constants.BROKER_CONNECTION;
        String clientId = "TempClient";
        String lastWill = "ERROR";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            System.out.println("Connecting to broker: " + broker);
            connOpts.setWill(Constants.TOPIC_TEMP, lastWill.getBytes(), 2, false);
            sampleClient.connect(connOpts);
            if(sampleClient.isConnected()) {
                System.out.println("Connected");
            }

            while(sampleClient.isConnected()) {
                content = setNewTemperature();
                System.out.println("Publishing message: " + content);
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);
                sampleClient.publish(topic, message);
                System.out.println("TempClient Message published");
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

    public static void main(String[] args) {
        TemperatureClient client = new TemperatureClient();
    }

    String setNewTemperature() {
        double temp = ((Math.random() * (25 - 15)) + 15);
        DecimalFormat df = new DecimalFormat("#.##");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(symbols);
        return df.format(temp);
    }
}
