package com.example.studenttaskmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "student_task_manager";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE students (studentID INTEGER PRIMARY KEY AUTOINCREMENT, studentName TEXT, studentSurname TEXT)");
        db.execSQL("CREATE TABLE modules (moduleID INTEGER PRIMARY KEY AUTOINCREMENT, moduleName TEXT,duration TEXT)");
        db.execSQL("CREATE TABLE tasks (taskID INTEGER PRIMARY KEY AUTOINCREMENT, taskName TEXT, dueDate TEXT, moduleID INTEGER, FOREIGN KEY(moduleID) REFERENCES modules(moduleID))");
        db.execSQL("CREATE TABLE instructors (instructorID INTEGER PRIMARY KEY AUTOINCREMENT, instructorTitle TEXT, instructorName TEXT, instructorSurname TEXT)");
        db.execSQL("CREATE TABLE admins (adminID INTEGER PRIMARY KEY AUTOINCREMENT, adminName TEXT, adminSurname TEXT)");
        db.execSQL("CREATE TABLE login_credentials (username TEXT PRIMARY KEY, password TEXT, role TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS students");
        db.execSQL("DROP TABLE IF EXISTS modules");
        db.execSQL("DROP TABLE IF EXISTS tasks");
        db.execSQL("DROP TABLE IF EXISTS instructors");
        db.execSQL("DROP TABLE IF EXISTS admins");
        db.execSQL("DROP TABLE IF EXISTS login_credentials");
        onCreate(db);
    }
}
