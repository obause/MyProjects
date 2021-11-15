package de.hochschulehannover.myprojects;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

public class TaskListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
    }

    protected void readTasks (DBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks", null);

        int nameIndex = cursor.getColumnIndex("name");
        while (cursor.moveToNext()) {
            Log.i("Aufgabe", cursor.getString(nameIndex));
        }
    }
}