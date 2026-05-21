package com.example.applicationlogin;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.textfield.TextInputEditText;



public class MainActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etUsername = findViewById(R.id.et_login);
        etPassword = findViewById(R.id.et_passwd);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            user = "Riglet"; //A ENLEVER !!
            pass = "truc";//A ENLEVER !!
            if (user.isEmpty() || pass.isEmpty()) {
                showToast("Champs obligatoires !");
            } else {
                // processLogin(user, pass);
                GestionApi gapi = new GestionApi();
                gapi.processLogin(user, pass, this);
            }
        });

        MqttManager mqttManager = new MqttManager();
        new Thread(() -> {
            mqttManager.connect((topic, message) -> {


                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "ALERTE MQTT (" + topic + ") : " + message, Toast.LENGTH_LONG).show();


                });
            });



            mqttManager.subscribeToTopic("alerte/incendie");
            mqttManager.subscribeToTopic("alerte/renversement");
            mqttManager.subscribeToTopic("alerte/batterie");
            mqttManager.subscribeToTopic("alerte/niveauRemplissage");
        }).start();
    }
    public void navigateToHome(String user) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("USERNAME_KEY", user);
        startActivity(intent);
        finish();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}