package com.example.diplomast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diplomast.DTO.Specialist;

import java.io.Serializable;

public class BlankActivity extends AppCompatActivity {
    String blank_id; Specialist specialist;
    String separatorr = "Специалист";
    TextView BlankTitle, BlankContent;
    Button EditBtn, ExitBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blank);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        blank_id = getIntent().getStringExtra("blank_id");
        specialist = (Specialist) getIntent().getSerializableExtra("ActiveSpecialist");
        BlankTitle = findViewById(R.id.blank_title);
        BlankContent = findViewById(R.id.blank_content);
        EditBtn = findViewById(R.id.edit_btn);
        ExitBtn = findViewById(R.id.exit_btn);


        if (blank_id != "0"){
            EditBtn.setVisibility(View.GONE);
        }

        loadBlank(blank_id);
    }

    public void EditOnClick(View view){
        Intent intent = new Intent(getApplicationContext(), SpecialistEditActivity.class);
        intent.putExtra("ActiveSpecialist", (Serializable) specialist);
        intent.putExtra("KEY", separatorr);
        startActivity(intent);
    }

    private void loadBlank(String id){
        if ("3".equals(id)){
            BlankTitle.setText("Аккаунт на рассмотрении");
            BlankContent.setText("Ваш аккаунт находится на рассмотрении. Он станет доступен Вам и клиентам после одобрения администратора.");
        }else if ("4".equals(id)){
            BlankTitle.setText("Аккаунт заблокирован");
            BlankContent.setText("Ваш аккаунт заблокирован.");
        }else if ("0".equals(id)){
            BlankTitle.setText("Аккаунт отклонён");
            EditBtn.setVisibility(View.VISIBLE);
            BlankContent.setText("Ваш аккаунт отклонён администратором. Возможно Вы не добавили необходимые данные или они являются некорректными.");
        }
    }

    public void ExitOnClick(View view) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Очистка всех значений
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Закрыть все предыдущие активити
        startActivity(intent);
    }
}