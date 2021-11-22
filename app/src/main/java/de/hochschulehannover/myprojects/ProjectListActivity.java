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
import java.util.HashMap;
import java.util.Map;

public class ProjectListActivity extends AppCompatActivity {

    ListView projectListView;

    public static ArrayList<String> projectItems = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;
    static Map<String, Integer> map = new HashMap<String, Integer>();

    public static void readProjects (DBHelper dbHelper) {
        projectItems.clear();
        map.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name FROM projects", null);

        int nameIndex = cursor.getColumnIndex("name");
        int idIndex = cursor.getColumnIndex("id");
        while (cursor.moveToNext()) {
            projectItems.add(cursor.getString(nameIndex));
            map.put(cursor.getString(nameIndex), cursor.getInt(idIndex));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        projectListView = findViewById(R.id.projectListView);

        DBHelper dbHelper = new DBHelper(this);

        projectItems.clear();
        map.clear();

        readProjects(dbHelper);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, projectItems);
        projectListView.setAdapter(arrayAdapter);

        projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Integer projectID = map.get(projectListView.getItemAtPosition(i).toString());
                Intent intent = new Intent(getApplicationContext(), TaskListActivity.class);
                intent.putExtra("projectID",projectID);
                startActivity(intent);
            }
        });

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
        return true;
    }
    public void goToInfo() {
        Intent intent = new Intent(ProjectListActivity.this, InfosActivity.class);
        startActivity(intent);
    }

    public void createProject(View view) {
        Intent intent = new Intent(ProjectListActivity.this, AddProject.class);
        startActivity(intent);
    }
}