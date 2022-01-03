package de.hochschulehannover.myprojects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import de.hochschulehannover.myprojects.adapter.TaskListAdapter;
import de.hochschulehannover.myprojects.adapter.TaskPagerAdapter;
import de.hochschulehannover.myprojects.databinding.ActivityTaskListBinding;
import de.hochschulehannover.myprojects.databinding.ActivityTasksByStatusBinding;
import de.hochschulehannover.myprojects.firebase.FirestoreClass;
import de.hochschulehannover.myprojects.model.Project;
import de.hochschulehannover.myprojects.model.Task;
import de.hochschulehannover.myprojects.model.TaskList;
import de.hochschulehannover.myprojects.utils.Constants;

/**
 * <h2>Activity TaskListActivity</h2>
 * <p>Liste der Aufgaben eines Projekts. Diese erbt von {@link BaseActivity}.
 *
 * Die Activity beinhaltet ein TabLayout. Es gibt drei Tabs die jeweils die Aufgaben mit dem entsprechenden
 * Status anzeigen (Backlog, In Arbeit, Fertig).
 * Die Tabs werden jeweils über einen ViewPager mit der Adapterklasse {@link TaskPagerAdapter} mit einem Fragment
 * der Klasse {@link TaskListContentFragment} verbunden. Die Inhalte in den Fragments sind wiederum über
 * einen RecyclerView mit dem Adapter {@link TaskListAdapter} umgesetzt.
 * </p>
 * <p>
 *  <b>Autor: Ole</b>
 * </p>
 */

public class TaskListActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView taskRecyclerView;
    private Bundle bundle;

    private TaskListContentFragment backlogFragment;
    private TaskListContentFragment inProgressFragment;
    private TaskListContentFragment doneFragment;

    private String projectDocumentId;
    private String userName;
    private Project projectDetails;

    public static final int CREATE_TASK_REQUEST_CODE = 1;
    private static final String TAG = "TaskListActivity";

    //private ActivityTasksByStatusBinding binding;
    private ActivityTaskListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        binding = ActivityTaskListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

         */

        FloatingActionButton fab = binding.fab;

        if (getIntent().hasExtra(Constants.DOCUMENT_ID)) {
            projectDocumentId = getIntent().getStringExtra(Constants.DOCUMENT_ID);
        }
        if (getIntent().hasExtra(Constants.NAME)) {
            userName = getIntent().getStringExtra(Constants.NAME);
        }

        // Neue Aufgabe erstellen mit startActivityForResult, um danach die Listen zu aktualisieren
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskListActivity.this, AddTask.class);
                intent.putExtra(Constants.DOCUMENT_ID, projectDocumentId);
                intent.putExtra(Constants.NAME, userName);
                startActivityForResult(intent, CREATE_TASK_REQUEST_CODE);
            }
        });

        //toolbar = findViewById(R.id.taskListToolbar);
        toolbar = binding.taskListToolbar;
        //taskRecyclerView = findViewById(R.id.taskListRecyclerView);

        showDialog("Lade Projektdaten");
        // Projektdaten und Aufgabenlisten abrufen
        new FirestoreClass().getProjectDetails(this, projectDocumentId);

        bundle = new Bundle();
        bundle.putParcelable("project", projectDetails);

        // Fragment-Objekte erstellen
        backlogFragment = new TaskListContentFragment(Constants.BACKLOG, 0, projectDetails);
        backlogFragment.setArguments(bundle);
        inProgressFragment = new TaskListContentFragment(Constants.PROGRESS, 1, projectDetails);
        inProgressFragment.setArguments(bundle);
        doneFragment = new TaskListContentFragment(Constants.DONE, 2, projectDetails);
        doneFragment.setArguments(bundle);

        // Fragments über den TaskPagerAdapter mit den Tabs verbinden
        TaskPagerAdapter taskPagerAdapter = new TaskPagerAdapter(this, getSupportFragmentManager());
        taskPagerAdapter.addFragment(backlogFragment, getString(R.string.backlog));
        taskPagerAdapter.addFragment(inProgressFragment, getString(R.string.in_progress));
        taskPagerAdapter.addFragment(doneFragment, getString(R.string.done));
        //taskPagerAdapter.addFragment(new BacklogFragment("Done"), "Done");

        ViewPager viewPager = binding.viewPager;
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(taskPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
    }

    /**
     * Abgerufene Projekt-Details verarbeiten. Das Projekt-Objekt wird von der {@link FirestoreClass} übergeben.
     * Zusätzlich werden die drei Aufgabenlisten an die entsprechenden Fragments übergeben, damit dort
     * die Listen mit den Aufgaben mithilfe des entsprechenden RecyclerViews erstellt werden können.
     * @param project
     */
    public void getProjectDetails(Project project) {

        projectDetails = project;

        hideDialog();
        // Projektname als Titel in der Actionbar
        setupActionBar(project.name);

        FragmentManager fm = getSupportFragmentManager();
        //BacklogFragment fragment = fm.findFragmentById(R.id.fr)
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        Log.i("Fragments:", allFragments.toString());

        Log.i(TAG, "TaskList:" + project.taskList.toString());

        // Aufgabenlisten im UI entsprechend den Tabs generieren
        backlogFragment.setupRecycler(project.taskList.get(Constants.BACKLOG_INDEX), project);
        Log.i(TAG, "Recycler für Backlog aufgesetzt");
        inProgressFragment.setupRecycler(project.taskList.get(Constants.PROGRESS_INDEX), project);
        Log.i(TAG, "Recycler für in Progress aufgesetzt");
        doneFragment.setupRecycler(project.taskList.get(Constants.DONE_INDEX), project);
        Log.i(TAG, "Recycler für Done aufgesetzt");
        //doneFragment.test();

        /*taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setHasFixedSize(true);

        TaskListAdapter adapter = new TaskListAdapter(this, project.taskList);
        taskRecyclerView.setAdapter(adapter);*/
    }

    /**
     * Diese Methode wird von der {@link FirestoreClass} aufgerufen wenn die Aufgabenlisten im
     * Firestore erfolgreich aktualisiert werden konnten. Danach werden die ProjektDetails
     * und Aufgabenlisten im UI aktualisiert
     */
    public void updateStatusListSuccess() {
        hideDialog();
        showInfoSnackBar("Aufgabenliste erfolgreich aktualisiert!");
        showDialog("Lade Daten...");
        new FirestoreClass().getProjectDetails(this, projectDetails.documentId);
    }

    /**
     * Diese Methode wird von der {@link FirestoreClass} aufgerufen wenn das Projekt erfolgreich
     * gelöscht werden konnte. Die Activity wird nach Löschen des Projekts geschlossen.
     */
    public void projectDeletedSuccessfully() {
        hideDialog();
        showInfoSnackBar("Projekt "+ projectDetails.name + "erfolgreich gelöscht");
        finish();
    }

    /**
     * Attribut projectDetails vom Datentyp Projekt abrufen
     * @return
     */
    public Project getProject() {
        return projectDetails;
    }

    // Weitere Aufgabenliste(ähnlich wie Backlog etc.) vom Nutzer erstellen. Wurde nicht mehr umgesetzt
    // Es kann nur die drei Standard-Aufgabenlisten geben, ein Nutzer kann keine eigenen Listen erstellen.
    public void createTaskList(String taskListName) {
        TaskList taskList = new TaskList(taskListName, new FirestoreClass().getUserId());

        projectDetails.taskList.add(0, taskList);

        showDialog("Erstelle Liste...");
        new FirestoreClass().updateTaskList(this, projectDetails);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_members, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.membersMenuItem) {
            Intent intent = new Intent(this, ProjectMembersActivity.class);
            intent.putExtra(Constants.PROJECT_DETAILS, projectDetails);
            startActivity(intent);
        }
        if (item.getItemId()==R.id.editProjectMenuItem){
            // TODO
            showErrorSnackBar("Noch nicht verfügbar");
        }
        if (item.getItemId() == R.id.deleteProjectMenuItem) {
            new android.app.AlertDialog.Builder(TaskListActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Projekt löschen")
                    .setMessage("Möchtest du dieses Projekt wirklich löschen?")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showDialog("Lösche Projekt...");
                            new FirestoreClass().deleteProject(TaskListActivity.this, projectDocumentId);
                        }
                    })
                    .setNegativeButton("Nein", null)
                    .show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            new FirestoreClass().getProjectDetails(this, projectDetails.documentId);
        }
        if (resultCode == RESULT_OK && requestCode == CREATE_TASK_REQUEST_CODE) {
            new FirestoreClass().getProjectDetails(this, projectDetails.documentId);
            showInfoSnackBar("Aufgabe erfolgreich erstellt");
        } else {
            Log.e("TaskListActivity","Aufgabenerstellung abgebrochen");
        }
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