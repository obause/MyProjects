package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;



public class TaskListActivity extends AppCompatActivity {

    ListView taskListView;

    public static ArrayList<String> taskItems = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

    protected void readTasks (DBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks", null);

        int nameIndex = cursor.getColumnIndex("name");
        while (cursor.moveToNext()) {
            Log.i("Aufgabe", cursor.getString(nameIndex));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        taskListView = findViewById(R.id.taskListView);

        DBHelper dbHelper = new DBHelper(this);

        readTasks(dbHelper);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, taskItems);
        taskListView.setAdapter(arrayAdapter);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), TaskListActivity.class);
                intent.putExtra("projectID", i);
                startActivity(intent);
            }

        });
    }


        public void createTask(View view) {
            Intent intent = new Intent(TaskListActivity.this, AddTask.class);
            startActivity(intent);
        }
}