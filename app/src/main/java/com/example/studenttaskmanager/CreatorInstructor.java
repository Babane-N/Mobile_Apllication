package com.example.studenttaskmanager;

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

public class CreatorInstructor extends AppCompatActivity {


    EditText instructorTitle;
    EditText instructorName;
    EditText instructorSurname;
    Button createInstructor, Back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creator_instructor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        instructorTitle = findViewById(R.id.editInstructor_Title);
        instructorName = findViewById(R.id.editInstructor_FirstName);
        instructorSurname = findViewById(R.id.editInstructor_SecondName);

        createInstructor = findViewById(R.id.btnCreateInstructor);
        Back = findViewById(R.id.btnInstructorBack);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CreatorInstructor.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        createInstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                insertInstructor();
            }
        });
    }

    public void insertInstructor() {

        try {
            String iTitle = instructorTitle.getText().toString();
            String iName = instructorName.getText().toString().trim();
            String iSurname = instructorSurname.getText().toString().trim();

            if (iName.isEmpty() || iSurname.isEmpty()) {
                Toast.makeText(this, "Please enter both name and surname", Toast.LENGTH_LONG).show();
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "INSERT INTO instructors (instructorTitle, instructorName, instructorSurname) VALUES (?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, iTitle);
            statement.bindString(2, iName);
            statement.bindString(3, iSurname);

            statement.execute();
            db.close();

            Toast.makeText(this, "Instructor Record created", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Instructor Record creation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("InsertError", "Error inserting instructor record", e);
        }
    }

}