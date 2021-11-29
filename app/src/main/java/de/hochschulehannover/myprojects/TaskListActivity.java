package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TaskListActivity extends AppCompatActivity {

    ListView taskListView;

    public static ArrayList<String> taskItems = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;
    static Integer projectId;
    public static Map<String, Integer> map = new HashMap<String, Integer>();

    public static void readTasks (DBHelper dbHelper) {
        taskItems.clear();
        map.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE projectId = " + projectId, null);

        int nameIndex = cursor.getColumnIndex("name");
        int idIndex = cursor.getColumnIndex("id");
        while (cursor.moveToNext()) {
            taskItems.add(cursor.getString(nameIndex));
            map.put(cursor.getString(nameIndex), cursor.getInt(idIndex));
        }
    }

    public void deleteProject() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        new android.app.AlertDialog.Builder(TaskListActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Projekt löschen")
                .setMessage("Möchtest du dieses Projekt wirklich löschen?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.delete("projects", "id = ?", new String[]{projectId.toString()});
                        db.delete("tasks", "projectId = ?", new String[]{projectId.toString()});
                        ProjectListActivity.readProjects(dbHelper);
                        ProjectListActivity.arrayAdapter.notifyDataSetChanged();
                        Toast.makeText(TaskListActivity.this, "Projekt wurde gelöscht!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Nein", null)
                .show();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        TextView projectIDTextView = findViewById(R.id.projectIDTextView);
        Bundle extras = getIntent().getExtras();
        projectId = extras.getInt("projectID");
        projectIDTextView.setText("Projektnr.: " + projectId.toString());

        taskListView = findViewById(R.id.taskListView);

        DBHelper dbHelper = new DBHelper(this);
        taskItems.clear();
        readTasks(dbHelper);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, taskItems);
        taskListView.setAdapter(arrayAdapter);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Integer taskId = map.get(taskListView.getItemAtPosition(i).toString());
                Intent intent = new Intent(getApplicationContext(), AddTask.class);
                intent.putExtra("taskID", taskId);
                startActivity(intent);
            }
        });
    }

        public void createTask(View view) {
            Intent intent = new Intent(TaskListActivity.this, AddTask.class);
            intent.putExtra("projectID", projectId);
            startActivity(intent);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        if (item.getItemId()==R.id.info){
            goToInfo();
        }
        if (item.getItemId() == R.id.deleteProject) {
            deleteProject();
        }
        return true;
    }
    public void goToInfo() {
        Intent intent = new Intent(TaskListActivity.this, InfosActivity.class);
        startActivity(intent);
    }

    public void weiter(View view) {
        Intent intent = new Intent(getApplicationContext(), TasksByStatus.class);
        intent.putExtra("projectID",projectId);
        startActivity(intent);
    }
}