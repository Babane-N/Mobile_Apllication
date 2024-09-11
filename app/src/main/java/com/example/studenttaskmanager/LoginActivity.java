package com.example.studenttaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Button loginBtn;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.editTextText_username);
        password = findViewById(R.id.editTextTextPassword);
        loginBtn = findViewById(R.id.button_login);
        db = new DatabaseHelper(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (user.equalsIgnoreCase("admin") && pass.equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else if (user.equalsIgnoreCase("instructor") && pass.equalsIgnoreCase("instructor")) {
                    Intent intent = new Intent(LoginActivity.this, InstructorActivity.class);
                    startActivity(intent);
                } else if (user.equalsIgnoreCase("student") && pass.equalsIgnoreCase("student")) {
                    Intent intent = new Intent(LoginActivity.this, StudentActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
