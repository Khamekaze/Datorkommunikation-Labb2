package com.khamurai.labb2;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Random;


public class Main {

    /*
    static TemperatureClient temperatureClient;
    static  TemperatureManager temperatureManager;

     */

    public static void main(String[] args) {
        Thread t1 = new Thread(new TemperatureClient());
        Thread t2 = new Thread(new TemperatureManager());
        Thread t3 = new Thread(new DataLogger());

        t1.start();
        t2.start();
        t3.start();
    }
}
