package com.example.diplomast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;
import com.google.gson.Gson;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {
    APIinterface api; String blank_id;
    Boolean i = true; String b;
    String separatorr;
    EditText LoginBox, PasswordBox, PasswordBox2;
    Button TopBtn, BottomBtn;
    LinearLayout RegLayout2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        separatorr = getIntent().getStringExtra("KEY"); // Получаем сепаратор из предыдущего окна
        api = APIclient.start().create(APIinterface.class);
        LoginBox = findViewById(R.id.login_box);
        PasswordBox = findViewById(R.id.password_box);
        PasswordBox2 = findViewById(R.id.password_box2);
        TopBtn = findViewById(R.id.top_btn);
        BottomBtn = findViewById(R.id.bottom_btn);
        RegLayout2 = findViewById(R.id.reg_layout2);

        loadSavedCredentials();

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        b = prefs.getString("login", "nope");
        if (b != "nope"){
            Authorization(LoginBox.getText().toString(), PasswordBox.getText().toString());
        }
    }

    public void TopOnClick(View view) {
        String log = String.valueOf(LoginBox.getText());
        String pas = String.valueOf(PasswordBox.getText());
        String pas2 = String.valueOf(PasswordBox2.getText());

        if (i == true){
            if (log.length() < 4){
                Toast.makeText(getApplicationContext(), "Слишком короткий логин!", Toast.LENGTH_SHORT).show();
            } else if (pas.length() < 8){
                Toast.makeText(getApplicationContext(), "Слишком короткий пароль!", Toast.LENGTH_SHORT).show();
            } else if (!pas.equals(pas2)){
                Toast.makeText(getApplicationContext(), "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
            } else {
                Registration(log, pas);
            }
        } else if (i == false){
            Authorization(log, pas);
        }
    }

    public void BottomOnClick(View view) {
        if (i == true){
            RegLayout2.setVisibility(View.GONE);
            TopBtn.setText("Войти");
            BottomBtn.setText("Зарегистрироваться");
            i = false;
        } else if (i == false){
            RegLayout2.setVisibility(View.VISIBLE);
            TopBtn.setText("Зарегистрироваться");
            BottomBtn.setText("Войти");
            i = true;
        }
    }

    private void Registration(String login, String password){
        if ("Клиент".equals(separatorr)){
            Client newclient = new Client();
            newclient.login = login;
            newclient.password = password;
            Intent intent0 = new Intent(getApplicationContext(), ClientEditActivity.class);
            intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent0.putExtra("ActiveClient", (Serializable) newclient);
            intent0.putExtra("KEY", separatorr);
            startActivity(intent0);

            Gson gson = new Gson();
            String clientJson = gson.toJson(newclient);

            // Save to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("tempUser", clientJson);
            editor.apply();
        } else if ("Специалист".equals(separatorr)){
            Specialist newspecialist = new Specialist();
            newspecialist.login = login;
            newspecialist.password = password;
            newspecialist.status = "3";
            Intent intent = new Intent(getApplicationContext(), SpecialistEditActivity.class);
            intent.putExtra("ActiveSpecialist", (Serializable) newspecialist);
            intent.putExtra("KEY", separatorr);
            startActivity(intent);

            Gson gson = new Gson();
            String specialistJson = gson.toJson(newspecialist);

            // Save to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("tempUser", specialistJson);
            editor.apply();
        }
        saveCredentials(login, password);

    }

    private void Authorization(String login, String password){
        if ("Клиент".equals(separatorr)){
            Call<Client> call = api.getClientByLoginAndPassword(login, password);
            call.enqueue(new Callback<Client>() {
                @Override
                public void onResponse(Call<Client> call, Response<Client> response) {
                    if (response.isSuccessful()){
                        Client tempclient = response.body();
                        Intent intent = new Intent(getApplicationContext(), ClientProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("ActiveClient", (Serializable) tempclient);
                        intent.putExtra("KEY", separatorr);
                        startActivity(intent);

                        // Convert Client to JSON string
                        Gson gson = new Gson();
                        String clientJson = gson.toJson(tempclient);

                        // Save to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("tempUser", clientJson);
                        editor.apply();
                    } else {
                        Toast.makeText(getApplicationContext(), "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                        Log.d("BAD RESPONSE", String.valueOf(response.body()));
                    }
                }
                @Override
                public void onFailure(Call<Client> call, Throwable t) {
                    Log.d("FAILURE", String.valueOf(t));
                }
            });
        } else if ("Специалист".equals(separatorr)){
            Call<Specialist> call = api.getSpecialistByLoginAndPassword(login, password);
            call.enqueue(new Callback<Specialist>() {
                @Override
                public void onResponse(Call<Specialist> call, Response<Specialist> response) {
                    if (response.isSuccessful()){
                        Specialist specialist = response.body();
                        SpecNavigation(specialist);

                        // Convert Specialist to JSON string
                        Gson gson = new Gson();
                        String specialistJson = gson.toJson(specialist);

                        // Save to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("tempUser", specialistJson);
                        editor.apply();
                    }else {
                        Toast.makeText(getApplicationContext(), "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                        Log.d("BAD RESPONSE", String.valueOf(response.body()));
                    }
                }
                @Override
                public void onFailure(Call<Specialist> call, Throwable t) {
                    Log.d("FAILURE", String.valueOf(t));
                }
            });
        }
        saveCredentials(login, password);

    }

    private void SpecNavigation(Specialist specialist){
        if ("0".equals(specialist.status)){
            //ЗАЯВКА_ОТКЛОНЕНА
            blank_id = "0";
            Intent intent1 = new Intent(getApplicationContext(), BlankActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("blank_id", blank_id);
            intent1.putExtra("ActiveSpecialist", (Serializable) specialist);
            startActivity(intent1);
        } else if ("3".equals(specialist.status)) {
            //ЗАЯВКА_НА_РАССМОТРЕНИИ
            blank_id = "3";
            Intent intent2 = new Intent(getApplicationContext(), BlankActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.putExtra("blank_id", blank_id);
            startActivity(intent2);
        } else if ("4".equals(specialist.status)) {
            //ПРОФИЛЬ_ЗАБЛОКИРОВАН
            blank_id = "4";
            Intent intent3 = new Intent(getApplicationContext(), BlankActivity.class);
            intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent3.putExtra("blank_id", blank_id);
            startActivity(intent3);
        }else {
            Intent intent4 = new Intent(getApplicationContext(), SpecialistProfileActivity.class);
            intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent4.putExtra("ActiveSpecialist", (Serializable) specialist);
            intent4.putExtra("KEY", separatorr);
            startActivity(intent4);
        }
    }

    private void saveCredentials(String login, String password) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("login", login);
        editor.putString("password", password);
        editor.apply();
    }
    private void loadSavedCredentials() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedLogin = prefs.getString("login", "");
        String savedPassword = prefs.getString("password", "");
        LoginBox.setText(savedLogin);
        PasswordBox.setText(savedPassword);
        RegLayout2.setVisibility(View.GONE);
        TopBtn.setText("Войти");
        BottomBtn.setText("Зарегистрироваться");
        i = false;
    }

    private void closeActivity() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }
}