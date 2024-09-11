package com.example.studenttaskmanager;

import static com.example.studenttaskmanager.R.id.editStudentName;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateStudent extends AppCompatActivity {

    EditText studentName;
    EditText studentSurname;

    Button createStudent, Back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        studentName = findViewById(R.id.editStudentName);
        studentSurname = findViewById(R.id.editStudentSurname);

        createStudent = findViewById(R.id.btnCreateStudent);
        Back = findViewById(R.id.btnSudentBack);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CreateStudent.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        createStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertStudent();
            }
        });
    }
    public void insertStudent()
    {
        try {
            String sName = studentName.getText().toString().trim();
            String sSurname = studentSurname.getText().toString().trim();

            if (sName.isEmpty() || sSurname.isEmpty()) {
                Toast.makeText(this, "Please enter both name and surname", Toast.LENGTH_LONG).show();
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "INSERT INTO students (studentName, studentSurname) VALUES (?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, sName);
            statement.bindString(2, sSurname);

            statement.execute();
            db.close();

            Toast.makeText(this, "Student Record created", Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Student Record creation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("InsertError", "Error inserting student record", e);
        }
    }

}