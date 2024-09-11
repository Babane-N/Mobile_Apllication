package com.example.studenttaskmanager;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateModule extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button createModule, Back;
    Spinner myModuleSpinner;
    EditText duration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_module);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        myModuleSpinner = findViewById(R.id.moduleSelectSpinner);
        ArrayAdapter<CharSequence> myModuleAdapter = ArrayAdapter.createFromResource(this, R.array.moduleNames, android.R.layout.simple_spinner_dropdown_item);
        myModuleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myModuleSpinner.setAdapter(myModuleAdapter);
        myModuleSpinner.setOnItemSelectedListener(this);
        duration = findViewById(R.id.editModule_duration);

        createModule = findViewById(R.id.btnCreateModule);
        Back = findViewById(R.id.btnModule_Back);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CreateModule.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        createModule.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                insertModule();
            }
        });
    }
    public void insertModule(){
        try{
            Spinner moduleSpinner = findViewById(R.id.moduleSelectSpinner);
            String module = moduleSpinner.getSelectedItem().toString().trim();
            String moduleDuration = duration.getText().toString().trim();

            if (moduleDuration.isEmpty())
            {
                Toast.makeText(this, "Please enter the duration", Toast.LENGTH_LONG).show();
                return;
            }

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "INSERT INTO modules (moduleName, duration) VALUES (?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, module);
            statement.bindString(2, moduleDuration);

            statement.execute();
            db.close();

            Toast.makeText(this, "Module Record created", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Module Record creation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("InsertError", "Error inserting module record", e);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String myModuleText = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), myModuleText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}