package com.example.studenttaskmanager;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {

    ListView taskRecords;
    ArrayList<String> tasks = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Adjust window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        taskRecords = findViewById(R.id.taskRecordList);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        taskRecords.setAdapter(arrayAdapter);

        // Load tasks from database in the background
        new LoadTasksFromDatabaseTask().execute();

        taskRecords.setOnItemClickListener((parent, view, position, id) -> {
            // Extract task ID from the selected item string
            String taskInfo = tasks.get(position);
            String taskID = taskInfo.split("\n")[0].split(": ")[1];
            Intent intent = new Intent(StudentActivity.this, UpdateDeleteActivity.class);
            intent.putExtra("taskID", Integer.parseInt(taskID));
            startActivity(intent);
        });

    }

    @SuppressLint("Range")
    private void loadTasksFromDatabase() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT t.taskID, t.taskName, t.dueDate, m.moduleName FROM tasks t " +
                    "JOIN modules m ON t.moduleID = m.moduleID", null);

            if (cursor.moveToFirst()) {
                do {
                    String taskID = cursor.getString(cursor.getColumnIndex("taskID"));
                    String taskName = cursor.getString(cursor.getColumnIndex("taskName"));
                    String dueDate = cursor.getString(cursor.getColumnIndex("dueDate"));
                    String moduleName = cursor.getString(cursor.getColumnIndex("moduleName"));

                    String taskInfo = "ID: " + taskID + "\nName: " + taskName + "\nDue Date: " + dueDate + "\nModule: " + moduleName;
                    tasks.add(taskInfo);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading tasks from database", e);
            Toast.makeText(this, "Failed to load tasks", Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    private class LoadTasksFromDatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            loadTasksFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
