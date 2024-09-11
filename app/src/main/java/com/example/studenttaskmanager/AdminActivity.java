package com.example.studenttaskmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity{
    Button adCreateStudent, adCreateInstructor, adCreateModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        adCreateStudent = findViewById(R.id.btnAdminCreateStudent);
        adCreateInstructor = findViewById(R.id.btnAdminCreateInstructor);
        adCreateModule = findViewById(R.id.btnAdminCreateModule);

        adCreateStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminActivity.this, CreateStudent.class);
                startActivity(intent);
            }
        });

        adCreateInstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminActivity.this, CreatorInstructor.class);
                startActivity(intent);
            }
        });

        adCreateModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminActivity.this, CreateModule.class);
                startActivity(intent);
            }
        });
    }

}