package com.example.studenttaskmanager;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.List;

public class InstructorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText taskNamed;
    EditText taskDueDate;
    Spinner moduleSpinner;
    Button createTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor);

        taskNamed = findViewById(R.id.editTextText_task);
        taskDueDate = findViewById(R.id.editTextText_dueDate);
        moduleSpinner = findViewById(R.id.moduleSelectionSpinner);
        createTask = findViewById(R.id.createTask_button);

        moduleSpinner.setOnItemSelectedListener(this);

        // Populate spinner in the background
        new PopulateModuleSpinnerTask().execute();

        createTask.setOnClickListener(v -> insertTask());
    }

    private void insertTask() {
        String tName = taskNamed.getText().toString().trim();
        String taskDue = taskDueDate.getText().toString().trim();
        int moduleID = getSelectedModuleID();

        if (tName.isEmpty() || taskDue.isEmpty() || moduleID == -1) {
            Toast.makeText(this, "Please enter the task name, due date, and select a module", Toast.LENGTH_LONG).show();
            return;
        }

        new InsertTaskTask(tName, taskDue, moduleID).execute();
    }

    @SuppressLint("Range")
    private void populateModuleSpinner() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT moduleName FROM modules", null);

        List<String> moduleNames = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                moduleNames.add(cursor.getString(cursor.getColumnIndex("moduleName")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moduleNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moduleSpinner.setAdapter(adapter);
    }

    @SuppressLint("Range")
    private int getSelectedModuleID() {
        String selectedModuleName = moduleSpinner.getSelectedItem().toString();
        int moduleID = -1;

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT moduleID FROM modules WHERE moduleName = ?", new String[]{selectedModuleName});

        if (cursor.moveToFirst()) {
            moduleID = cursor.getInt(cursor.getColumnIndex("moduleID"));
        }
        cursor.close();
        db.close();

        return moduleID;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String myModuleSelectedText = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), myModuleSelectedText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private class PopulateModuleSpinnerTask extends AsyncTask<Void, Void, List<String>> {
        @SuppressLint("Range")
        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> moduleNames = new ArrayList<>();
            DatabaseHelper dbHelper = new DatabaseHelper(InstructorActivity.this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT moduleName FROM modules", null);

            if (cursor.moveToFirst()) {
                do {
                    moduleNames.add(cursor.getString(cursor.getColumnIndex("moduleName")));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return moduleNames;
        }

        @Override
        protected void onPostExecute(List<String> moduleNames) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(InstructorActivity.this, android.R.layout.simple_spinner_item, moduleNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            moduleSpinner.setAdapter(adapter);
        }
    }

    private class InsertTaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String tName;
        private final String taskDue;
        private final int moduleID;

        InsertTaskTask(String tName, String taskDue, int moduleID) {
            this.tName = tName;
            this.taskDue = taskDue;
            this.moduleID = moduleID;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(InstructorActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String sql = "INSERT INTO tasks (taskName, dueDate, moduleID) VALUES (?, ?, ?)";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, tName);
                statement.bindString(2, taskDue);
                statement.bindLong(3, moduleID);
                statement.execute();
                db.close();
                return true;
            } catch (Exception e) {
                Log.e("InsertError", "Error inserting task record", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(InstructorActivity.this, "Task Record created", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(InstructorActivity.this, "Task Record creation failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}