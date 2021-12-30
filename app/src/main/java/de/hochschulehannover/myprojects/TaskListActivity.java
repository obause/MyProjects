package de.hochschulehannover.myprojects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

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
        new FirestoreClass().getProjectDetails(this, projectDocumentId);

        bundle = new Bundle();
        bundle.putParcelable("project", projectDetails);

        backlogFragment = new TaskListContentFragment(Constants.BACKLOG, 0, projectDetails);
        backlogFragment.setArguments(bundle);
        inProgressFragment = new TaskListContentFragment(Constants.PROGRESS, 1, projectDetails);
        inProgressFragment.setArguments(bundle);
        doneFragment = new TaskListContentFragment(Constants.DONE, 2, projectDetails);
        doneFragment.setArguments(bundle);

        TaskPagerAdapter taskPagerAdapter = new TaskPagerAdapter(this, getSupportFragmentManager());
        taskPagerAdapter.addFragment(backlogFragment, "Backlog");
        taskPagerAdapter.addFragment(inProgressFragment, "In Bearbeitung");
        taskPagerAdapter.addFragment(doneFragment, "Abgeschlossen");
        //taskPagerAdapter.addFragment(new BacklogFragment("Done"), "Done");

        ViewPager viewPager = binding.viewPager;
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(taskPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
    }

    public void getProjectDetails(Project project) {

        projectDetails = project;

        hideDialog();
        setupActionBar(project.name);

        //TaskList taskList = new TaskList("Backlog", project.userId);
        //TaskList taskList2 = new TaskList("Zweite Testaufgabe", "Max", "Niedrig", "In Arbeit");
        //project.taskList.add(taskList);
        //project.taskList.add(taskList2);

        FragmentManager fm = getSupportFragmentManager();
        //BacklogFragment fragment = fm.findFragmentById(R.id.fr)
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        Log.i("Fragments:", allFragments.toString());

        Log.i(TAG, "TaskList:" + project.taskList.toString());

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

    public void updateStatusListSuccess() {
        hideDialog();
        showInfoSnackBar("Aufgabenliste erfolgreich aktualisiert!");
        showDialog("Lade Daten...");
        new FirestoreClass().getProjectDetails(this, projectDetails.documentId);
    }

    public Project getProject() {
        return projectDetails;
    }

    public void createTaskList(String taskListName) {
        TaskList taskList = new TaskList(taskListName, new FirestoreClass().getUserId());

        projectDetails.taskList.add(0, taskList);

        showDialog("Erstelle Listen...");
        new FirestoreClass().updateTaskList(this, projectDetails);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //
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