package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import de.hochschulehannover.myprojects.adapter.TaskListAdapter;
import de.hochschulehannover.myprojects.firebase.FirestoreClass;
import de.hochschulehannover.myprojects.model.Project;
import de.hochschulehannover.myprojects.model.Task;
import de.hochschulehannover.myprojects.utils.Constants;


public class TaskListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView taskRecyclerView;

    private String projectDocumentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        toolbar = findViewById(R.id.taskListToolbar);
        taskRecyclerView = findViewById(R.id.taskListRecyclerView);

        if (getIntent().hasExtra(Constants.DOCUMENT_ID)) {
            projectDocumentId = getIntent().getStringExtra(Constants.DOCUMENT_ID);
        }

        showDialog("Lade Projektdaten");
        new FirestoreClass().getProjectDetails(this, projectDocumentId);

    }

    public void getProjectDetails(Project project) {
        hideDialog();
        setupActionBar(project.name);

        Task task = new Task("Testaufgabe", "Testname", "Hoch", "Abgeschlossen");
        Task task2 = new Task("Zweite Testaufgabe", "Max", "Niedrig", "In Arbeit");
        project.taskList.add(task);

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setHasFixedSize(true);

        TaskListAdapter adapter = new TaskListAdapter(this, project.taskList);
        taskRecyclerView.setAdapter(adapter);
    }

    // Custom ActionBar initialisieren
    private void setupActionBar(String title) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_24dp);
            actionBar.setTitle(title);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}