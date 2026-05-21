package com.example.applicationlogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvWelcome = findViewById(R.id.tv_welcome);
        Button btnStats = findViewById(R.id.btn_stats);
        Button btnLogout = findViewById(R.id.btn_logout);

        String user = getIntent().getStringExtra("USERNAME_KEY");

        if (user != null && !user.isEmpty()) {
            tvWelcome.setText("Bienvenue, " + user + " !");
        } else {
            tvWelcome.setText("Bienvenue, Invité");
        }

        btnStats.setText("Choix Poubelles");
        btnStats.setOnClickListener(v -> {

            Intent intent = new Intent(HomeActivity.this, PoubellesActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "À bientôt !", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}