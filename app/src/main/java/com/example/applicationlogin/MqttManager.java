package com.example.applicationlogin;

import android.util.Log;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttManager {

    private MqttClient mqttClient;

    private final String BROKER_URL = "tcp://172.20.10.2:1883";
    private final String CLIENT_ID = MqttClient.generateClientId();

    public void connect(MqttMessageListener listener) {
        try {
            mqttClient = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            // options.setUserName("ton_user"); // Décommenter si ton broker a un mot de passe
            // options.setPassword("ton_mdp".toCharArray());



            // On écoute les messages entrants
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e("MQTT", "Connexion perdue !");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    Log.d("MQTT", "Message reçu sur " + topic + " : " + payload);
                    // On transmet le message à l'activité
                    listener.onMessageReceived(topic, payload);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            mqttClient.connect(options);
            Log.d("MQTT", "Connecté au broker !");

        } catch (MqttException e) {
            e.printStackTrace();
            Log.e("MQTT", "Erreur de connexion : " + e.getMessage());
            if (e.getCause() != null) {
                Log.e("MQTT", "Raison exacte : " + e.getCause().getMessage());
            }
        }
    }

    public void subscribeToTopic(String topic) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.subscribe(topic, 0); 
                Log.d("MQTT", "Abonné au topic : " + topic);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public interface MqttMessageListener {
        void onMessageReceived(String topic, String message);
    }
}