package com.example.studenttaskmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class UpdateDeleteActivity extends AppCompatActivity {

    EditText editTextTaskName, editTextDueDate;
    Spinner moduleSpinner;
    Button buttonUpdateTask, buttonDeleteTask;
    int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);

        editTextTaskName = findViewById(R.id.editTextTaskName);
        editTextDueDate = findViewById(R.id.editTextDueDate);
        moduleSpinner = findViewById(R.id.moduleSelectionSpinner);
        buttonUpdateTask = findViewById(R.id.buttonUpdateTask);
        buttonDeleteTask = findViewById(R.id.buttonDeleteTask);

        Intent intent = getIntent();
        taskId = intent.getIntExtra("taskID", -1);

        if (taskId != -1) {
            new LoadTaskDetailsTask().execute(taskId);
            new PopulateModuleSpinnerTask().execute();
        }

        buttonUpdateTask.setOnClickListener(v -> updateTask());
        buttonDeleteTask.setOnClickListener(v -> deleteTask());
    }

    @SuppressLint("Range")
    private void updateTask() {
        String taskName = editTextTaskName.getText().toString().trim();
        String dueDate = editTextDueDate.getText().toString().trim();
        String selectedModuleName = moduleSpinner.getSelectedItem().toString();
        int moduleId = getModuleId(selectedModuleName);

        if (taskName.isEmpty() || dueDate.isEmpty() || moduleId == -1) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new UpdateTaskTask(taskId, taskName, dueDate, moduleId).execute();
    }

    private void deleteTask() {
        new DeleteTaskTask(taskId).execute();
    }

    @SuppressLint("Range")
    private int getModuleId(String moduleName) {
        int moduleId = -1;

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT moduleID FROM modules WHERE moduleName = ?", new String[]{moduleName});

        if (cursor.moveToFirst()) {
            moduleId = cursor.getInt(cursor.getColumnIndex("moduleID"));
        }

        cursor.close();
        db.close();
        return moduleId;
    }

    @SuppressLint("Range")
    private class LoadTaskDetailsTask extends AsyncTask<Integer, Void, Void> {
        String taskName, dueDate, moduleName;

        @Override
        protected Void doInBackground(Integer... params) {
            int taskId = params[0];
            DatabaseHelper dbHelper = new DatabaseHelper(UpdateDeleteActivity.this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT t.taskName, t.dueDate, m.moduleName FROM tasks t " +
                    "JOIN modules m ON t.moduleID = m.moduleID WHERE t.taskID = ?", new String[]{String.valueOf(taskId)});

            if (cursor.moveToFirst()) {
                taskName = cursor.getString(cursor.getColumnIndex("taskName"));
                dueDate = cursor.getString(cursor.getColumnIndex("dueDate"));
                moduleName = cursor.getString(cursor.getColumnIndex("moduleName"));
            }

            cursor.close();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            editTextTaskName.setText(taskName);
            editTextDueDate.setText(dueDate);

            // Wait for module spinner to be populated before setting the selection
            moduleSpinner.post(() -> {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) moduleSpinner.getAdapter();
                int position = adapter.getPosition(moduleName);
                moduleSpinner.setSelection(position);
            });
        }
    }

    private class PopulateModuleSpinnerTask extends AsyncTask<Void, Void, List<String>> {
        @SuppressLint("Range")
        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> moduleNames = new ArrayList<>();
            DatabaseHelper dbHelper = new DatabaseHelper(UpdateDeleteActivity.this);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateDeleteActivity.this, android.R.layout.simple_spinner_item, moduleNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            moduleSpinner.setAdapter(adapter);
        }
    }

    private class UpdateTaskTask extends AsyncTask<Void, Void, Boolean> {
        private final int taskId;
        private final String taskName;
        private final String dueDate;
        private final int moduleId;

        UpdateTaskTask(int taskId, String taskName, String dueDate, int moduleId) {
            this.taskId = taskId;
            this.taskName = taskName;
            this.dueDate = dueDate;
            this.moduleId = moduleId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(UpdateDeleteActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String sql = "UPDATE tasks SET taskName = ?, dueDate = ?, moduleID = ? WHERE taskID = ?";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, taskName);
                statement.bindString(2, dueDate);
                statement.bindLong(3, moduleId);
                statement.bindLong(4, taskId);
                statement.execute();
                db.close();
                return true;
            } catch (Exception e) {
                Log.e("UpdateError", "Error updating task", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(UpdateDeleteActivity.this, "Task updated", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(UpdateDeleteActivity.this, "Task update failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class DeleteTaskTask extends AsyncTask<Void, Void, Boolean> {
        private final int taskId;

        DeleteTaskTask(int taskId) {
            this.taskId = taskId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(UpdateDeleteActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String sql = "DELETE FROM tasks WHERE taskID = ?";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindLong(1, taskId);
                statement.execute();
                db.close();
                return true;
            } catch (Exception e) {
                Log.e("DeleteError", "Error deleting task", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(UpdateDeleteActivity.this, "Task deleted", Toast.LENGTH_LONG).show();
                finish(); // Close the activity after deletion
            } else {
                Toast.makeText(UpdateDeleteActivity.this, "Task deletion failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}

