package com.example.diplomast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;

public class MainActivity extends AppCompatActivity {
    APIinterface api; String separator;
    Button ClientBtn, SpecialistBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        api = APIclient.start().create(APIinterface.class);
        ClientBtn = findViewById(R.id.client_btn);
        SpecialistBtn = findViewById(R.id.specialist_btn);
    }

    public void ClientOnClick(View view) {
        separator = "Клиент";
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        intent.putExtra("KEY", separator);
        startActivity(intent);
    }

    public void SpecialistOnClick(View view) {
        separator = "Специалист";
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        intent.putExtra("KEY", separator);
        startActivity(intent);
    }
}